package cell.gpf.dc.basic;

import bap.cells.Cells;
import cell.CellIntf;
import cell.cdao.IDao;
import gpf.dc.basic.dto.privilege.nesting.MenuSetDto;

import java.util.List;

/**
 * 岗位菜单关系管理
 */
public interface IPositionPrivilegeMgr extends CellIntf {
    static IPositionPrivilegeMgr get(){
        return Cells.get(IPositionPrivilegeMgr.class);
    }

    /**
     * 查询岗位关联的菜单
     * @param dao
     * @param positionUuid
     * @param applicationUuid
     * @return
     * @throws Exception
     */
    public List<MenuSetDto> queryPositionMenuRelationList(IDao dao, String positionUuid,String applicationUuid)throws Exception;

    /**
     * 保存岗位关联的菜单
     * @param dao
     * @param positionUuid
     * @param applicationUuid
     * @param menuSet
     * @throws Exception
     */
    public void savePositionMenuRelationList(IDao dao, String positionUuid, String applicationUuid,List<MenuSetDto> menuSet)throws Exception;
}
