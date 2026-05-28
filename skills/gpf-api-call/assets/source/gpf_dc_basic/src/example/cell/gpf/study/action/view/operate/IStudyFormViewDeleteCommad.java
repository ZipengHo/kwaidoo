package cell.gpf.study.action.view.operate;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.view.AbsFormView;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "表单视图组件删除指令回调操作"
,what="演示通过表单视图组件的删除指令回调触发源组件的删除指令"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-19"
,updateTime = "2025-02-19")
public interface IStudyFormViewDeleteCommad<T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//界面组件对象
		AbsComponent component = (AbsComponent) input.getCurrentComponent();
		if(component instanceof AbsFormView) {
			AbsFormView formView = (AbsFormView) component;
			//触发表单视图的删除指令
			formView.fireDeleteCommand(panelContext);
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}
