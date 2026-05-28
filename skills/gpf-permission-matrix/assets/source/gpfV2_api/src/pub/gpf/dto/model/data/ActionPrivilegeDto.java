package gpf.dto.model.data;

import java.io.Serializable;

public class ActionPrivilegeDto implements Serializable {

    private static final long serialVersionUID = -7343146670532903235L;

    String uuid;
    String name;
    boolean visible;
    boolean operatable;

    public String getName() {
        return name;
    }

    public ActionPrivilegeDto setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public ActionPrivilegeDto setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean isOperatable() {
        return operatable;
    }

    public ActionPrivilegeDto setOperatable(boolean operatable) {
        this.operatable = operatable;
        return this;
    }
}
