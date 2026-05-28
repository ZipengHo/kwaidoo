package cell.bap.test;

import java.io.IOException;

import bap.cells.Cells;
import cell.CellIntf;

public interface TestDaoCell extends  CellIntf
{
    public static TestDaoCell get() {return Cells.get(TestDaoCell.class);}
    
    public Object test(Object o) throws Exception;
    
    public void testVoid();
    
    public void testException1() throws IOException;

    public void testException2() throws NullPointerException, Exception;
}

