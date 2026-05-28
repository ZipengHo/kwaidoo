package cell.gpf.study.action.view.operate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.param.BaseTableViewParam;
import gpf.dc.basic.fe.component.view.TableViewActionIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;
import gpf.dc.basic.param.view.dto.TableViewSetting;
import gpf.dto.model.data.ActionPrivilegeDto;
import gpf.dto.model.data.FormPrivilegeDto;
@ClassDeclare(label = "表格视图组件初始化动作干预样例"
,what="演示通过表格视图组件封装的界面初始化动作干预表单视图界面的构建，在表格视图初始化时，可以干预表格上按钮、属性的显隐控制，表格的视图配置信息"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-03-22"
,updateTime = "2025-03-22")
public interface IStudyTableViewInitOp <T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//界面组件对象
		Tracer tracer = TraceUtil.getCurrentTracer();
		tracer.info("触发时机："+input.getTriggerTime());
		AbsComponent component = (AbsComponent) input.getCurrentComponent();
		if(component instanceof TableViewActionIntf) {
			TableViewActionIntf formView = (TableViewActionIntf) component;
			BaseTableViewParam viewParam = (BaseTableViewParam) formView.getWidgetParam();
			
			//设置表格初始化权限
			FormPrivilegeDto formPrivilege = new FormPrivilegeDto();
			List<ActionPrivilegeDto> actionPrivileges = new ArrayList<>();
			actionPrivileges.add(new ActionPrivilegeDto().setName("测试").setVisible(true).setOperatable(true));
			actionPrivileges.add(new ActionPrivilegeDto().setName("新增").setVisible(true).setOperatable(false));
			actionPrivileges.add(new ActionPrivilegeDto().setName("删除").setVisible(false).setOperatable(false));
			formPrivilege.setActionPrivileges(actionPrivileges);
			formView.initWidgetParamFormPrivilege(panelContext, formPrivilege);
			
			//1.对于界面组件中有定义标签组的组件，可通过以下方法批量控制显示隐藏
			Map<String,Boolean> groupRequireOverride = new LinkedHashMap<>();
			Map<String,Boolean> groupVisibleOverride = new LinkedHashMap<>();
			groupVisibleOverride.put("分组1", false);
			Map<String,Boolean> groupWritableOverride = new LinkedHashMap<>();
			groupWritableOverride.put("分组2", false);
			viewParam.setGroupWritableOverride(groupWritableOverride);
			viewParam.setGroupRequireOverride(groupRequireOverride);
			viewParam.setGroupVisibleOverride(groupVisibleOverride);
			//2.viewParam上的其他参数也可以在初始化时进行修改，
			viewParam.getColumns();
			TableViewSetting setting = viewParam.getSetting(TableViewSetting.class);
			
			//3.注意：修改完初始化参数配置后，要将参数重新设置回组件上
			formView.setWidgetParam(viewParam);
//			input.getRtx().setParam(BaseFeActionParameter.FeActionParameter_CurrentComponent, formView);
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}