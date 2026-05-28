package cell.bap.test;

import bap.cells.SimpleServiceCell;

public class CDefaultTestPluginCell extends SimpleServiceCell implements ITestPluginCell
{

    public String test2(Object o)
    {
        return this.getClass()+" = " + o;
    }

}
