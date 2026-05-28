package tiny.service.hook;

import com.leavay.common.util.ToolUtilities;
import com.leavay.nio.crpc.CRpcClient;
import com.leavay.nio.crpc.TsRequest;

import tiny.service.cmn.TsHook;
import tiny.service.cmn.member.TsMember;
import tiny.service.cmn.member.TsMemberCache;
import tiny.service.cmn.member.TsProvider;
import tiny.service.md.TsAlarm;

public class AlarmHook implements TsHook
{

    public void before(CRpcClient target, TsRequest request)
    {
        
    }

    public void after(CRpcClient target, TsRequest request, Object result)
    {
        
    }

    public void failed(CRpcClient target, TsRequest request, Throwable error)
    {
        try
        {
            TsAlarm alarm = TsAlarm.newConsumerAlarm(target, request, error);
    
            TsMember.get().reportAlarm(alarm);
        } catch (Exception exp)
        {
            ToolUtilities.error("Ts Alarm Hook", "Failed to report alarm : "+request, exp);
        }
        
    }

}
