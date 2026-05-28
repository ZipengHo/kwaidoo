package gpf.dc.basic.dto.privilege;

import com.kwaidoo.ms.tool.CmnUtil;

import gpf.dto.model.data.ActionPrivilegeDto;
import gpf.dto.model.data.FieldPrivilegeDto;

public enum PrivilegeRuleEnum {

    None("N"),ReadOnly("R"),WriteOnly("W"),ReadWrite("RW"),Require("W*"),Excutable("X");
    String value;
    PrivilegeRuleEnum(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    public static PrivilegeRuleEnum formValue(String value) {
        if(CmnUtil.isStringEmpty(value))
            return null;
        for(PrivilegeRuleEnum item :values()) {
            if(CmnUtil.isStringEqual(item.getValue(), value))
                return item;
        }
        return null;
    }

    public static void setFieldPrivielge(PrivilegeRuleEnum ruleEnum,FieldPrivilegeDto privilege) {
        if(ruleEnum == null)
            return;
        if(ruleEnum == PrivilegeRuleEnum.None) {
            privilege.setVisible(false).setWritable(false);
        }
        else if(ruleEnum == PrivilegeRuleEnum.ReadOnly)
            privilege.setVisible(true);
        else if(ruleEnum == PrivilegeRuleEnum.WriteOnly)
            privilege.setWritable(true);
        else if(ruleEnum == PrivilegeRuleEnum.ReadWrite)
            privilege.setVisible(true).setWritable(true);
        else if(ruleEnum == PrivilegeRuleEnum.Require)
            privilege.setWritable(true).setRequire(true);
    }

    public static void setActionPrivielge(PrivilegeRuleEnum ruleEnum,ActionPrivilegeDto privilege) {
        if(ruleEnum == null)
            return;
        if(ruleEnum == PrivilegeRuleEnum.None)
            privilege.setVisible(false).setOperatable(false);
        else if(ruleEnum == PrivilegeRuleEnum.ReadOnly)
            privilege.setVisible(true);
        else if(ruleEnum == PrivilegeRuleEnum.Excutable)
            privilege.setVisible(true).setOperatable(true);
    }
}
