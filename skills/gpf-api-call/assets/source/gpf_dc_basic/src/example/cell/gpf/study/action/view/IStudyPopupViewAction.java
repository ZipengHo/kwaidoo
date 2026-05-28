package cell.gpf.study.action.view;

import cell.CellIntf;
import cell.gpf.adur.action.IActionMgr;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.anotation.ClassDeclare;
import fe.cmn.panel.PanelContext;
import fe.cmn.panel.PanelDto;
import fe.cmn.panel.ability.PopDialog;
import fe.cmn.widget.ListenerDto;
import fe.cmn.widget.WindowSizeDto;
import fe.util.component.AbsComponent;
import gpf.adur.action.Action;
import gpf.dc.basic.dto.view.PDFInstanceTableViewDto;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "界面弹窗视图代码样例"
,what="界面弹窗视图代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-14"
,updateTime = "2025-02-14")
public interface IStudyPopupViewAction <T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//界面组件对象
		AbsComponent component = (AbsComponent) input.getCurrentComponent();
		
		String title = "弹窗样例";
		//PDF表格视图模型ID
		String actionModelId = PDFInstanceTableViewDto.FormModelId;
		//PDF视图编号
		String actionCode = "基础包_服务参数配置";
		Action viewAction = IActionMgr.get().queryActionByCode(input.getRtx().getDao(), actionModelId, actionCode);
		IDCRuntimeContext rtx = input.getRtx();
		IDCRuntimeContext newRtx = rtx.cloneRtx();
		PanelDto panel = (PanelDto) IActionMgr.get().executeAction(viewAction, newRtx);
		//弹窗
		//设置窗口大小
		panel.setPreferSize(WindowSizeDto.all(0.8, 0.8));
		PopDialog.show(panelContext, title, panel);
		//抽屉
		//设置窗口大小
//		panel.setPreferSize(WindowSizeDto.all(0.6, 1));
//		PopDrawer.show(panelContext, title, panel);
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}