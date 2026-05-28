package cell.bap.test;

import java.io.IOException;

import com.cdao.model.CDoNamed;
import com.leavay.common.util.ToolUtilities;

import bap.cells.BasicCell;
import bap.cells.Cells;
import bap.cells.annotation.After;
import bap.cells.hook.CellHookPrinter;
import cell.cdao.IDao;
import cell.cdao.IDaoService;

public class TestDaoImpl extends BasicCell implements TestDaoCell
{
    @After(CellHookPrinter.class)
    public Object test(Object o) throws Exception
    {
        try(IDao dao = IDaoService.get().newDao())
        {
            Object oo;
            try (DemoCell2 c2 = Cells.get(DemoCell2.class))
            {
                oo = c2.testRealDao(dao, "New name : " +  o);
                CDoNamed nn = (CDoNamed) oo;
                
                
                nn.setName("Update By Outside : " + nn.getName());
                dao.updateDo(nn);
            }
            
            dao.commit();
            return oo;
        }
    }

    @Override
    public void testException1() throws IOException
    {
        throw new IOException("Test IO Exception");
    }

    @Override
    public void testException2() throws NullPointerException, Exception
    {
        if (ToolUtilities.randomRange(1000) % 2 == 0)
            throw new NullPointerException("Test NullPointerException");
        else
            throw new Exception("Test General Exception");
    }

    @Override
    public void testVoid()
    {
        // TODO 自动生成的方法存根
        
    }

}
