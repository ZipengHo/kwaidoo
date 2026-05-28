package cell.basic.cmn.event;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.client.util.CmnEvent.CmnRunnable;
import com.leavay.client.util.CmnEvent.EThreadPool;
import com.leavay.common.util.buff.BufferQueues;
import com.leavay.common.util.buff.DataBufferQueue;
import com.leavay.common.util.javac.ClassFactory;
import com.leavay.common.util.serial.StringSerianizedImp;
import com.leavay.dfc.gui.LvUtil;
import com.leavay.nio.crpc.RpcMap;

import bap.cells.BasicServiceCell;
import bap.cells.Cells;
import bap.cells.exception.ClassLoaderConflictException;
import basic.cmn.dto.event.DeadLetterQueueDto;
import basic.cmn.dto.event.EventDefinitionDto;
import basic.cmn.dto.event.EventDto;
import basic.cmn.dto.event.EventOutBoxDto;
import basic.cmn.dto.event.EventProcessingLogDto;
import basic.cmn.dto.event.EventSubscriptionDto;
import basic.cmn.event.EventHandlerInitParameter;
import basic.cmn.event.EventHandlerProgress;
import basic.cmn.event.EventInvokeMode;
import basic.cmn.event.TraceContext;
import cell.CellIntf;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.cmn.IJson;
import cell.cmn.IJsonService;
import cell.cmn.util.IServerConfig;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.dc.cache.ICacheMgr;
import cmn.dto.Progress;
import cmn.util.JsonUtil;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.adur.data.Form;
import gpf.adur.data.ResultSet;
import gpf.dc.basic.i18n.GpfDCBasicI18n;
import gpf.dc.util.DtoConvertUtil;

public class CEventBus extends BasicServiceCell implements IEventBus {

    /**
     * 事件订阅列表
     */
    Map<String, EventSubscriptionDto> eventSubscriptions = new ConcurrentHashMap<>();
    /**
     * 临时事件订阅列表
     */
    Map<String, EventSubscriptionDto> tmpEventSubscriptions = new ConcurrentHashMap<>();
    /**
     * 事件总线线程池，用于处理事件分发
     */
    EThreadPool eventBusPool = new EThreadPool("EventBus Handler Pool", 0, 50, false);

    // 主线程，用于处理异步事件分发
    Thread mainThread;

    private final int MIN_POLL_INTERVAL = 100; // 最小轮询间隔
    private final int MAX_POLL_INTERVAL = 2000; // 最大轮询间隔

    private final String LOG = CEventBus.class.getSimpleName();

    public DataBufferQueue<String> logQueue = null;
    public final static String LogFile = CEventBus.class.getSimpleName();
    /**
     * 日志保留历史时间key
     */
    public final static String CONF_LOG_KEEP_HISTORY_DAY = "EventBus.ExecuteLog.KeepHistoryDay";
    /**
     * 日志保留历史默认天数：5天
     */
    public final static int DFT_LOG_KEEP_HISTORY_DAY = 5;

