package bap.cells.hook;

import com.leavay.common.util.ToolUtilities;

import bap.cells.CellServerFactory;
import bap.cells.RmtCellBuilderIntf;

public class CellLocalHook implements AutoCloseable
{
    protected String _user;

    protected RmtCellBuilderIntf _builder;

    public CellLocalHook(String user, RmtCellBuilderIntf builder)
    {
        this._user = user;
        this._builder = builder;
    }

    @Override
    public void close() throws Exception
    {
        // 销毁自己
        RmtCellBuilderIntf builder = CellServerFactory.getMe().getRemoteBuilder(_builder.getInterfaceClass());
        if (builder != _builder)
        {
            ToolUtilities.warnAndOutput("CellLocalHook", "Local builder is wild and will destroy itself");
            return;
        }
        CellServerFactory.getMe().removeInvalidRemote(_builder.getInterfaceClass(), _builder);
    }
}
