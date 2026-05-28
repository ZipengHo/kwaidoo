package cell.bap.test;

import com.leavay.ms.tool.CmnUtil;

import crpc.BasicCallback;

public class DemoDaoImpl extends BasicCallback implements DemoDao
{
    private static final long serialVersionUID = -7818200528306743216L;

    public void commit(String msg)
    {
        // 主要看这里断在哪个调试进程里
        CmnUtil.out("[DemoDao]Commit : " +msg);
    }

    public void insert(String msg)
    {
        CmnUtil.out("[DemoDao]Insert : " + msg);
    }

    public void delete(String msg)
    {
        CmnUtil.out("[DemoDao]Delete : " + msg);
    }

    public void update(String msg)
    {
        CmnUtil.out("[DemoDao]Update : " + msg);
    }
}