    @Override
    protected void doStartService() throws Exception {
        // 启动主线程
        try {
            registAllSubscriber();
        } catch (Exception e) {
            Tracer tracer = TraceUtil.getCurrentTracer();
            tracer.error(CEventBus.class.getSimpleName(), ToolUtilities.getFullExceptionStack(e));
        }
        if (mainThread == null) {
            StringSerianizedImp stringSer = new StringSerianizedImp();
            logQueue = BufferQueues.getInstance().getOrGenerateQueueBuffer(LogFile, false, 1024, stringSer);
            mainThread = new Thread("EventBus Main Thread") {
                @Override
                public void run() {
                    // 主线程循环处理事件
                    int queryInterval = 500; // 默认轮询间隔
                    long lastQueryTime = 0L; // 上次查询时间
                    long lastDeleteTime = 0L; // 上次删除时间
                    while (true) {
                        // 注册所有订阅者
                        try{
                            ClassFactory.loadClass(EventOutBoxDto.FormModelId);
                        }catch (Exception e){
                            // 等待下一次轮询
                            ToolUtilities.sleep(5000);
                            continue;
                        }
                        try {
                            registAllSubscriber();
                        } catch (InterruptedException e) {
                            // 处理中断异常
                            Thread.currentThread().interrupt();
                            break;
                        } catch (ClassLoaderConflictException e) {
                            // 处理中断异常
                            Thread.currentThread().interrupt();
                            break;
                        } catch (Exception e) {
                            Tracer tracer = TraceUtil.getCurrentTracer();
                            tracer.error(CEventBus.class.getSimpleName(), ToolUtilities.getFullExceptionStack(e));
                        }

                        //触发发件箱中的事件
                        try {
                            if (System.currentTimeMillis() - lastQueryTime > queryInterval) {
                                // 从发件箱获取事件
                                ResultSet<EventOutBoxDto> eventRs = queryEventOutBoxPage(null,1, Integer.MAX_VALUE);
                                if (eventRs.getDataList().isEmpty()) {
                                    // 没有事件时增加轮询间隔
                                    queryInterval = Math.min(queryInterval + 100, MAX_POLL_INTERVAL);
                                } else {
                                    // 有事件时恢复最小轮询间隔
                                    queryInterval = MIN_POLL_INTERVAL;
                                }
                                for (EventOutBoxDto event : eventRs.getDataList()) {
                                	if( eventBusPool.getRunnable(event.getCode()) != null 
                                			|| eventBusPool.getRunning(event.getCode()) != null) {
                                		//如果事件正在处理就先跳过本次的处理
                                		continue;
                                	}
                                    // 加入异步处理事件队列
                                    eventBusPool.run(new EventRunnable(event));
                                }
                                lastQueryTime = System.currentTimeMillis();
                            }
                        } catch (InterruptedException e) {
                            // 处理中断异常
                            Thread.currentThread().interrupt();
                            break;
                        } catch (ClassLoaderConflictException e) {
                            // 处理中断异常
                            Thread.currentThread().interrupt();
                            break;
                        } catch (Exception e) {
                            Tracer tracer = TraceUtil.getCurrentTracer();
                            tracer.error(CEventBus.class.getSimpleName(), ToolUtilities.getFullExceptionStack(e));
                        }
                        // 每500ms批量保存一次日志
                        try {
                            batchSaveLog();
                        } catch (InterruptedException e) {
                            // 处理中断异常
                            Thread.currentThread().interrupt();
                            break;
                        } catch (ClassLoaderConflictException e) {
                            // 处理中断异常
                            Thread.currentThread().interrupt();
                            break;
                        } catch (Exception e) {
                            Tracer tracer = TraceUtil.getCurrentTracer();
                            tracer.error(CEventBus.class.getSimpleName(), ToolUtilities.getFullExceptionStack(e));
                        }
                        // 每天清理一次过期日志
                        if (System.currentTimeMillis() - lastDeleteTime > 24 * 60 * 60 * 1000L) {
                            try {
                                cleanExpireLog();
                            } catch (InterruptedException e) {
                                // 处理中断异常
                                Thread.currentThread().interrupt();
                                break;
                            } catch (ClassLoaderConflictException e) {
                                // 处理中断异常
                                Thread.currentThread().interrupt();
                                break;
                            } catch (Exception e) {
                                Tracer tracer = TraceUtil.getCurrentTracer();
                                tracer.error(CEventBus.class.getSimpleName(), ToolUtilities.getFullExceptionStack(e));
                            }
                            lastDeleteTime = System.currentTimeMillis();
                        }
                        // 等待下一次轮询
                        ToolUtilities.sleep(500);
                    }
                }
            };
            mainThread.start();
        }
    }

    @Override
    protected void doStopService() {
        if (mainThread != null) {
            mainThread.interrupt();
        }
        eventBusPool.shutdown();
    }

    protected void addLogToQueue(EventProcessingLogDto log) throws Exception {
        try (IJson json = IJsonService.get().getJson()) {
            logQueue.put(json.toJson(log));
        }
    }

    protected void batchSaveLog() throws Exception {
        List<String> list = new ArrayList<>();
        while (list.size() < 100) {
            //没有从队列拿到数据的，直接退出
            String log = logQueue.poll(0, TimeUnit.SECONDS);
            if (log == null)
                break;
            list.add(log);
        }
        if (!CmnUtil.isCollectionEmpty(list)) {
            try (IDao dao = IDaoService.get().newDao(); IJson json = IJsonService.get().getJson()) {
                List<Form> logs = new ArrayList<>();
                for (String log : list) {
                    EventProcessingLogDto dto = json.fromJson(log, EventProcessingLogDto.class);
                    Form form = DtoConvertUtil.convertToForm(dto);
                    logs.add(form);
                }
                if (!logs.isEmpty()) {
                    IFormMgr.get().createForms(dao, logs);
                }
                dao.commit();
            }
        }
    }

    protected void cleanExpireLog() throws Exception {
        long deleteDateMillSeconds = System.currentTimeMillis() - IServerConfig.get().getInteger(CONF_LOG_KEEP_HISTORY_DAY, DFT_LOG_KEEP_HISTORY_DAY) * 24 * 60 * 60 * 1000L;
        try (IDao dao = IDaoService.get().newDao()) {
            Cnd cnd = Cnd.where(new SqlExpressionGroup().andLTE(EventProcessingLogDto.FieldCode_HandleStartTime, deleteDateMillSeconds));
            IFormMgr.get().deleteForm(dao, EventProcessingLogDto.FormModelId, cnd, 500);
            dao.commit();
        }
    }

    public class EventRunnable extends CmnRunnable {

        private Long poolingTime = System.currentTimeMillis();
        private Long startTime;
        private Long endTime;
        private Long cost;
        private EventOutBoxDto eventOutBox;
        // 用于存储当前任务所在的线程
        private Thread currentThread;

        public EventRunnable(EventOutBoxDto event) {
        	super(event.getCode());
            this.eventOutBox = event;
        }

        public Long getPoolingTime() {
            return poolingTime;
        }

        public EventRunnable setPoolingTime(Long poolingTime) {
            this.poolingTime = poolingTime;
            return this;
        }

        public Long getStartTime() {
            return startTime;
        }

        public EventRunnable setStartTime(Long startTime) {
            this.startTime = startTime;
            return this;
        }

        public Long getEndTime() {
            return endTime;
        }

        public EventRunnable setEndTime(Long endTime) {
            this.endTime = endTime;
            return this;
        }

        public Long getCost() {
            return cost;
        }

