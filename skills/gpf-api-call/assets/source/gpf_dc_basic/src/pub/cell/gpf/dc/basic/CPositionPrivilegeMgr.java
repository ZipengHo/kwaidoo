package cell.gpf.dc.basic;

import bap.cells.BasicCell;
import cell.cdao.IDao;
import cell.fe.gpf.dc.basic.IApplicationService;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.adur.role.IRoleMgr;
import com.kwaidoo.ms.tool.ToolUtilities;
import gpf.adur.data.Form;
import gpf.adur.data.ResultSet;
import gpf.adur.role.Role;
import gpf.dc.basic.dto.privilege.PositionPrivilegeDto;
import gpf.dc.basic.dto.privilege.nesting.MenuSetDto;
import gpf.dc.basic.i18n.GpfDCBasicI18n;
import gpf.dc.basic.param.view.dto.ApplicationSetting;
import gpf.dc.util.DtoConvertUtil;
import gpf.exception.VerifyException;
import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import java.util.Collections;
import java.util.List;

public class CPositionPrivilegeMgr extends BasicCell implements IPositionPrivilegeMgr {
    @Override
    public List<MenuSetDto> queryPositionMenuRelationList(IDao dao, String positionUuid, String applicationUuid) throws Exception {
        Cnd cnd = Cnd.where(new SqlExpressionGroup().andEquals(PositionPrivilegeDto.FieldCode_PositionUuid,positionUuid)
                .andEquals(PositionPrivilegeDto.FieldCode_ApplicationUuid,applicationUuid));
        ResultSet<Form> rs = IFormMgr.get().queryFormPage(dao, PositionPrivilegeDto.FormModelId,cnd,1,1,true,true);
        if(rs.isEmpty()){
            return Collections.emptyList();
        }else{
            PositionPrivilegeDto positionMenuRelationDto = DtoConvertUtil.convertToDto(rs.getDataList().get(0), PositionPrivilegeDto.class,true);
            if(positionMenuRelationDto.getMenuSet() == null)
                return Collections.emptyList();
            else
                return positionMenuRelationDto.getMenuSet();
        }
    }

    @Override
    public void savePositionMenuRelationList(IDao dao, String positionUuid, String applicationUuid, List<MenuSetDto> menuSet) throws Exception {
        IRoleMgr roleMgr = IRoleMgr.get();
        Role role = roleMgr.queryRole(dao,positionUuid);
        if(role == null)
            throw new VerifyException(GpfDCBasicI18n.getString("岗位[{1}]不存在！",positionUuid));
        ApplicationSetting appSettting = IApplicationService.get().queryApplicationSetting(applicationUuid);
        if(appSettting == null)
            throw  new VerifyException(GpfDCBasicI18n.getString("应用[{1}]不存在！",applicationUuid));
        Cnd cnd = Cnd.where(new SqlExpressionGroup().andEquals(PositionPrivilegeDto.FieldCode_PositionUuid,positionUuid)
                .andEquals(PositionPrivilegeDto.FieldCode_ApplicationUuid,applicationUuid));
        ResultSet<Form> rs = IFormMgr.get().queryFormPage(dao, PositionPrivilegeDto.FormModelId,cnd,1,1,true,true);
        if(rs.isEmpty()){
            PositionPrivilegeDto positionMenuRelationDto = new PositionPrivilegeDto();
            positionMenuRelationDto.setPositionUuid(positionUuid)
                    .setPositionName(role.getLabel())
                    .setApplicationUuid(applicationUuid)
                    .setApplicationName(appSettting.getName()).setMenuSet(menuSet);
            positionMenuRelationDto.setCode(ToolUtilities.allockUUIDWithUnderline());
            Form form = DtoConvertUtil.convertToForm(positionMenuRelationDto);
            IFormMgr.get().createForm(dao,form);
        }else{
            PositionPrivilegeDto positionMenuRelationDto = DtoConvertUtil.convertToDto(rs.getDataList().get(0), PositionPrivilegeDto.class,true);
            positionMenuRelationDto.setPositionUuid(positionUuid)
                    .setPositionName(role.getLabel())
                    .setApplicationUuid(applicationUuid)
                    .setApplicationName(appSettting.getName()).setMenuSet(menuSet);
            Form form = DtoConvertUtil.convertToForm(positionMenuRelationDto);
            IFormMgr.get().updateForm(dao,form);
        }
    }
}
