package gpf.dto.model.data;

import java.io.Serializable;

public class FieldPrivilegeDto implements Serializable {

    private static final long serialVersionUID = -3536974134832025707L;

    String uuid;

    /**
     * 属性Code
     */
    String field;

    /**
     * 属性名称
     */
    String fieldName;

    boolean visible;
    boolean writable;

    /**
     * 是否必填
     */
    Boolean require;

    public String getField() {
        return field;
    }

    public FieldPrivilegeDto setField(String field) {
        this.field = field;
        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public FieldPrivilegeDto setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public FieldPrivilegeDto setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean isWritable() {
        return writable;
    }

    public FieldPrivilegeDto setWritable(boolean writable) {
        this.writable = writable;
        return this;
    }

    public Boolean getRequire() {
        return require;
    }

    public FieldPrivilegeDto setRequire(Boolean require) {
        this.require = require;
        return this;
    }
}
