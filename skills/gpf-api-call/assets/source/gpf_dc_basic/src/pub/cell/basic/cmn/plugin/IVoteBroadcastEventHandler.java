package cell.basic.cmn.plugin;

import com.kwaidoo.ms.tool.CmnUtil;

import basic.cmn.dto.vote.UserVotePollDto;
import basic.cmn.dto.vote.VoteMode;
import basic.cmn.dto.vote.VotePollDto;
import cell.CellIntf;
import cell.basic.cmn.vote.IVoteService;
import cell.cmn.session.ISessionService;
import cmn.dto.session.UserSession;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import fe.cmn.panel.PanelContext;
import fe.cmn.panel.PanelDto;
import fe.cmn.panel.ability.PopDialog;
import fe.cmn.widget.WindowSizeDto;
import gpf.dc.basic.fe.component.event.FeBroadcastEvent;
import gpf.dc.basic.fe.component.vote.UserVoteView;
import gpf.dc.basic.fe.intf.FeBroadcastEventHandler;

public interface IVoteBroadcastEventHandler extends CellIntf,FeBroadcastEventHandler{

	@Override
	default Object onEvent(PanelContext context, FeBroadcastEvent event) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		UserSession userSession = ISessionService.get().get(context.getAppUuid());
		tracer.info("userSession = "+userSession);
		if(userSession == null)
			return null;
		String uuid = (String) event.getPayload().get("uuid");
		IVoteService voteService = IVoteService.get();
		VotePollDto votePoll = voteService.queryVotePoll(uuid);
		if(votePoll == null)
			return null;
		if(votePoll.getVoteModeEnum() == VoteMode.一客户端一票) {
			UserVotePollDto userVote = voteService.queryDeviceVotePoll(userSession.getDeviceId(), uuid);
			tracer.info("userVote = " + userVote);
			if(userVote != null && CmnUtil.isStringEmpty(userVote.getSelectedOption())) {
				String voteSubject = (String) event.getPayload().get("voteSubject");
				String voteDesc = (String) event.getPayload().get("voteDesc");
//				PopDialog.showConfirm(context, "发布插件通知", event.getPayload()+"");
				PanelDto panel = UserVoteView.NEW(context, "", userVote);
				panel.setPreferSize(WindowSizeDto.all(0.3, 0.3));
				PopDialog.show(context, voteSubject, panel);
			}
		}else if(votePoll.getVoteModeEnum() == VoteMode.一人一票) {
			UserVotePollDto userVote = IVoteService.get().queryUserVotePoll(context.getCurrentUser(), uuid);
			tracer.info("userVote = " + userVote);
			if(userVote != null && CmnUtil.isStringEmpty(userVote.getSelectedOption())) {
				String voteSubject = (String) event.getPayload().get("voteSubject");
				String voteDesc = (String) event.getPayload().get("voteDesc");
//			PopDialog.showConfirm(context, "发布插件通知", event.getPayload()+"");
				PanelDto panel = UserVoteView.NEW(context, "", userVote);
				panel.setPreferSize(WindowSizeDto.all(0.3, 0.3));
				PopDialog.show(context, voteSubject, panel);
			}
		}
		return null;
	}
}

