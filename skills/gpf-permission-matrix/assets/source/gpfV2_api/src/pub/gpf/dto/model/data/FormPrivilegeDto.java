package gpf.dto.model.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FormPrivilegeDto implements Serializable {

    private static final long serialVersionUID = 3170949818882523246L;

    /**
     * 动作权限
     */
    List<ActionPrivilegeDto> actionPrivileges = new ArrayList<>();

    /**
     * 属性权限
     */
    List<FieldPrivilegeDto> fieldPrivileges = new ArrayList<>();

    /**
     * 分组权限
     */
    List<GroupPrivilegeDto> groupPrivileges = new ArrayList<>();

    public List<ActionPrivilegeDto> getActionPrivileges() {
        return actionPrivileges;
    }

    public Map<String, ActionPrivilegeDto> getActionPrivilegeMap() {
        Map<String, ActionPrivilegeDto> map = new LinkedHashMap<String, ActionPrivilegeDto>();
        for (ActionPrivilegeDto action : actionPrivileges) {
            map.put(action.getName(), action);
        }
        return map;
    }

    public FormPrivilegeDto setActionPrivileges(List<ActionPrivilegeDto> actionPrivileges) {
        this.actionPrivileges = actionPrivileges;
        return this;
    }

    public List<FieldPrivilegeDto> getFieldPrivileges() {
        return fieldPrivileges;
    }

    public FormPrivilegeDto setFieldPrivileges(List<FieldPrivilegeDto> fieldPrivileges) {
        this.fieldPrivileges = fieldPrivileges;
        return this;
    }

    public Map<String, FieldPrivilegeDto> getFieldPrivilegeMap() {
        Map<String, FieldPrivilegeDto> map = new LinkedHashMap<String, FieldPrivilegeDto>();
        for (FieldPrivilegeDto fieldPriv : fieldPrivileges) {
            map.put(fieldPriv.getField(), fieldPriv);
        }
        return map;
    }

    public List<GroupPrivilegeDto> getGroupPrivileges() {
        return groupPrivileges;
    }

    public FormPrivilegeDto setGroupPrivileges(List<GroupPrivilegeDto> groupPrivileges) {
        this.groupPrivileges = groupPrivileges;
        return this;
    }

    public Map<String, GroupPrivilegeDto> getGroupPrivilegeMap() {
        Map<String, GroupPrivilegeDto> map = new LinkedHashMap<String, GroupPrivilegeDto>();
        for (GroupPrivilegeDto fieldPriv : groupPrivileges) {
            map.put(fieldPriv.getName(), fieldPriv);
        }
        return map;
    }

    public static FormPrivilegeDto megerFormPrivilegeDto(FormPrivilegeDto orgPrivilege, FormPrivilegeDto newPrivilege) {
        if (orgPrivilege == null) {
            return newPrivilege;
        }
        if (newPrivilege == null) {
            return orgPrivilege;
        }
        Map<String, FieldPrivilegeDto> orgFieldPrivs = orgPrivilege.getFieldPrivilegeMap();
        for (FieldPrivilegeDto newPriv : newPrivilege.getFieldPrivileges()) {
            if (!orgFieldPrivs.containsKey(newPriv.getField())) {
                orgFieldPrivs.put(newPriv.getField(), newPriv);
            } else {
                FieldPrivilegeDto orgFieldPriv = orgFieldPrivs.get(newPriv.getField());
                if (newPriv.isVisible()) {
                    orgFieldPriv.setVisible(true);
                }
                if (newPriv.isWritable()) {
                    orgFieldPriv.setWritable(true);
                }
            }
        }
        Map<String, ActionPrivilegeDto> orgActionPrivs = orgPrivilege.getActionPrivilegeMap();
        for (ActionPrivilegeDto newPriv : newPrivilege.getActionPrivileges()) {
            if (!orgActionPrivs.containsKey(newPriv.getName())) {
                orgActionPrivs.put(newPriv.getName(), newPriv);
            } else {
                ActionPrivilegeDto orgPriv = orgActionPrivs.get(newPriv.getName());
                if (newPriv.isOperatable()) {
                    orgPriv.setOperatable(true);
                }
                if (newPriv.isVisible()) {
                    orgPriv.setVisible(true);
                }
            }
        }
        Map<String, GroupPrivilegeDto> orgGroupPrivs = orgPrivilege.getGroupPrivilegeMap();
        for (GroupPrivilegeDto newPriv : newPrivilege.getGroupPrivileges()) {
            if (!orgGroupPrivs.containsKey(newPriv.getName())) {
                orgGroupPrivs.put(newPriv.getName(), newPriv);
            } else {
                GroupPrivilegeDto orgPriv = orgGroupPrivs.get(newPriv.getName());
                if (newPriv.isWritable()) {
                    orgPriv.setWritable(true);
                }
                if (newPriv.isVisible()) {
                    orgPriv.setVisible(true);
                }
            }
        }
        orgPrivilege.setFieldPrivileges(new ArrayList<>(orgFieldPrivs.values()));
        orgPrivilege.setActionPrivileges(new ArrayList<>(orgActionPrivs.values()));
        orgPrivilege.setGroupPrivileges(new ArrayList<>(orgGroupPrivs.values()));
        return orgPrivilege;
    }
}
