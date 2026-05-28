package cell.bap.test;

import bap.cells.Cells;
import cell.ServiceCellIntf;

public interface ITestPluginCell extends ServiceCellIntf
{
    static ITestPluginCell get()
    {
        return Cells.get(ITestPluginCell.class);
    }

    default String test(Object o)
    {
        return "DEFAULT LOGIC : " + o;
    }
    
    String test2(Object o);
}
