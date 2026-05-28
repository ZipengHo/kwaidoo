package bap.cells;

import cell.CellIntf;

public interface CellBuilderIntf
{
    public String getInterfaceClass();
    public String getImplementClass();

    default public int getLevel() {
        return CellLevel.LEVEL_NORMAL;
    }
    
    public default CellIntf tryGetSingletone() {
        return null;
    }
    
    // 构建一个cell
    public CellIntf buildCell(Object... params) throws ClassNotFoundException, Exception;
}
