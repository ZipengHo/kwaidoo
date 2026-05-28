package cell.bap;

import com.cdao.model.CDoUser;
import com.leavay.common.util.ToolBasic;

import bap.cells.CellLevel;
import bap.cells.Cells;
import bap.cells.SimpleServiceCell;
import cell.cdao.IDaoService;
import cell.nio.ws.IWsLogin;
import cell.nio.ws.WsSession;

/**
 * 最粗浅、简单的实现，外围项目需根据实际需要，用更高LEVEL的CELL来替代此逻辑
 */

@CellLevel(CellLevel.LEVEL_NORMAL + 1) // 设置为稍高级别，覆盖最低级别实现（如DFC里的默认实现）
public class CBapWsLogin extends SimpleServiceCell implements IWsLogin
{
    public WsSession login(String name, String pwd) throws Exception
    {
        CDoUser user = Cells.get(IDaoService.class).loginWithUser(name, pwd);
        Long tokenExpireMin = user.getTokenExpireTime();
        if (tokenExpireMin == null)
        {
            tokenExpireMin = 24*60L;
        }
     

        WsSession wsSession = new WsSession();
        wsSession.setSessionId(user.getUuid() + "-" + ToolBasic.allocRandomID());
        wsSession.setUserAlias(user.getAlias());
        wsSession.setExpirtSecond(tokenExpireMin*60);

        return wsSession;
    }

    public void logout(String sessionId) throws Exception
    {
    }
}