        public EventRunnable setCost(Long cost) {
            this.cost = cost;
            return this;
        }

        public EventOutBoxDto getEventOutBox() {
            return eventOutBox;
        }

        public EventRunnable setEventOutBox(EventOutBoxDto eventOutBox) {
            this.eventOutBox = eventOutBox;
            return this;
        }

        public Thread getCurrentThread() {
            return currentThread;
        }

        @Override
        public void run() {
            try {
                this.currentThread = Thread.currentThread();
                startTime = System.currentTimeMillis();
                Tracer tracer = TraceUtil.getCurrentTracer();
                EventSubscriptionDto eventSubscriptionDto = getRuntimeEventSubscription(eventOutBox.getSubscriptionCode());
                if(eventSubscriptionDto == null){
                    tracer.error("异步处理事件" + eventOutBox.getEventCode() + "时，未找到订阅者：" + eventOutBox.getSubscriptionCode());
                    // 移除事件从发件箱
                    deleteEventOutBox(eventOutBox);
                    return;
                }
                asyncHandleEvent(eventOutBox,eventSubscriptionDto);
                endTime = System.currentTimeMillis();
                cost = endTime - startTime;
            } catch (Exception e) {
                Tracer tracer = TraceUtil.getCurrentTracer();
                tracer.error(CEventBus.class.getSimpleName(), ToolUtilities.getFullExceptionStack(e));
            }
        }
    }

    @Override
    public void publish(EventDto event) throws Exception {
        publish(null, event);
    }

    @Override
    public void publish(RpcMap<Object> runtimeContext, EventDto event) throws Exception {
        //查找订阅列表，检查是否有匹配的订阅
        Tracer tracer = TraceUtil.getCurrentTracer();
        //校验事件是否符合事件定义要求
        EventDefinitionDto eventDefinitionDto = IEventRegistry.get().queryEventDefinitionByCode(event.getEventCode());
        if(eventDefinitionDto == null){
            tracer.warning(LOG,"未找到事件定义：" + event.getEventCode());
            return;
        }
        if(!CmnUtil.isStringEqual(event.getEventSource(),eventDefinitionDto.getEventSource())
            && !event.getEventSource().matches(eventDefinitionDto.getEventSource())){
            //事件源不匹配事件定义中的事件源正则时，直接返回
            tracer.warning(LOG,"事件源不匹配事件定义中的事件源正则时，直接返回：" + event.getEventSource() + " != " + eventDefinitionDto.getEventSource());
            return;
        }
        IEventRegistry.get().validateEvent(eventDefinitionDto,event);

        TraceContext traceContext = event.getTraceParent();
        if(traceContext == null){
            traceContext = new TraceContext();
            event.setTraceParent(traceContext);
        }else{
            event.setCurrentTraceParent(traceContext);
        }

        List<EventSubscriptionDto> eventSubscriptionDtos = getRuntimeSubscriptions(event.getEventCode());
        for (EventSubscriptionDto eventSubscriptionDto : eventSubscriptionDtos) {
            if(!CmnUtil.isStringEmpty(eventSubscriptionDto.getEventSourceRegex())){
                if(!event.getEventSource().matches(eventSubscriptionDto.getEventSourceRegex())){
                    //事件源不匹配订阅者中的事件源正则时，直接跳过
                    continue;
                }
            }
            //将事件持久化到发件箱，并触发同步事件的处理逻辑
            EventInvokeMode invokeMode = eventSubscriptionDto.getInvokeModeEnum();
            if (invokeMode == EventInvokeMode.同步) {
                tracer.info("触发同步订阅事件处理：" + eventSubscriptionDto.getCode());
                // 同步调用，直接触发事件处理
                long retryTimes = 0;
                long maxRetryTimes = CmnUtil.getLong(eventSubscriptionDto.getRetryTimes(),0L);
                do{
                    try {
                        handleEvent(runtimeContext, event, eventSubscriptionDto, null);
                        break;
                    }catch (Exception e){
                        tracer.error("处理同步订阅事件" + eventSubscriptionDto.getCode() + "时发生异常", ToolUtilities.getFullExceptionStack(e));
                        retryTimes++;
                        if(retryTimes >= maxRetryTimes){
                            throw e;
                        }
                    }
                }while(retryTimes < maxRetryTimes);
            } else if (invokeMode == EventInvokeMode.异步) {
                tracer.info("触发异步订阅事件处理：" + eventSubscriptionDto.getCode());
                // 异步调用，将事件放入发件箱
                addEventToOutBox(event,eventSubscriptionDto);
            }
        }
    }

