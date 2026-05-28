package cell.bap.test;

import cell.CellIntf;

public interface TestCell1 extends CellIntf
{
    public default Object test(Object o)
    {
        return o;
    }
}
