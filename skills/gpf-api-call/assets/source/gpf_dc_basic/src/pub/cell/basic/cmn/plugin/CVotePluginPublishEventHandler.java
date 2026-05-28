package cell.basic.cmn.plugin;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.kwaidoo.ms.tool.CmnUtil;
import com.leavay.common.util.javac.ClassFactory;
import com.leavay.nio.crpc.RpcMap;

import bap.cells.BasicServiceCell;
import bap.md.java.CJavaProject;
import basic.cmn.dto.event.EventDto;
import basic.cmn.dto.event.EventSubscriptionDto;
import basic.cmn.dto.vote.VoteOptionDto;
import basic.cmn.dto.vote.VotePollDto;
import basic.cmn.event.EventHandlerInitParameter;
import basic.cmn.event.EventInvokeMode;
import cell.basic.cmn.event.IEventBus;
import cell.basic.cmn.vote.IVoteService;
import cell.fe.gpf.dc.basic.IApplicationService;
import cell.gpf.dc.basic.IExpressionMgr;
import cell.gpf.dc.concrete.IJavaProjectMgr;
import cmn.util.JsonUtil;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.dc.basic.fe.component.event.FeBroadcastEvent;

public class CVotePluginPublishEventHandler extends BasicServiceCell implements IVotePluginPublishEventHandler {

    @Override
    protected void doStartService() throws Exception {
        try{
            IVoteService.get();
            subscribePluginVoteEvent();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doStopService() {

    }

    public void subscribePluginVoteEvent() throws Exception {
    	try {
    		ClassFactory.loadClass(EventSubscriptionDto.FormModelId);
    	}catch (Exception e) {
    		return;
		}
    	EventSubscriptionDto voteStartEventSubscriptionDto = new EventSubscriptionDto();
        voteStartEventSubscriptionDto.setEventCode(IVoteService.VOTE_START_EVENT)
                .setEventSourceRegex(IVoteService.class.getName())
                .setEventHandler(IVotePluginPublishEventHandler.class.getName())
                .setSubscriber(IJavaProjectMgr.class.getName())
                .setInvokeMode(EventInvokeMode.异步)
                ;
        voteStartEventSubscriptionDto.setCode("发布插件投票通知");
        saveEventSubscription(voteStartEventSubscriptionDto);
    	
        EventSubscriptionDto voteEndEventSubscriptionDto = new EventSubscriptionDto();
        voteEndEventSubscriptionDto.setEventCode(IVoteService.VOTE_END_EVENT)
                .setEventSourceRegex(IVoteService.class.getName())
                .setEventHandler(IVotePluginPublishEventHandler.class.getName())
                .setSubscriber(IJavaProjectMgr.class.getName())
                .setInvokeMode(EventInvokeMode.异步)
                ;
        voteEndEventSubscriptionDto.setCode("发布插件投票结束");
        saveEventSubscription(voteEndEventSubscriptionDto);
    }
    
    protected void saveEventSubscription(EventSubscriptionDto eventSubscriptionDto) throws Exception {
    	EventSubscriptionDto existEventSubscriptionDto = IEventBus.get().queryEventSubscriptionByCode(eventSubscriptionDto.getCode());
        if(existEventSubscriptionDto!=null){
//            eventSubscriptionDto.setUuid(existEventSubscriptionDto.getUuid());
//            IEventBus.get().updateEventSubscription(eventSubscriptionDto);
        }else{
            IEventBus.get().createEventSubscription(eventSubscriptionDto);
        }
	}

    @Override
    public void onPluginVoteEnd(String voteId,String voteSubject,String voteDesc) throws Exception {
        //从voteDesc中匹配{}中的内容，如果有内容，提取除工程名称
        Tracer tracer = TraceUtil.getCurrentTracer();
        Map<String,String> map = JsonUtil.fromJson(voteDesc, Map.class);
        String publishType = map.get("操作");
        String projectName = map.get("工程");
        tracer.info("接收到 投票结束事件 ，执行操作：" + voteDesc);
        if(CmnUtil.isStringEqual(publishType, "重载插件")) {
        	IJavaProjectMgr.get().reloadPlugin();
        }else if(CmnUtil.isStringEqual(publishType, "重建工程插件")) {
        	CJavaProject project = IJavaProjectMgr.get().queryProjectByName(projectName);
        	if(project != null) {
        		IJavaProjectMgr.get().publishProject(project.getUuid());
        	}
        }
    }

    @Override
    public Object onEvent(EventHandlerInitParameter initParameter, RpcMap<Object> context, EventDto event) throws Exception {
        Tracer tracer = TraceUtil.getCurrentTracer();
        tracer.info("接收到 投票事件：" + event.getEventCode()+", data : " + JsonUtil.toJson(event));
        if(event.getEventSource().equals(IVoteService.class.getName())){
            String voteId = (String) event.getPayloadValue("uuid");
            String voteSubject = (String) event.getPayloadValue("voteSubject");
            String voteDesc = (String) event.getPayloadValue("voteDesc");
            if(event.getEventCode().equals(IVoteService.VOTE_START_EVENT)){
            	FeBroadcastEvent feEvent = new FeBroadcastEvent(IVoteService.VOTE_START_EVENT, IVoteBroadcastEventHandler.class, event.getPayload());
            	IApplicationService.get().putBroadcastEvent(feEvent);
            }else if(event.getEventCode().equals(IVoteService.VOTE_END_EVENT)){
                //查看投票结果，是否全员同意
                VotePollDto votePollDto = IVoteService.get().queryVotePoll(voteId);
                if(votePollDto!=null){
                    int disAgreeCount = 0;
                    for(VoteOptionDto voteOptionDto : votePollDto.getVoteOption()){
                        if(CmnUtil.isStringEqual(voteOptionDto.getOption(),"不同意")){
                            disAgreeCount = CmnUtil.getInteger(voteOptionDto.getVoteCount(),0);
                        }
                    }
                    //没有人拒绝，未投票也算是同意
                    if(disAgreeCount == 0)
                        onPluginVoteEnd(voteId,voteSubject,voteDesc);
                    else{
                        tracer.info("接收到 投票结束事件 ： 有用户拒绝投票，不发布插件");
                    }
                }
            }
        }
        return null;
    }


}