    protected void asyncHandleEvent(EventOutBoxDto eventOutBoxDto, EventSubscriptionDto eventSubscriptionDto) throws Exception {
        EventDto event = new EventDto();
        event.setEventCode(eventOutBoxDto.getEventCode());
        event.setEventSource(eventOutBoxDto.getEventSource());
        if (!CmnUtil.isStringEmpty(eventOutBoxDto.getPayload())) {
            event.setPayload(JsonUtil.fromJson(eventOutBoxDto.getPayload(), Map.class));
        }
        if (!CmnUtil.isStringEmpty(eventOutBoxDto.getMetadata())) {
            event.setMetadata(JsonUtil.fromJson(eventOutBoxDto.getMetadata(), Map.class));
        }
        event.setPublishTime(eventOutBoxDto.getPublishTime());

        //查找订阅列表，检查是否有匹配的订阅
        Tracer tracer = TraceUtil.getCurrentTracer();

        EventInvokeMode invokeMode = eventSubscriptionDto.getInvokeModeEnum();
        if (invokeMode == EventInvokeMode.异步) {
            if(!CmnUtil.isStringEmpty(eventSubscriptionDto.getEventSourceRegex())){
                if(!event.getEventSource().matches(eventSubscriptionDto.getEventSourceRegex())){
                    //事件源不匹配订阅者中的事件源正则时，直接跳过
                    return;
                }
            }
            tracer.info("处理异步订阅事件：" + eventSubscriptionDto.getCode());
            // 异步调用
            handleEvent(null, event, eventSubscriptionDto,eventOutBoxDto);
        }
    }

    protected EventProcessingLogDto newEventProcessingLogDto(EventDto event, EventSubscriptionDto eventSubscriptionDto){
        EventProcessingLogDto log = new EventProcessingLogDto();
        log.setCode(ToolUtilities.allockUUIDWithUnderline());
        log.setEventCode(event.getEventCode())
                .setEventSource(event.getEventSource())
                .setPublishTime(event.getPublishTime())
                .setSubscriptionCode(eventSubscriptionDto.getCode())
                .setInvokeMode(eventSubscriptionDto.getInvokeMode());
        if (event.getPayload() != null) {
            log.setPayload(JsonUtil.toPrettyJson(event.getPayload()));
        }
        if(event.getMetadata() != null) {
            log.setMetadata(JsonUtil.toPrettyJson(event.getMetadata()));
            TraceContext traceContext = event.getTraceParent();
            if(traceContext != null){
                log.setTraceId(traceContext.getTraceId())
                        .setParentSpanID(traceContext.getParentId())
                        .setCurrentSpanID(TraceContext.getCurrentSpanId());
            }
        }
        return log;
    }

    protected Object handleEvent(RpcMap<Object> context, EventDto event, EventSubscriptionDto eventSubscriptionDto, EventOutBoxDto outBoxDto) throws Exception {
        // 从订阅中获取事件处理类
        LvUtil.AutoTracer tracer = LvUtil.newAutoTracer();
        long maxRetryTimes = CmnUtil.getLong(eventSubscriptionDto.getRetryTimes(),0L);
        long currentRetryTimes = 0L;
        Long startTime = System.currentTimeMillis();
        // 创建事件处理实例
        EventHandler eventHandler = null;
        Throwable throwable = null;
        EventProcessingLogDto log = newEventProcessingLogDto(event,eventSubscriptionDto);
        try {
            if(outBoxDto != null){
                log.setOutBoxCode(outBoxDto.getCode());
                currentRetryTimes = queryEventRetryTimes(outBoxDto.getCode());
            }
            String eventHandlerClassName = eventSubscriptionDto.getEventHandler();
            if (CmnUtil.isStringEmpty(eventHandlerClassName)) {
                throw new IllegalArgumentException("事件处理器类名为空: " + eventSubscriptionDto.getCode());
            }
            // 加载事件处理类
            Class<? extends EventHandler> eventHandlerClass;
            try {
                eventHandlerClass = ClassFactory.loadClass(eventHandlerClassName);
            } catch (ClassNotFoundException e) {
                throw new Exception("无法加载事件处理类: " + eventHandlerClassName, e);
            }

            if (CellIntf.class.isAssignableFrom(eventHandlerClass)) {
                eventHandler = Cells.get(eventHandlerClass);
            } else if (!eventHandlerClass.isInterface()) {
                eventHandler = eventHandlerClass.newInstance();
            } else {
                throw new Exception("事件处理类" + eventHandlerClassName + "不是可实例化的EventHandler实现类!");
            }

            // 调用事件处理方法
            IDao dao = null;
            if(context == null){
                context = new RpcMap<>();
                dao = IDaoService.newIDao();
                context.put("$dao$",dao);
                Progress prog = Progress.newTracer();
                context.put("$progress$",prog);
            }
            // 确保在事件处理方法中使用的dao是我们创建的实例
            try {
                // 应用映射规则
                if(!CmnUtil.isStringEmpty(eventSubscriptionDto.getPayloadMappingRule())){
                    event = ToolUtilities.clone(event);
                    event.setPayload(applyMappingRule(event.getPayload(), eventSubscriptionDto.getPayloadMappingRule()));
                }
                EventHandlerInitParameter initParameter = null;
                if(!CmnUtil.isStringEmpty(eventSubscriptionDto.getEventHandlerParam())){
                    initParameter = (EventHandlerInitParameter) JsonUtil.fromJson(eventSubscriptionDto.getEventHandlerParam(), eventHandler.getInitParameterType());
                }
                Object result = eventHandler.onEvent(initParameter,context, event);
                if (dao != null) { // 只有当是我们创建的dao时才提交
                    dao.commit();
                }
                return result;
            } finally {
                if (dao != null) {
                    dao.close();
                }
            }
        } catch (Throwable t) {
            currentRetryTimes++;
            throwable = t;
            throw t;
        } finally {
            Long endTime = System.currentTimeMillis();
            Long cost = endTime - startTime;
            log.setHandleStartTime(startTime).setHandleEndTime(endTime).setHandleCost(cost);
            log.setRetryTimes(currentRetryTimes);
            if (tracer != null) {
                String handlerLog = tracer.getTrace();
                log.setHandleLog(handlerLog);
                tracer.close();
            }
            if (throwable != null) {
                log.setErrorMsg(ToolUtilities.getFullExceptionStack(throwable));
            }
            addLogToQueue(log);
            if(throwable == null){
                deleteEventOutBox(outBoxDto);
            }else{
                if(currentRetryTimes >= maxRetryTimes){
                    // 超过最大重试次数，将事件移动到死信队列
                    moveEventToDeadLetterQueue(outBoxDto,throwable);
                }
            }
        }
    }


