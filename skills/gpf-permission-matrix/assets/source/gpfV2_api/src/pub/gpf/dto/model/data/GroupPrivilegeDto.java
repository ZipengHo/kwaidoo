package gpf.dto.model.data;

import java.io.Serializable;

public class GroupPrivilegeDto implements Serializable {

    private static final long serialVersionUID = -3536974134832025707L;

    String uuid;
    String name;
    boolean visible;
    boolean writable;

    public String getName() {
        return name;
    }

    public GroupPrivilegeDto setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public GroupPrivilegeDto setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean isWritable() {
        return writable;
    }

    public GroupPrivilegeDto setWritable(boolean writable) {
        this.writable = writable;
        return this;
    }
}
