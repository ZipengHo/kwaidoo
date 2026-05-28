package gpf.dc.basic.privilege.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppPrivilegeDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1752064009525667306L;
	boolean isPrivilegeSettingEmpty = true;
	public AppPrivilegeDto setPrivilegeSettingEmpty(boolean isPrivilegeSettingEmpty) {
		this.isPrivilegeSettingEmpty = isPrivilegeSettingEmpty;
		return this;
	}
	public boolean isPrivilegeSettingEmpty() {
		return isPrivilegeSettingEmpty;
	}
	/**
	 * 当前身份对应的菜单权限
	 */
	Map<String,List<MenuPrivilegeDto>> identifyMenuPrivilegeMap = new LinkedHashMap<>();
	
	public Map<String, List<MenuPrivilegeDto>> getIdentifyMenuPrivilegeMap() {
		return identifyMenuPrivilegeMap;
	}
	public AppPrivilegeDto setIdentifyMenuPrivilegeMap(Map<String, List<MenuPrivilegeDto>> identifyMenuPrivilegeMap) {
		this.identifyMenuPrivilegeMap = identifyMenuPrivilegeMap;
		return this;
	}
	/**
	 * 获取应用内的所有菜单权限
	 * @return
	 */
	public Map<String, MenuPrivilegeDto> getAllMenuPrivileges() {
		if(identifyMenuPrivilegeMap == null)
			return null;
		Map<String,MenuPrivilegeDto> map = new LinkedHashMap<String, MenuPrivilegeDto>();
		for(String identify : identifyMenuPrivilegeMap.keySet()) {
			List<MenuPrivilegeDto> menuPrivileges = identifyMenuPrivilegeMap.get(identify);
			for(MenuPrivilegeDto privilegeDto : menuPrivileges) {
				if(map.containsKey(privilegeDto.getMenuUuid())) {
					if(privilegeDto.isVisible()) {
						map.put(privilegeDto.getMenuUuid(), privilegeDto);
					}
				}else {
					map.put(privilegeDto.getMenuUuid(), privilegeDto);
				}
			}
		}
		return map;
	}
	
	
}