    protected Map<String, Object> applyMappingRule(Map<String, Object> payload, String ruleJson) {
        if (payload == null || ruleJson == null) return payload;
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, String> ruleMap = JsonUtil.fromJson(ruleJson, Map.class);
        for (Map.Entry<String, String> entry : ruleMap.entrySet()) {
            Object value = getNestedValue(payload, entry.getKey());
            result.put(entry.getValue(), value);
        }
        return result;
    }

    private Object getNestedValue(Map<String, Object> map, String key) {
        String[] parts = key.split("\\.");
        Object current = map;
        for (String part : parts) {
            if (!(current instanceof Map)) return null;
            current = ((Map<?, ?>) current).get(part);
        }
        return current;
    }

    long clientTimeTag = 0;
    /**
     * 注册所有订阅者
     */
    protected void registAllSubscriber() throws Exception {
        long modelDataTag = CmnUtil.getLong(ICacheMgr.get().getModelDataTag(EventSubscriptionDto.FormModelId),-1);
        if(clientTimeTag == modelDataTag) {
            return;
        }
        ResultSet<EventSubscriptionDto> rs = queryEventSubscriptionPage(null,1,Integer.MAX_VALUE);
        //FIXME 这里要清理旧的订阅，在批量添加新的订阅
        for(EventSubscriptionDto event : rs.getDataList()){
            subscribe(event);
        }
        clientTimeTag = modelDataTag;
    }

    @Override
    public EventSubscriptionDto subscribe(EventSubscriptionDto subscriptionDto) {
        if(subscriptionDto.isTemporary()){
            tmpEventSubscriptions.put(subscriptionDto.getCode(), subscriptionDto);
        }else {
            eventSubscriptions.put(subscriptionDto.getCode(), subscriptionDto);
        }
        return subscriptionDto;
    }

    @Override
    public void unsubscribe(EventSubscriptionDto subscriptionDto) {
        tmpEventSubscriptions.remove(subscriptionDto.getCode());
        eventSubscriptions.remove(subscriptionDto.getCode());
    }

    protected EventSubscriptionDto getRuntimeEventSubscription(String code){
        if(CmnUtil.isStringEmpty(code)){
            return null;
        }
        EventSubscriptionDto eventSubscriptionDto = tmpEventSubscriptions.get(code);
        if(eventSubscriptionDto == null){
            eventSubscriptionDto = eventSubscriptions.get(code);
        }
        return eventSubscriptionDto;
    }

    @Override
    public ResultSet<EventSubscriptionDto> queryRuntimeSubscriptionPage(String keyword, int pageNo, int pageSize) throws Exception {
        // 查找订阅列表，检查是否有匹配的订阅
        List<EventSubscriptionDto> matchedSubscriptions = new ArrayList<>();
        for (EventSubscriptionDto eventSubscriptionDto : eventSubscriptions.values()) {
            if (CmnUtil.isStringEmpty(keyword) ||
                    eventSubscriptionDto.getCode().toLowerCase().contains(keyword.toLowerCase())) {
                matchedSubscriptions.add(eventSubscriptionDto);
            }
        }
        for (EventSubscriptionDto eventSubscriptionDto : tmpEventSubscriptions.values()) {
            if (CmnUtil.isStringEmpty(keyword) ||
                    eventSubscriptionDto.getCode().toLowerCase().contains(keyword.toLowerCase())) {
                matchedSubscriptions.add(eventSubscriptionDto);
            }
        }

        // 实现分页逻辑
        int totalCount = matchedSubscriptions.size();
        int startIndex = (pageNo - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalCount);

        List<EventSubscriptionDto> pagedSubscriptions;
        if (startIndex >= totalCount) {
            pagedSubscriptions = new ArrayList<>();
        } else {
            pagedSubscriptions = matchedSubscriptions.subList(startIndex, endIndex);
        }

        ResultSet<EventSubscriptionDto> resultSet = new ResultSet<>();
        resultSet.setTotalCount(totalCount);
        resultSet.setDataList(pagedSubscriptions);
        return resultSet;
    }

    @Override
    public List<EventSubscriptionDto> getRuntimeSubscriptions(String eventCode) throws Exception {
        //查找订阅列表，检查是否有匹配的订阅
        List<EventSubscriptionDto> eventSubscriptionDtos = new ArrayList<>();
        for (EventSubscriptionDto eventSubscriptionDto : eventSubscriptions.values()) {
            if (CmnUtil.isStringEqual(eventCode,eventSubscriptionDto.getEventCode())) {
                eventSubscriptionDtos.add(eventSubscriptionDto);
            }
        }
        for (EventSubscriptionDto eventSubscriptionDto : tmpEventSubscriptions.values()) {
            if (CmnUtil.isStringEqual(eventCode,eventSubscriptionDto.getEventCode())) {
                eventSubscriptionDtos.add(eventSubscriptionDto);
            }
        }
        return eventSubscriptionDtos;
    }

