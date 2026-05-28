package gpf.dc.basic.dto.privilege.nesting;

import gpf.adur.data.DataType;
import gpf.anotation.FieldMeta;
import gpf.dc.dto.NestingDto;

import java.io.Serializable;

/**
 * 菜单配置列表
 *
 */
public class MenuSetDto extends NestingDto implements Serializable{
    public final static String FormModelId = "gpf.md.slave.MenuSet";
    public final static String FieldCode_MenuUuid = "cai4Dan1Uuid";
    public final static String sMenuUuid = "菜单Uuid";
    public final static String FieldCode_MenuPath = "cai4Dan1Lu4Jing4";
    public final static String sMenuPath = "菜单路径";
    @FieldMeta(code = FieldCode_MenuUuid,name = sMenuUuid, dataType = DataType.Depend)
    String menuUuid;
    @FieldMeta(code = FieldCode_MenuPath,name = sMenuPath, dataType = DataType.Text)
    String menuPath;

    public String getMenuUuid() {
        return menuUuid;
    }

    public MenuSetDto setMenuUuid(String menuUuid) {
        this.menuUuid = menuUuid;
        return this;
    }

    public String getMenuPath() {
        return menuPath;
    }

    public MenuSetDto setMenuPath(String menuPath) {
        this.menuPath = menuPath;
        return this;
    }
}
