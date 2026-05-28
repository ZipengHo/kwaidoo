package bap.cells.hook.test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.Cnd;

import com.cdao.model.CDoNamed;
import com.leavay.common.util.ToolUtilities;
import com.leavay.ms.tool.CmnUtil;

import bap.cells.CellServerFactory;
import bap.cells.Cells;
import bap.cells.hook.CellHookPrinter;
import bap.cells.hook.CellLocalHook;
import cell.bap.test.TestDaoCell;
import cell.bap.test.TestDaoImpl;
import cell.cdao.IDao;
import cell.cdao.IDaoService;

// 测试挂接一个hook到TestDaoCell上， 监控打印该Cell下各个方法的调用
// 该测试类只能在主控上执行, 是直接作用在CELL中央服务模块里的HOOK系统
public class TestCellHook
{

    public static void test() throws Exception
    {
        try (CellLocalHook hook = attachHook()) // 用完后会自动注销自己
        {
//            runTestData();
            
            runTestException();
        }
    }
    
    public static CellLocalHook attachHook() throws Exception
    {
        return CellServerFactory.getMe().attachLocalHookBuilder(TestDaoImpl.class, ToolUtilities.newLinkedList(CellHookPrinter.class.getName()), ToolUtilities.newLinkedList(CellHookPrinter.class.getName()), true);
    }
    
    public static void runTestException()
    {
        try
        {
            TestDaoCell.get().testException1();
        } catch (Throwable exp)
        {
//            exp.printStackTrace();
        }
        

        try
        {
            TestDaoCell.get().testException2();
        } catch (Throwable exp)
        {
//            exp.printStackTrace();
        }
        
    }
    
    public static void runTestData() throws Exception
    {
        List<String> names = new LinkedList();
        for (int i=0;i<100;i++)
        {
            String name = "__TestCellHook__:"+ToolUtilities.allockUUID()+"("+i+")";
            Cells.get(TestDaoCell.class).test(name);
            names.add(name);
        }
       
        try(IDao dao = IDaoService.newIDao())
        {
            int rows = dao.deleteDos(CDoNamed.class.getName(), Cnd.where(CDoNamed.Name, "like", "%__TestCellHook__:%"));
            dao.commit();
            
            CmnUtil.err("Clear rows : " + rows);
        }
    }
    
}