    protected EventOutBoxDto addEventToOutBox(EventDto event, EventSubscriptionDto eventSubscriptionDto) throws Exception {
        EventOutBoxDto eventOutBoxDto = new EventOutBoxDto();
        eventOutBoxDto.setCode(ToolUtilities.allockUUIDWithUnderline());
        eventOutBoxDto.setEventCode(event.getEventCode());
        eventOutBoxDto.setEventSource(event.getEventSource());
        if (event.getPayload() != null) {
            eventOutBoxDto.setPayload(JsonUtil.toJson(event.getPayload()));
        }

        if (event.getMetadata() != null) {
            eventOutBoxDto.setMetadata(JsonUtil.toJson(event.getMetadata()));
        }
        eventOutBoxDto.setSubscriptionCode(eventSubscriptionDto.getCode());
        eventOutBoxDto.setPublishTime(event.getPublishTime());
        try (IDao dao = IDaoService.newIDao()) {
            Form form = DtoConvertUtil.convertToForm(eventOutBoxDto);
            form = IFormMgr.get().createForm(dao, form);
            dao.commit();
            eventOutBoxDto = DtoConvertUtil.convertToDto(form, EventOutBoxDto.class, true);
            return eventOutBoxDto;
        }
    }

    protected ResultSet<EventOutBoxDto> queryEventOutBoxPage(Cnd cnd, int pageNo, int pageSize) throws Exception {
        try (IDao dao = IDaoService.newIDao()) {
            ResultSet<Form> resultSet = IFormMgr.get().queryFormPage(dao, EventOutBoxDto.FormModelId, cnd, pageNo, pageSize, true, true);
            List<EventOutBoxDto> list = new ArrayList<>();
            for (Form form : resultSet.getDataList()) {
                EventOutBoxDto eventOutBoxDto = DtoConvertUtil.convertToDto(form, EventOutBoxDto.class, true);
                list.add(eventOutBoxDto);
            }
            ResultSet<EventOutBoxDto> resultSet2 = new ResultSet<>();
            resultSet2.setDataList(list);
            resultSet2.setTotalCount(resultSet.getTotalCount());
            return resultSet2;
        }
    }

    protected void deleteEventOutBox(EventOutBoxDto eventOutBoxDto) throws Exception {
        if(eventOutBoxDto == null) {
            return;
        }
        try (IDao dao = IDaoService.newIDao()) {
            IFormMgr.get().deleteForm(dao, EventOutBoxDto.FormModelId, eventOutBoxDto.getUuid());
            dao.commit();
        }
    }

    protected void moveEventToDeadLetterQueue(EventOutBoxDto eventOutBoxDto, Throwable e) throws Exception {
        if(eventOutBoxDto == null) return;
        try (IDao dao = IDaoService.newIDao()) {
            IFormMgr.get().deleteForm(dao, EventOutBoxDto.FormModelId, eventOutBoxDto.getUuid());
            DeadLetterQueueDto deadLetterQueueDto = new DeadLetterQueueDto();
            deadLetterQueueDto.setCode(eventOutBoxDto.getCode());
            deadLetterQueueDto.setEventCode(eventOutBoxDto.getEventCode());
            deadLetterQueueDto.setEventSource(eventOutBoxDto.getEventSource());
            deadLetterQueueDto.setSubscriptionCode(eventOutBoxDto.getSubscriptionCode());
            if (eventOutBoxDto.getPayload() != null) {
                deadLetterQueueDto.setPayload(eventOutBoxDto.getPayload());
            }
            if (eventOutBoxDto.getMetadata() != null) {
                deadLetterQueueDto.setMetadata(eventOutBoxDto.getMetadata());
            }
            deadLetterQueueDto.setPublishTime(eventOutBoxDto.getPublishTime());
            deadLetterQueueDto.setFailReason(ToolUtilities.getFullExceptionStack(e));
            deadLetterQueueDto.setCreateTime(System.currentTimeMillis());
            Form form = DtoConvertUtil.convertToForm(deadLetterQueueDto);
            form = IFormMgr.get().createForm(dao, form);
            dao.commit();
        }
    }

    @Override
    public EventSubscriptionDto createEventSubscription(EventSubscriptionDto subscription) throws Exception {
        try (IDao dao = IDaoService.newIDao()) {
            Form form = DtoConvertUtil.convertToForm(subscription);
            form = IFormMgr.get().createForm(dao, form);
            dao.commit();
            return DtoConvertUtil.convertToDto(form, EventSubscriptionDto.class, true);
        }
    }

    @Override
    public EventSubscriptionDto queryEventSubscription(String subscriptionUuid) throws Exception {
        try (IDao dao = IDaoService.newIDao()) {
            Form form = IFormMgr.get().queryForm(dao, EventSubscriptionDto.FormModelId, subscriptionUuid);
            if (form == null) {
                return null;
            }
            return DtoConvertUtil.convertToDto(form, EventSubscriptionDto.class, true);
        }
    }

