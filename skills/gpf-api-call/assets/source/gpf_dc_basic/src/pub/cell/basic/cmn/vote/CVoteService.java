package cell.basic.cmn.vote;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.dao.Cnd;
import org.nutz.dao.sql.OrderBy;
import org.nutz.dao.util.cri.OrderBySet;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.client.util.lazy.LazyPool;

import bap.cells.BasicServiceCell;
import basic.cmn.dto.event.EventDefinitionDto;
import basic.cmn.dto.event.EventDto;
import basic.cmn.dto.vote.UserVotePollDto;
import basic.cmn.dto.vote.VoteMode;
import basic.cmn.dto.vote.VoteOptionDto;
import basic.cmn.dto.vote.VoteParticipantDto;
import basic.cmn.dto.vote.VotePollDto;
import basic.cmn.dto.vote.VoteRecordDto;
import basic.cmn.dto.vote.VoteRuleType;
import basic.cmn.dto.vote.VoteStatus;
import cell.basic.cmn.event.IEventBus;
import cell.basic.cmn.event.IEventRegistry;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.cdao.ILock;
import cell.gpf.adur.data.IFormMgr;
import cmn.dto.session.UserSession;
import cmn.enums.NestingTableUpdateMode;
import cmn.util.JsonUtil;
import cmn.util.NullUtil;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.adur.data.Form;
import gpf.adur.data.FormModel;
import gpf.adur.data.ResultSet;
import gpf.adur.user.User;
import gpf.dc.util.DtoConvertUtil;
import gpf.exception.VerifyException;
import web.dto.Pair;


public class CVoteService extends BasicServiceCell implements IVoteService{

    /**
     * 投票轮询任务Map，key为投票ID，value为投票轮询任务
     */
    Map<String,VotePollDto> votePollMap = new ConcurrentHashMap<>();

    Thread mainThread = null;

