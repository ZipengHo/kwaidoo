package gpf.dc.basic.dto.privilege;

import java.io.Serializable;
import java.util.List;

import gpf.anotation.FieldMeta;
import gpf.adur.data.DataType;
import gpf.dc.basic.dto.privilege.nesting.MenuSetDto;
import gpf.dc.dto.BusinessModelDto;

/**
 * 岗位菜单关系
 *
 */
public class PositionPrivilegeDto extends BusinessModelDto implements Serializable{
    public final static String FormModelId = "gpf.md.basic.PositionPrivilege";
    public final static String FieldCode_PositionUuid = "gang3Wei4Uuid";
    public final static String sPositionUuid = "岗位Uuid";
    public final static String FieldCode_PositionName = "gang3Wei4Ming2Cheng1";
    public final static String sPositionName = "岗位名称";
    public final static String FieldCode_ApplicationUuid = "ying1Yong4Uuid";
    public final static String sApplicationUuid = "应用Uuid";
    public final static String FieldCode_ApplicationName = "ying1Yong4Ming2Cheng1";
    public final static String sApplicationName = "应用名称";
    public final static String FieldCode_MenuSet = "cai4Dan1Pei4Jhih4Lie4Biao3";
    public final static String sMenuSet = "菜单配置列表";
    @FieldMeta(code = FieldCode_PositionUuid,name = sPositionUuid, dataType = DataType.Depend)
    String positionUuid;
    @FieldMeta(code = FieldCode_PositionName,name = sPositionName, dataType = DataType.Text)
    String positionName;
    @FieldMeta(code = FieldCode_ApplicationUuid,name = sApplicationUuid, dataType = DataType.Depend)
    String applicationUuid;
    @FieldMeta(code = FieldCode_ApplicationName,name = sApplicationName, dataType = DataType.Text)
    String applicationName;
    @FieldMeta(code = FieldCode_MenuSet,name = sMenuSet, dataType = DataType.NestingModel, tableModel = MenuSetDto.class)
    List<MenuSetDto> menuSet;

    public String getPositionUuid() {
        return positionUuid;
    }

    public PositionPrivilegeDto setPositionUuid(String positionUuid) {
        this.positionUuid = positionUuid;
        return this;
    }

    public String getPositionName() {
        return positionName;
    }

    public PositionPrivilegeDto setPositionName(String positionName) {
        this.positionName = positionName;
        return this;
    }

    public String getApplicationUuid() {
        return applicationUuid;
    }

    public PositionPrivilegeDto setApplicationUuid(String applicationUuid) {
        this.applicationUuid = applicationUuid;
        return this;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public PositionPrivilegeDto setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public List<MenuSetDto> getMenuSet() {
        return menuSet;
    }

    public PositionPrivilegeDto setMenuSet(List<MenuSetDto> menuSet) {
        this.menuSet = menuSet;
        return this;
    }
}
