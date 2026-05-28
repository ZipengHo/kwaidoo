package cell.basic.cmn.plugin;

import basic.cmn.event.EventHandlerInitParameter;
import cell.CellPreloadIntf;
import cell.ServiceCellIntf;
import cell.basic.cmn.event.EventHandler;

public interface IVotePluginPublishEventHandler extends ServiceCellIntf, CellPreloadIntf, EventHandler<EventHandlerInitParameter> {

    public void onPluginVoteEnd(String voteId,String voteSubject,String voteDesc)throws Exception;
}
