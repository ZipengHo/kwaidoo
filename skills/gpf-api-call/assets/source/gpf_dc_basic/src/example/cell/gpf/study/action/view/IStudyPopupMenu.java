package cell.gpf.study.action.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import cmn.util.Tracer;
import fe.cmn.menu.MenuDto;
import fe.cmn.menu.MenuItemDto;
import fe.cmn.panel.MenuPosition;
import fe.cmn.panel.PanelContext;
import fe.cmn.panel.ability.PopMenu;
import fe.cmn.res.JDFICons;
import fe.cmn.table.TableContext;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "弹出菜单并设置调用动作"
,what="弹出菜单后选择菜单项，执行指定动作定义的动作"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-03-02"
,updateTime = "2025-03-02")
public interface IStudyPopupMenu<T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		Tracer tracer = newTracer();
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//界面组件对象
		AbsComponent component = (AbsComponent) input.getCurrentComponent();
		
		List<MenuItemDto> items = new ArrayList<>();
		MenuItemDto item = new MenuItemDto().setLabel("菜单").setIcon(JDFICons.information);
		Map<String,Object> data = new LinkedHashMap<String,Object>();
		if(panelContext instanceof TableContext) {
			data.put("currentRowId", ((TableContext) panelContext).getCurrentRowId());
			data.put("currentRowIdx", ((TableContext) panelContext).getCurrentHoverRowIdx());
		}
		//动作定义名称
		String actionName = "菜单响应";
		item.setOnClick(component.newListener(component.getService(), actionName, true, data));
		//在响应动作中，通过listener的getBinaryData获取传递的data
//		FeDeliverData<Map<String,Object>> feData = (FeDeliverData<Map<String, Object>>) listener.getBinaryData();
//		Map<String,Object> rowInfo = feData.getData();
		items.add(item);
        MenuDto menu = new MenuDto(items);
        PopMenu.attachShow(panelContext, menu, null, MenuPosition.right_top);
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
	
}