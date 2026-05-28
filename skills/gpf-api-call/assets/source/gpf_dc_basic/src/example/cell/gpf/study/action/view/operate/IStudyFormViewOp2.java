package cell.gpf.study.action.view.operate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.kwaidoo.ms.tool.Utils;
import com.leavay.dfc.gui.LvUtil;

import cell.CellIntf;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.study.nadur.form.IStudyFormOp;
import cell.gpf.study.nadur.form.StudyFormMockConst;
import cmn.anotation.ClassDeclare;
import cson.core.CsonPojo;
import fe.cmn.app.ability.BatchExecuteCallback;
import fe.cmn.data.PairDto;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import gpf.adur.data.AttachData;
import gpf.adur.data.Form;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.param.ConfirmCallbackParam;
import gpf.dc.basic.fe.component.view.AbsFormView;
import gpf.dc.basic.fe.component.view.BaseFormEditView;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "表单视图组件操作接口样例"
,what="演示通过表单视图组件封装的界面交互接口与前端交互"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-19"
,updateTime = "2025-02-19")
public interface IStudyFormViewOp2<T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

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
			Form form = input.getForm();
			formView.simulateUploadAttach(form, IFormMgr.get().getFieldCode("附件"), new AttachData(Utils.getId()+".txt", "This is a test!".getBytes()));
			formView.fireCommandListener(panelContext, panelContext.getCurrentPanelWidgetId(), BaseFormEditView.CMD_CONFIRM, new ConfirmCallbackParam()
					.setForm(form).setQuitPop(true).setCommit(false));
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}