    String eventSchema = "{\n" +
            "  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n" +
            "  \"type\": \"object\",\n" +
            "  \"properties\": {\n" +
            "    \"modelId\": {\n" +
            "      \"type\": \"string\",\n" +
            "      \"description\": \"模型ID标识\"\n" +
            "    },\n" +
            "    \"uuid\": {\n" +
            "      \"type\": \"string\",\n" +
            "      \"description\": \"通用唯一识别码\"\n" +
            "    },\n" +
            "    \"voteSubject\": {\n" +
            "      \"type\": \"string\",\n" +
            "      \"description\": \"投票主题\"\n" +
            "    },\n" +
            "    \"voteDesc\": {\n" +
            "      \"type\": \"string\",\n" +
            "      \"description\": \"投票描述信息\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"additionalProperties\": false\n" +
            "}";
    public final static String INC_VOTEOPTION = "INC";
    public final static String DEC_VOTEOPTION = "DEC";
    LazyPool<Pair<String,VoteOptionDto>> voteOptionLazyPool = new LazyPool<Pair<String,VoteOptionDto>>(100L){
        @Override
        public void handle(List lstData) {
            Map<String,VoteOptionDto>  voteOptionMap = new ConcurrentHashMap<>();
            for(Object obj : lstData){
                if(obj instanceof Pair){
                	Pair<String,VoteOptionDto> voteOptionPair = (Pair<String,VoteOptionDto>) obj;
                    String type = voteOptionPair.getKey();
                    VoteOptionDto voteOptionDto = voteOptionPair.getValue();
                	if(!voteOptionMap.containsKey(voteOptionDto.getUuid())){
                        voteOptionMap.put(voteOptionDto.getUuid(), voteOptionDto);
                    }
                    voteOptionDto = voteOptionMap.get(voteOptionDto.getUuid());
                    //更新投票选项统计值
                    if(CmnUtil.isStringEqual(type, INC_VOTEOPTION)) {
	                    if(voteOptionDto.getVoteCount() == null){
	                        voteOptionDto.setVoteCount(1L);
	                    }else {
	                        voteOptionDto.setVoteCount(voteOptionDto.getVoteCount() + 1);
	                    }
                    }else {
                    	if(voteOptionDto.getVoteCount() != null && voteOptionDto.getVoteCount() != 0){
	                        voteOptionDto.setVoteCount(voteOptionDto.getVoteCount() - 1);
	                    }
                    }
                }
            }
            try(IDao dao = IDaoService.newIDao()) {
                for (VoteOptionDto voteOptionDto : voteOptionMap.values()) {
                    Form form = DtoConvertUtil.convertToForm(voteOptionDto);
                    IFormMgr.get().updateForm(dao,form,NestingTableUpdateMode.Nothing,new String[]{VoteOptionDto.FieldCode_VoteCount},null);
                }
                dao.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void doStartService() throws Exception {
        try {
            registVoteEvents();
            initVotePollMap();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(mainThread == null){
            mainThread = new Thread("VoteService Thread"){
                @Override
                public void run() {
                    while(true){
                        // 检查投票是否已截止
                        Map<String,VotePollDto> tmpVotePollMap = new LinkedHashMap<>();
                        tmpVotePollMap.putAll(votePollMap);
                        for(VotePollDto votePollDto : tmpVotePollMap.values()){
                            try {
                                if(votePollDto.getEndTime() != null && votePollDto.getEndTime() < System.currentTimeMillis()) {
                                    _doEndVotePoll(votePollDto);
                                }else {
                                    //根据投票类型决定是否可以结束投票
                                    VoteRuleType ruleType = votePollDto.getRuleTypeEnum();
                                    List<VoteRecordDto> voteRecords = queryVoteRecords(votePollDto.getUuid());
                                    //检查是否所有用户都已投票
                                    boolean isEnd = false;
                                    if(voteRecords.size() == votePollDto.getVoteParticipant().size()){
                                        //所有用户都已投票，结束投票
                                        isEnd = true;
                                    }
                                    if(isEnd){
                                        _doEndVotePoll(votePollDto);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        ToolUtilities.sleep(5*1000L);
                    }
                }
            };
            mainThread.start();
        }
    }

    @Override
    protected void doStopService() {
        if(mainThread != null){
            mainThread.interrupt();
        }
    }

    protected void registVoteEvents() throws Exception {
        EventDefinitionDto startEvent = new  EventDefinitionDto();
        startEvent.setCode(VOTE_START_EVENT);
        startEvent.setEventName(VOTE_START_EVENT).setEventSource(IVoteService.class.getName())
                .setEventType(VOTE_START_EVENT).setEventSchema(eventSchema);
        saveVoteEvent(startEvent);

        EventDefinitionDto endEvent = new  EventDefinitionDto();
        endEvent.setCode(VOTE_END_EVENT);
        endEvent.setEventName(VOTE_END_EVENT).setEventSource(IVoteService.class.getName())
                .setEventType(VOTE_END_EVENT).setEventSchema(eventSchema);
        saveVoteEvent(endEvent);
    }

    protected void saveVoteEvent(EventDefinitionDto eventDefinition) throws Exception {
//    	String key = EventDefinitionDto.FormModelId + eventDefinition.getCode();
//    	ILock.get().lockKey(key, 10*1000);
//    	try {
	        EventDefinitionDto existEvent = IEventRegistry.get().queryEventDefinitionByCode(eventDefinition.getCode());
	        if(existEvent == null){
	            IEventRegistry.get().createEventDefinition(eventDefinition);
	        }else{
//	            eventDefinition.setUuid(existEvent.getUuid());
//	            IEventRegistry.get().updateEventDefinition(eventDefinition);
	        }
//    	}finally {
//			ILock.get().unlock(key);
//		}
    }

    protected void initVotePollMap() throws Exception {
        try(IDao dao = IDaoService.newIDao()){
            Cnd cnd = Cnd.NEW();
            cnd.and(new SqlExpressionGroup().andEquals(VotePollDto.FieldCode_Status,VoteStatus.进行中.name()));
            ResultSet<VotePollDto> rs = queryVotePollPage(cnd,1,Integer.MAX_VALUE);
            for(VotePollDto votePollDto : rs.getDataList()){
                votePollMap.put(votePollDto.getUuid(),votePollDto);
            }
        }
    }

    @Override
    public VotePollDto draftVotePoll(User user, VotePollDto votePoll) throws Exception {
        try(IDao dao = IDaoService.newIDao()){
            votePoll.setCode(ToolUtilities.allockUUIDWithUnderline());
            votePoll.setStatus(VoteStatus.草稿).setResult(null)
                    .setCreator(user.getFullName()).setCreatorID(user.getCode())
                    .setCreateTime(System.currentTimeMillis()).setUpdateTime(System.currentTimeMillis());
            Form form = DtoConvertUtil.convertToForm(votePoll);
            form = IFormMgr.get().createForm(dao,form);
            dao.commit();
            return DtoConvertUtil.convertToDto(form,VotePollDto.class,true);
        }
    }

    @Override
    public VotePollDto saveVotePoll(User user,VotePollDto votePoll) throws Exception {
        VotePollDto existVotePoll = queryVotePoll(votePoll.getUuid());
        if(existVotePoll == null){
            return draftVotePoll(user,votePoll);
        }else {
            try(IDao dao = IDaoService.newIDao()){
                VoteStatus status = votePoll.getStatusEnum();
                if(status != VoteStatus.草稿){
                    throw new VerifyException("投票状态错误，只能更新投票草稿");
                }
                if(!CmnUtil.isStringEqual(votePoll.getCreatorID(),user.getCode())){
                    throw new VerifyException("只有投票创建者才能更新投票草稿");
                }
                votePoll.setUpdateTime(System.currentTimeMillis())
                        .setStatus(VoteStatus.草稿).setResult(null)
                        .setCreator(user.getFullName()).setCreatorID(user.getCode())
                        ;
                votePoll.setCode(existVotePoll.getCode()).setUuid(existVotePoll.getUuid());
                Form form = DtoConvertUtil.convertToForm(votePoll);
                form = IFormMgr.get().updateForm(dao, form);
                dao.commit();
                return DtoConvertUtil.convertToDto(form, VotePollDto.class, true);
            }
        }
    }

    @Override
    public void startVotePoll(User user, VotePollDto votePoll) throws Exception {
        VotePollDto existVotePoll = queryVotePoll(votePoll.getUuid());
        if(existVotePoll == null){
            throw new VerifyException("投票不存在");
        }
        VoteStatus status =  existVotePoll.getStatusEnum();
        if(status != VoteStatus.草稿){
            throw new VerifyException("投票已进行或以结束，不能重新开始");
        }
        existVotePoll.setStatus(VoteStatus.进行中).setUpdateTime(System.currentTimeMillis());
        try(IDao dao = IDaoService.newIDao()){
            Form form = DtoConvertUtil.convertToForm(existVotePoll);
            form = IFormMgr.get().updateForm(dao, form, NestingTableUpdateMode.Nothing,new String[]{VotePollDto.FieldCode_Status,VotePollDto.FieldCode_UpdateTime},null);
            dao.commit();
            votePollMap.put(existVotePoll.getUuid(),existVotePoll);
            IEventBus.get().publish(new EventDto().setEventCode(VOTE_START_EVENT).setEventSource(IVoteService.class.getName())
                    .addPayload("modelId",VotePollDto.FormModelId)
                    .addPayload("uuid",existVotePoll.getUuid())
                    .addPayload("voteSubject",votePoll.getVoteSubject())
                    .addPayload("voteDesc",votePoll.getVoteDesc())
            );
        }
    }

    @Override
    public void endVotePoll(String userCode,VotePollDto votePoll) throws Exception {
        try(IDao dao = IDaoService.newIDao()){
            VotePollDto existVotePoll = queryVotePoll(votePoll.getUuid());
            if(existVotePoll == null){
                throw new VerifyException("投票不存在");
            }
            VoteStatus status = existVotePoll.getStatusEnum();
            if (status != VoteStatus.进行中){
                throw new VerifyException("投票未进行中，不能结束");
            }
            if(!CmnUtil.isStringEqual(votePoll.getCreatorID(),userCode)){
                throw new VerifyException("只有投票发起人才能结束投票");
            }
            _doEndVotePoll(existVotePoll);
        }
    }

    @Override
    public void cancelVotePoll(String userCode, String voteUuid) throws Exception {
        try(IDao dao = IDaoService.newIDao()){
            VotePollDto existVotePoll = queryVotePoll(voteUuid);
            if(existVotePoll == null){
                throw new VerifyException("投票不存在");
            }
            VoteStatus status = existVotePoll.getStatusEnum();
            if (status == VoteStatus.已结束){
                throw new VerifyException("投票已结束，不能取消");
            }
            if(!CmnUtil.isStringEqual(existVotePoll.getCreatorID(),userCode)){
                throw new VerifyException("只有投票发起人才能取消投票");
            }
            existVotePoll.setUpdateTime(System.currentTimeMillis()).setStatus(VoteStatus.已取消);
            Form form = DtoConvertUtil.convertToForm(existVotePoll);
            form = IFormMgr.get().updateForm(dao,form, NestingTableUpdateMode.Nothing,new String[]{VotePollDto.FieldCode_Status,VotePollDto.FieldCode_UpdateTime},null);
            votePollMap.remove(existVotePoll.getUuid());
            dao.commit();
        }
    }

    protected void _doEndVotePoll(VotePollDto votePoll) throws Exception {
        try(IDao dao = IDaoService.newIDao()){
            votePoll.setUpdateTime(System.currentTimeMillis()).setStatus(VoteStatus.已结束);
            Form form = DtoConvertUtil.convertToForm(votePoll);
            //更新投票结果
            Form existForm = IFormMgr.get().queryForm(dao,VotePollDto.FormModelId,votePoll.getUuid());
            if(existForm == null){
                return;
            }
            form.setUuid(existForm.getUuid());
            form = IFormMgr.get().updateForm(dao,form, NestingTableUpdateMode.Nothing,new String[]{VotePollDto.FieldCode_Status,VotePollDto.FieldCode_UpdateTime},null);
            votePollMap.remove(votePoll.getUuid());
            dao.commit();
            EventDto event = new EventDto().setEventCode(VOTE_END_EVENT).setEventSource(IVoteService.class.getName())
                    .addPayload("modelId",VotePollDto.FormModelId)
                    .addPayload("uuid",votePoll.getUuid())
                    .addPayload("voteSubject",votePoll.getVoteSubject())
                    .addPayload("voteDesc",votePoll.getVoteDesc());
            Tracer tracer = TraceUtil.getCurrentTracer();
            tracer.info("发送投票结束事件：" + JsonUtil.toJson(event));
            IEventBus.get().publish(event);
        }
    }

    @Override
    public VotePollDto queryVotePoll(String uuid) throws Exception {
        try(IDao dao = IDaoService.newIDao()){
            Form form = IFormMgr.get().queryForm(dao,VotePollDto.FormModelId,uuid);
            if(form == null){
                return null;
            }
            return DtoConvertUtil.convertToDto(form,VotePollDto.class,true);
        }
    }

    @Override
    public ResultSet<VotePollDto> queryVotePollPage(Cnd cnd, int pageNo, int pageSize) throws Exception {
        try(IDao dao = IDaoService.newIDao()){
            if(cnd == null){
                cnd = Cnd.NEW();
            }
            if(isOrderByEmpty(cnd)){
                cnd.descNullsLast(VotePollDto.FieldCode_StartTime);
            }
            ResultSet<Form> rs = IFormMgr.get().queryFormPage(dao,VotePollDto.FormModelId,cnd,pageNo,pageSize,true,true);
            List<VotePollDto> list = new ArrayList<>();
            for(Form form : rs.getDataList()){
                list.add(DtoConvertUtil.convertToDto(form,VotePollDto.class,true));
            }
            ResultSet<VotePollDto> rs2 = new ResultSet<>();
            rs2.setDataList(list);
            rs2.setTotalCount(rs.getTotalCount());
            return rs2;
        }
    }
    protected boolean isOrderByEmpty(Cnd cnd) {
        OrderBy orderBy = cnd.getOrderBy();
        if (orderBy instanceof OrderBySet) {
            if (((OrderBySet) orderBy).getItems().isEmpty())
                return true;
            else
                return false;
        }
        return true;
    }

    @Override
    public ResultSet<UserVotePollDto> queryUserVotePollPage(String userCode, Cnd cnd, int pageNo, int pageSize) throws Exception {
        //查询用户可参与的投票分页
        FormModel mainModel = IFormMgr.get().queryFormModel(VotePollDto.FormModelId);
        FormModel joinModel = IFormMgr.get().queryFormModel(VoteParticipantDto.FormModelId);
        FormModel voteRecordModel = IFormMgr.get().queryFormModel(VoteRecordDto.FormModelId);
        String querySql = "with T1 as (select * from "+ mainModel.getTableName()+")" +
                ", T2 as (select * from "+joinModel.getTableName()+" where "+VoteParticipantDto.FieldCode_UserID+" = '"+userCode+"')" +
                ", T3 as (select * from "+voteRecordModel.getTableName()+" where "+VoteRecordDto.FieldCode_UserID+" = '"+userCode+"')" +
                ", Total as (select distinct T1.*,T3."+VoteRecordDto.FieldCode_VoteOptionID+ " as selectedOption from T1 join T2 on T1.uuid = T2.masterKey left join T3 on T1.uuid = T3."+VoteRecordDto.FieldCode_VoteID+")" +
                " select *,"+ResultSet.TotalCount_Select+" from Total";
        Tracer tracer = TraceUtil.getCurrentTracer();
        tracer.info("queryUserVotePollPage",querySql);
        if(cnd == null){
            cnd = Cnd.NEW();
        }
        if(isOrderByEmpty(cnd)){
            cnd.descNullsLast(VotePollDto.FieldCode_StartTime);
        }
        try(IDao dao = IDaoService.newIDao()){
            Set<String> extFields = new LinkedHashSet<>();
            extFields.add(ResultSet.TotalCount);
            extFields.add("selectedOption");
            ResultSet<Form> rs = IFormMgr.get().queryFormPageBySql(dao, VotePollDto.FormModelId,querySql,extFields,cnd,pageNo,pageSize);
            List<UserVotePollDto> list = new ArrayList<>();
            Set<String> nestingQueryFieldCodes = new LinkedHashSet<>();
            nestingQueryFieldCodes.add(VotePollDto.FieldCode_VoteOption);
            List<Form> forms = IFormMgr.get().batchQueryNestingTableData(dao,rs.getDataList(),nestingQueryFieldCodes);
            for(Form form : forms){
                VotePollDto dto =  DtoConvertUtil.convertToDto(form,VotePollDto.class,true);
                UserVotePollDto userVotePollDto = new UserVotePollDto();
                ToolUtilities.copyFields(dto,userVotePollDto);
//                userVotePollDto.setVoted(CmnUtil.getBoolean(form.getExtField("isVoted".toLowerCase()),false));
                userVotePollDto.setSelectedOption((String)form.getExtField("selectedOption".toLowerCase()));
                list.add(userVotePollDto);
            }
            ResultSet<UserVotePollDto> rs2 = new ResultSet<>();
            rs2.setDataList(list);
            rs2.setTotalCount(rs.getTotalCount());
            return rs2;
        }
    }

    @Override
    public ResultSet<UserVotePollDto> queryMyInitiatedVotePollPage(String userCode, Cnd cnd, int pageNo, int pageSize) throws Exception {
        if(cnd == null){
            cnd = Cnd.NEW();
        }
        if(isOrderByEmpty(cnd)){
            cnd.descNullsLast(VotePollDto.FieldCode_StartTime);
        }
        cnd.and(new SqlExpressionGroup().andEquals(VotePollDto.FieldCode_CreatorID,userCode));
        //查询用户发起的投票分页
        FormModel mainModel = IFormMgr.get().queryFormModel(VotePollDto.FormModelId);
        FormModel voteRecordModel = IFormMgr.get().queryFormModel(VoteRecordDto.FormModelId);
        String querySql = "with T1 as (select * from "+ mainModel.getTableName()+")" +
                ", T2 as (select * from "+voteRecordModel.getTableName()+" where "+VoteRecordDto.FieldCode_UserID+" = '"+userCode+"')" +
                ", Total as (select distinct T1.*,T2."+VoteRecordDto.FieldCode_VoteOptionID+" as selectedOption from T1 left join T2 on T1.uuid = T2."+VoteRecordDto.FieldCode_VoteID+")" +
                " select *,"+ResultSet.TotalCount_Select+" from Total";
        Tracer tracer = TraceUtil.getCurrentTracer();
        tracer.info("queryUserInitiatedVotePollPage",querySql);
        try(IDao dao = IDaoService.newIDao()){
            Set<String> extFields = new LinkedHashSet<>();
            extFields.add(ResultSet.TotalCount);
            extFields.add("selectedOption");
            ResultSet<Form> rs = IFormMgr.get().queryFormPageBySql(dao, VotePollDto.FormModelId,querySql,extFields,cnd,pageNo,pageSize);
            List<UserVotePollDto> list = new ArrayList<>();
            Set<String> nestingQueryFieldCodes = new LinkedHashSet<>();
            nestingQueryFieldCodes.add(VotePollDto.FieldCode_VoteOption);
            List<Form> forms = IFormMgr.get().batchQueryNestingTableData(dao,rs.getDataList(),nestingQueryFieldCodes);
            for(Form form : forms){
                VotePollDto dto =  DtoConvertUtil.convertToDto(form,VotePollDto.class,true);
                UserVotePollDto userVotePollDto = new UserVotePollDto();
                ToolUtilities.copyFields(dto,userVotePollDto);
//                userVotePollDto.setVoted(CmnUtil.getBoolean(form.getExtField("isVoted".toLowerCase()),false));
                userVotePollDto.setSelectedOption((String)form.getExtField("selectedOption".toLowerCase()));
                list.add(userVotePollDto);
            }
            ResultSet<UserVotePollDto> rs2 = new ResultSet<>();
            rs2.setDataList(list);
            rs2.setTotalCount(rs.getTotalCount());
            return rs2;
        }
    }

    @Override
    public void vote(UserSession userSession, String voteUuid, String voteOption,boolean isAnonymous) throws Exception {
        VotePollDto votePollDto = queryVotePoll(voteUuid);
        if(votePollDto == null){
            return;
        }
        if(votePollDto.getStatusEnum() != VoteStatus.进行中){
            throw new VerifyException("投票已结束");
        }
        VoteRecordDto existVoteRecord = queryUserVoteRecord(votePollDto,userSession);
        if(existVoteRecord != null){
//            throw new VerifyException("您已投票，不能重复投票");
        	//更新投票选项统计数
        	VoteOptionDto orgVoteOptionDto = votePollDto.getVoteOption(existVoteRecord.getVoteOptionID());
            VoteOptionDto voteOptionDto = votePollDto.getVoteOption(voteOption);
            if (voteOptionDto != null) {
                try (IDao dao = IDaoService.newIDao()) {
                    Form form = DtoConvertUtil.convertToForm(existVoteRecord);
                    form = IFormMgr.get().updateForm(dao, form, NestingTableUpdateMode.Nothing, new String[] {VoteRecordDto.FieldCode_VoteOptionID,VoteRecordDto.FieldCode_VoteTime}, null);
                    dao.commit();
                    //懒处理更新统计值，避免并发更新导致统计值错误
                    if(orgVoteOptionDto != null) {
                    	voteOptionLazyPool.add(new Pair<>(DEC_VOTEOPTION,orgVoteOptionDto));
                    }
                    voteOptionLazyPool.add(new Pair<>(INC_VOTEOPTION,voteOptionDto));
                }
            }
        	existVoteRecord.setVoteOptionID(voteOption).setVoteTime(System.currentTimeMillis());
        }else {
            VoteRecordDto voteRecordDto = new VoteRecordDto().setVoteID(voteUuid).setVoteOptionID(voteOption)
            		.setUserID(userSession.getUserCode())
            		.setUserFullName(userSession.getUserFullName())
            		.setVoteIP(userSession.getClientIp())
            		.setDeviceId(userSession.getDeviceId());
            voteRecordDto.setVoteTime(System.currentTimeMillis());
            voteRecordDto.setCode(ToolUtilities.allockUUIDWithUnderline());
            if (votePollDto.isAnonymous() && isAnonymous) {
                voteRecordDto.setUserID(null).setUserFullName(null);
            }
            //更新投票选项统计数
            VoteOptionDto voteOptionDto = votePollDto.getVoteOption(voteOption);
            if (voteOptionDto != null) {
                try (IDao dao = IDaoService.newIDao()) {
                    Form form = DtoConvertUtil.convertToForm(voteRecordDto);
                    form = IFormMgr.get().createForm(dao, form);
                    dao.commit();
                    //懒处理更新统计值，避免并发更新导致统计值错误
                    voteOptionLazyPool.add(new Pair<>(INC_VOTEOPTION,voteOptionDto));
                }
            }
        }

    }

    public VoteRecordDto queryVoteRecord(String uuid) throws Exception {
        try(IDao dao = IDaoService.newIDao()){
            Form form = IFormMgr.get().queryForm(dao,VoteRecordDto.FormModelId,uuid);
            if(form == null){
                return null;
            }
            return DtoConvertUtil.convertToDto(form,VoteRecordDto.class,true);
        }
    }

    public VoteRecordDto queryUserVoteRecord(VotePollDto votePoll,UserSession userSession) throws Exception {
        try(IDao dao = IDaoService.newIDao()){
//            VotePollDto votePollDto = queryVotePoll(voteUuid);
//            if(votePollDto == null){
//                return null;
//            }
            Cnd cnd = Cnd.NEW();
            // 根据投票模式切换查询方式
            VoteMode voteMode = votePoll.getVoteModeEnum();
            if(voteMode == VoteMode.一客户端一票) {
                cnd.and(new SqlExpressionGroup()
                        .andEquals(VoteRecordDto.FieldCode_DeviceId,userSession.getDeviceId()));
            }else if(voteMode == VoteMode.一人一票){
                cnd.and(new SqlExpressionGroup().andEquals(VoteRecordDto.FieldCode_UserID,userSession.getUserCode()));
            }else {
            	return null;
            }
            cnd.and(new SqlExpressionGroup().andEquals(VoteRecordDto.FieldCode_VoteID,votePoll.getUuid()));
            ResultSet<Form> rs = IFormMgr.get().queryFormPage(dao,VoteRecordDto.FormModelId,cnd,1,1,true,true);
            if(rs.isEmpty()){
                return null;
            }
            Form form = rs.getDataList().get(0);
            return DtoConvertUtil.convertToDto(form,VoteRecordDto.class,true);
        }
    }
    
    protected VoteRecordDto queryUserVoteRecordByCnd(String voteUuid,Cnd cnd) throws Exception {
    	try(IDao dao = IDaoService.newIDao()){
    		cnd.and(new SqlExpressionGroup().andEquals(VoteRecordDto.FieldCode_VoteID,voteUuid));
    		ResultSet<Form> rs = IFormMgr.get().queryFormPage(dao,VoteRecordDto.FormModelId,cnd,1,1,true,true);
    		if(rs.isEmpty()){
    			return null;
    		}
    		Form form = rs.getDataList().get(0);
    		return DtoConvertUtil.convertToDto(form,VoteRecordDto.class,true);
    	}
    }

    protected List<VoteRecordDto> queryVoteRecords(String voteUuid) throws Exception {
        Cnd cnd = Cnd.NEW();
        cnd.and(new SqlExpressionGroup().andEquals(VoteRecordDto.FieldCode_VoteID,voteUuid));
        try(IDao dao = IDaoService.newIDao()){
            ResultSet<Form> rs = IFormMgr.get().queryFormPage(dao,VoteRecordDto.FormModelId,cnd,1,Integer.MAX_VALUE,true,true);
            List<VoteRecordDto> list = new ArrayList<>();
            for(Form form : rs.getDataList()){
                list.add(DtoConvertUtil.convertToDto(form,VoteRecordDto.class,true));
            }
            return list;
        }
    }

    @Override
    public UserVotePollDto queryUserVotePoll(String userCode, String voteUuid) throws Exception {
        Cnd cnd = Cnd.NEW();
        VotePollDto votePoll = queryVotePoll(voteUuid);
        cnd.and(new SqlExpressionGroup().andEquals(VotePollDto.FieldCode_Uuid,voteUuid));
        ResultSet<UserVotePollDto> rs = queryUserVotePollPage(userCode,cnd,1,1);
        if(rs.isEmpty()){
        	if(votePoll != null) {
	        	UserVotePollDto userVote = new UserVotePollDto();
	        	ToolUtilities.copyFields(votePoll, userVote);
	        	userVote.setTotalCount(votePoll.getVoteParticipant().size());
                userVote.setSelectedOption(null);
	            return userVote;
        	}
        	return null;
        }else {
            UserVotePollDto userVote = rs.getDataList().get(0);
            userVote.setTotalCount(votePoll.getVoteParticipant().size());
            return userVote;
        }
    }
    
    @Override
    public UserVotePollDto queryDeviceVotePoll(String deviceId, String voteUuid) throws Exception {
    	VotePollDto votePoll = queryVotePoll(voteUuid);
    	if(votePoll == null)
    		return null;
    	for(VoteParticipantDto voteParticipant : NullUtil.get(votePoll.getVoteParticipant())){
    		if(CmnUtil.isStringEqual(voteParticipant.getDeviceId(), deviceId)) {
    			UserVotePollDto userVote = new UserVotePollDto();
    			ToolUtilities.copyFields(votePoll, userVote);
    			Cnd cnd = Cnd.where(new SqlExpressionGroup().andEquals(VoteRecordDto.FieldCode_DeviceId, deviceId));
    			VoteRecordDto voteRecord = queryUserVoteRecordByCnd(voteUuid, cnd);
    			if(voteRecord != null) {
    				userVote.setSelectedOption(voteRecord.getVoteOptionID());
    			}
    			userVote.setTotalCount(votePoll.getVoteParticipant().size());
    			return userVote;
    		}
    	}
    	return null;
    }

    @Override
    public List<VotePollDto> getRunningVotePollList() {
        return new ArrayList<>(votePollMap.values());
    }

}