    @Override
    public EventSubscriptionDto queryEventSubscriptionByCode(String subscriptionCode) throws Exception {
        try (IDao dao = IDaoService.newIDao()) {
            Form form = IFormMgr.get().queryFormByCode(dao, EventSubscriptionDto.FormModelId, subscriptionCode);
            if (form == null) {
                return null;
            }
            return DtoConvertUtil.convertToDto(form, EventSubscriptionDto.class, true);
        }
    }

    @Override
    public EventSubscriptionDto updateEventSubscription(EventSubscriptionDto subscription) throws Exception {
        try (IDao dao = IDaoService.newIDao()) {
            Form form = DtoConvertUtil.convertToForm(subscription);
            form = IFormMgr.get().updateForm(dao, form);
            dao.commit();
            return DtoConvertUtil.convertToDto(form, EventSubscriptionDto.class, true);
        }
    }

    @Override
    public void deleteEventSubscription(String subscriptionUuid) throws Exception {
        try (IDao dao = IDaoService.newIDao()) {
            IFormMgr.get().deleteForm(dao, EventSubscriptionDto.FormModelId, subscriptionUuid);
            dao.commit();
        }
    }

    @Override
    public ResultSet<EventSubscriptionDto> queryEventSubscriptionPage(Cnd cnd, int pageNo, int pageSize) throws Exception {
        try (IDao dao = IDaoService.newIDao()) {
            ResultSet<Form> resultSet = IFormMgr.get().queryFormPage(dao, EventSubscriptionDto.FormModelId, cnd, pageNo, pageSize, true, true);
            ResultSet<EventSubscriptionDto> resultSet2 = new ResultSet<>();
            resultSet2.setTotalCount(resultSet.getTotalCount());
            resultSet2.setDataList(DtoConvertUtil.convertToDtos(resultSet.getDataList(), EventSubscriptionDto.class));
            return resultSet2;
        }
    }

    @Override
    public ResultSet<DeadLetterQueueDto> queryDeadLetterQueuePage(Cnd cnd, int pageNo, int pageSize) throws Exception {
        try (IDao dao = IDaoService.newIDao()) {
            ResultSet<Form> resultSet = IFormMgr.get().queryFormPage(dao, DeadLetterQueueDto.FormModelId, cnd, pageNo, pageSize, true, true);
            ResultSet<DeadLetterQueueDto> resultSet2 = new ResultSet<>();
            resultSet2.setTotalCount(resultSet.getTotalCount());
            resultSet2.setDataList(DtoConvertUtil.convertToDtos(resultSet.getDataList(), DeadLetterQueueDto.class));
            return resultSet2;
        }
    }

    @Override
    public void deleteDeadLetterQueue(String deadLetterQueueUuid) throws Exception {
        try (IDao dao = IDaoService.newIDao()) {
            IFormMgr.get().deleteForm(dao, DeadLetterQueueDto.FormModelId, deadLetterQueueUuid);
            dao.commit();
        }
    }

    @Override
    public void retryDeadLetterQueue(DeadLetterQueueDto deadLetterQueueDto) throws Exception {
        String relateSubscription = deadLetterQueueDto.getSubscriptionCode();
        EventSubscriptionDto eventSubscriptionDto = getRuntimeEventSubscription(relateSubscription);
        if(eventSubscriptionDto == null) {
            throw new Exception(GpfDCBasicI18n.getString("订阅配置{1}不存在", relateSubscription));
        }
        EventDto eventDto = new EventDto();
        eventDto.setEventCode(deadLetterQueueDto.getEventCode())
                .setEventSource(deadLetterQueueDto.getEventSource())
                .setPublishTime(deadLetterQueueDto.getPublishTime());
        if(!CmnUtil.isStringEmpty(deadLetterQueueDto.getPayload())) {
            eventDto.setPayload(JsonUtil.fromJson(deadLetterQueueDto.getPayload(), Map.class));
        }
        if(!CmnUtil.isStringEmpty(deadLetterQueueDto.getMetadata())) {
            eventDto.setMetadata(JsonUtil.fromJson(deadLetterQueueDto.getMetadata(), Map.class));
        }
        handleEvent(null,eventDto,eventSubscriptionDto,null);
    }

    @Override
    public ResultSet<EventProcessingLogDto> queryEventProcessingLogPage(Cnd cnd, int pageNo, int pageSize) throws Exception {
        try (IDao dao = IDaoService.newIDao()) {
            ResultSet<Form> resultSet = IFormMgr.get().queryFormPage(dao, EventProcessingLogDto.FormModelId, cnd, pageNo, pageSize, true, true);
            ResultSet<EventProcessingLogDto> resultSet2 = new ResultSet<>();
            resultSet2.setTotalCount(resultSet.getTotalCount());
            resultSet2.setDataList(DtoConvertUtil.convertToDtos(resultSet.getDataList(), EventProcessingLogDto.class));
            return resultSet2;
        }
    }

    @Override
    public EventProcessingLogDto queryEventProcessingLogByEventOutBoxCode(String eventOutBoxCode) throws Exception {
        Cnd cnd = Cnd.where(new SqlExpressionGroup().andEquals(EventProcessingLogDto.FieldCode_OutBoxCode, eventOutBoxCode));
        try (IDao dao = IDaoService.newIDao()) {
            ResultSet<Form> resultSet = IFormMgr.get().queryFormPage(dao, EventProcessingLogDto.FormModelId, cnd, 1, 1, true, true);
            if(resultSet.getTotalCount() == 0) {
                return null;
            }
            Form form = resultSet.getDataList().get(0);
            return DtoConvertUtil.convertToDto(form, EventProcessingLogDto.class, true);
        }
    }

