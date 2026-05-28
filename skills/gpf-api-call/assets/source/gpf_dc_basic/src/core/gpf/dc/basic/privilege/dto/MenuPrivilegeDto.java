package gpf.dc.basic.privilege.dto;

import java.io.Serializable;

public class MenuPrivilegeDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1854372115028161123L;
	String menuUuid;
	String menuPath;
	boolean visible;
	
	public String getMenuUuid() {
		return menuUuid;
	}
	public MenuPrivilegeDto setMenuUuid(String menuUuid) {
		this.menuUuid = menuUuid;
		return this;
	}
	public String getMenuPath() {
		return menuPath;
	}
	public MenuPrivilegeDto setMenuPath(String menuPath) {
		this.menuPath = menuPath;
		return this;
	}
	public boolean isVisible() {
		return visible;
	}
	public MenuPrivilegeDto setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}
}
