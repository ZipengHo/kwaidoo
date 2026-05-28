package cell.gpf.study.action.view.operate;

import java.util.ArrayList;
import java.util.List;

import com.kwaidoo.ms.tool.CmnUtil;

import cell.CellIntf;
import cell.gpf.adur.data.IFormMgr;
import cmn.anotation.ClassDeclare;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.param.BaseDataViewParam;
import gpf.dc.basic.fe.component.view.AbsFormView;
import gpf.dc.basic.param.view.BaseFeActionParameter;
import gpf.dc.basic.param.view.dto.FormFieldDefine;
@ClassDeclare(label = "表单视图组件修改视图元数据并重建属性样例"
,what="演示通过表单视图组件的视图元数据调整属性显示"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-03-21"
,updateTime = "2025-03-21")
public interface IStudyFormViewModifyWidgetParamOp<T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

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
			BaseDataViewParam widgetParam = (BaseDataViewParam) formView.getWidgetParam();
			//1.控制表单属性必填项
			String textFieldCode = IFormMgr.get().getFieldCode("文本");
			String doubleFieldCode = IFormMgr.get().getFieldCode("小数");
			List<FormFieldDefine> fieldDefs = widgetParam.getFieldDefines();
			for(FormFieldDefine fieldDef : fieldDefs) {
				if(CmnUtil.isStringEqual(fieldDef.getCode(), textFieldCode)) {
					fieldDef.setAlias("文本改别名");
				}else if(CmnUtil.isStringEqual(fieldDef.getCode(), textFieldCode)) {
					fieldDef.setAlias("小数改别名");
				}
			}
			//重建属性组件
			List<String> fieldCodes = new ArrayList<>();
			fieldCodes.add(textFieldCode);
			fieldCodes.add(doubleFieldCode);
			formView.rebuildFieldWidgets(panelContext, fieldCodes);
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}