    protected long queryEventRetryTimes(String eventOutBoxCode) throws Exception {
        Cnd cnd = Cnd.where(new SqlExpressionGroup().andEquals(EventProcessingLogDto.FieldCode_OutBoxCode, eventOutBoxCode));
        try (IDao dao = IDaoService.newIDao()) {
            long totalCount = IFormMgr.get().countForm(dao, EventProcessingLogDto.FormModelId, cnd);
            return totalCount;
        }
    }

    @Override
    public ResultSet<EventHandlerProgress> queryEventHandlerProgressPage(String keyword, List<String> statusList, int pageNo, int pageSize) throws Exception {
        List<CmnRunnable> runnableList = eventBusPool.getRunningRunnable();
        ArrayList<CmnRunnable> waitingRunnableList = eventBusPool.getWaitingRunnable();
        List<EventHandlerProgress> list = new ArrayList<>();
        for(CmnRunnable runnable : runnableList) {
            if(runnable instanceof EventRunnable) {
                EventRunnable eventRunnable = (EventRunnable) runnable;
                EventOutBoxDto eventOutBox = eventRunnable.getEventOutBox();
                if(!CmnUtil.isStringEmpty(keyword)) {
                    keyword = keyword.toLowerCase().trim();
                    if(!CmnUtil.isStringEqual(eventOutBox.getEventCode().toLowerCase(), keyword)
                    && !CmnUtil.isStringEqual(eventOutBox.getSubscriptionCode().toLowerCase(), keyword)
                    && !CmnUtil.isStringEqual(eventOutBox.getEventSource().toLowerCase(), keyword)) {
                        continue;
                    }
                }
                EventHandlerProgress eventHandlerProgress = new EventHandlerProgress();
                eventHandlerProgress.setEventOutBox(eventRunnable.getEventOutBox());
                eventHandlerProgress.setRunableKey(eventRunnable.getKey());
                eventHandlerProgress.setPoolingTime(eventRunnable.getPoolingTime());
                eventHandlerProgress.setStartTime(eventRunnable.getStartTime());
                eventHandlerProgress.setEndTime(eventRunnable.getEndTime());
                eventHandlerProgress.setCost(eventRunnable.getCost());
                eventHandlerProgress.setStatus("running");
                list.add(eventHandlerProgress);
            }
        }
        for(CmnRunnable runnable : waitingRunnableList) {
            if(runnable instanceof EventRunnable) {
                EventRunnable eventRunnable = (EventRunnable) runnable;
                EventOutBoxDto eventOutBox = eventRunnable.getEventOutBox();
                if(!CmnUtil.isStringEmpty(keyword)) {
                    keyword = keyword.toLowerCase().trim();
                    if(!CmnUtil.isStringEqual(eventOutBox.getEventCode().toLowerCase(), keyword)
                            && !CmnUtil.isStringEqual(eventOutBox.getSubscriptionCode().toLowerCase(), keyword)
                            && !CmnUtil.isStringEqual(eventOutBox.getEventSource().toLowerCase(), keyword)) {
                        continue;
                    }
                }
                EventHandlerProgress eventHandlerProgress = new EventHandlerProgress();
                eventHandlerProgress.setEventOutBox(eventRunnable.getEventOutBox());
                eventHandlerProgress.setRunableKey(eventRunnable.getKey());
                eventHandlerProgress.setPoolingTime(eventRunnable.getPoolingTime());
                eventHandlerProgress.setStartTime(eventRunnable.getStartTime());
                eventHandlerProgress.setEndTime(eventRunnable.getEndTime());
                eventHandlerProgress.setCost(eventRunnable.getCost());
                eventHandlerProgress.setStatus("waiting");
                list.add(eventHandlerProgress);
            }
        }
        ResultSet<EventHandlerProgress> resultSet = new ResultSet<>();
        if(statusList.contains("running") && statusList.contains("waiting")) {
            resultSet.setTotalCount(eventBusPool.getTotalCount());
        }else if(statusList.contains("running")) {
            resultSet.setTotalCount(runnableList.size());
        }else if(statusList.contains("waiting")) {
            resultSet.setTotalCount(waitingRunnableList.size());
        }
        // 实现分页逻辑
        int totalCount = list.size();
        int startIndex = (pageNo - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalCount);

        List<EventHandlerProgress> pageList;
        if (startIndex >= totalCount) {
            pageList = new ArrayList<>();
        } else {
            pageList = list.subList(startIndex, endIndex);
        }
        resultSet.setDataList(pageList);
        return resultSet;
    }

    @Override
    public void killEventHandlerProgress(String runnableKey) throws Exception {
        CmnRunnable runnable = eventBusPool.getRunnable(runnableKey);
        if(runnable instanceof EventRunnable) {
            EventRunnable eventRunnable = (EventRunnable) runnable;
            if(eventRunnable.getCurrentThread() != null && !eventRunnable.getCurrentThread().isInterrupted()) {
                eventRunnable.getCurrentThread().interrupt();
            }
        }
    }
}