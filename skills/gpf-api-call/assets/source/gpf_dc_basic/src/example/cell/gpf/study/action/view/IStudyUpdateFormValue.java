package cell.gpf.study.action.view;

import java.util.LinkedHashMap;
import java.util.Map;

import cell.CellIntf;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.study.nadur.form.IStudyFormOp;
import cell.gpf.study.nadur.form.StudyFormMockConst;
import cmn.anotation.ClassDeclare;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import gpf.adur.data.Form;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.view.AbsFormView;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "表单界面更新表单值代码样例"
,what="表单界面更新表单值代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyUpdateFormValue <T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//界面组件对象
		AbsComponent component = (AbsComponent) input.getCurrentComponent();
		//继承于BaseFeActionParameter的参数类，可以直接通过getForm获取到界面的表单数据
		if(component instanceof AbsFormView) {
			//界面的表单值获取与流程内部的流程节点表单值获取略有不同，需要通过界面拿到用户输入值，这里已经通过input.getForm()封装
			Form form = input.getForm();
			//需要更新的属性值map，属性值类型为表单定义的属性类型，其中key是属性的code，除了模型上的系统属性直接通过常量获取外，其他用户定义的模型属性通过IFormMgr.get().getFieldCode()可获取属性名称对应的code
			Map<String,Object> fieldValueMap = new LinkedHashMap<>();
			//设置表单属性值的方式具体见IStudyFormOp
			//设置模型编号，模型编号是系统属性，直接用常量获取
			fieldValueMap.put(Form.Code, "001");
			//设置用户定义的模型属性，
			String fieldCode = IFormMgr.get().getFieldCode("文本属性");
			fieldValueMap.put(fieldCode, "自定义输入的文本值");
			IStudyFormOp.get().setAttrValue(fieldValueMap, false);
			fieldValueMap.put(IFormMgr.get().getFieldCode(StudyFormMockConst.TextField), "选项1,选项2,其他");
			//最后将计算好的表单值重新写到界面上，通过封装好的AbsFormView.setEditorValues方法设置
			((AbsFormView) component).setEditorValues(panelContext, form, fieldValueMap);
		}
		
		
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}
