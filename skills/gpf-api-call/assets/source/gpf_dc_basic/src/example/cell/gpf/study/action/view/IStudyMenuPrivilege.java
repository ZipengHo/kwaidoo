package cell.gpf.study.action.view;

import java.util.ArrayList;
import java.util.List;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import fe.cmn.panel.PanelContext;
import gpf.dc.basic.action.intf.FeMenuPrivilegeIntf;
import gpf.dc.basic.fe.component.app.AppCacheUtil;
import gpf.dc.basic.param.view.BaseFeActionParameter;
import gpf.dc.basic.param.view.dto.MenuNodeDto;
import gpf.dc.basic.privilege.dto.MenuPrivilegeDto;
@ClassDeclare(label = "应用菜单权限代码样例"
,what="应用菜单权限代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyMenuPrivilege <T extends BaseFeActionParameter> extends CellIntf, FeMenuPrivilegeIntf<T>{

	@Override
	default List<MenuPrivilegeDto> caculateMenuPrivilege(T input) throws Exception {
		List<MenuPrivilegeDto> menuPrivs = new ArrayList<>();
		MenuPrivilegeDto menuPriv = new MenuPrivilegeDto();
		PanelContext context = input.getPanelContext();
		//获取当前应用的菜单列表
		List<MenuNodeDto> menus = AppCacheUtil.getMenuNodes(context);
		for(MenuNodeDto menu : menus) {
			String menuUuid = menu.getKey();
			menuPriv.setMenuUuid(menuUuid);//设置应用菜单的uuid
			menuPriv.setVisible(true);
			menuPrivs.add(menuPriv);
		}
		return menuPrivs;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}
