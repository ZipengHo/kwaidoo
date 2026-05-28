package cell.gpf.study.app;

import java.util.List;
import java.util.Map;

import cell.CellIntf;
import cell.fe.gpf.dc.basic.IAppFeI18n;
import cmn.anotation.ClassDeclare;
import fe.cmn.panel.PanelContext;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.app.AppCacheUtil;
import gpf.dc.basic.param.view.BaseFeActionParameter;
import gpf.dc.basic.param.view.dto.ApplicationSetting;
import gpf.dc.basic.param.view.dto.MenuNodeDto;
import gpf.dc.basic.privilege.dto.MenuPrivilegeDto;
@ClassDeclare(label = "应用缓存操作代码样例"
,what="应用缓存操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyAppContext <T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		PanelContext panelContext = input.getPanelContext();
		//获取应用配置信息
		ApplicationSetting appSetting =  AppCacheUtil.getSetting(panelContext);
		//获取应用菜单
		List<MenuNodeDto> menus = AppCacheUtil.getMenuNodes(panelContext);
		//获取应用菜单权限
		Map<String,MenuPrivilegeDto> menuPrivs = AppCacheUtil.getMenuPrivileges(panelContext);
		//获取应用国际化接口
		IAppFeI18n appFeI18n = AppCacheUtil.getAppFeI18n(panelContext);
		//国际化资源key
		String key = "";
		//国际化资源分组
		String group = "";
		//国际化资源入参
		String[] params = {};
		String value = appFeI18n.format(key, params);
		String value2 = appFeI18n.formatInGroup(key, group, params);
		
		AppCacheUtil.getAppCacheMgr(panelContext,appSetting);
		
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}
