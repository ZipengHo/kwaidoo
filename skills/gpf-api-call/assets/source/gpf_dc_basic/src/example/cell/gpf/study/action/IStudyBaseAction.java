package cell.gpf.study.action;

import com.kwaidoo.ms.tool.CmnUtil;

import cell.CellIntf;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.anotation.ClassDeclare;
import cmn.i18n.I18nIntf;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.adur.data.Form;
import gpf.dc.action.intf.BaseActionIntf;
import gpf.dc.action.param.BaseActionParameter;
import gpf.dc.runtime.PDCForm;
import gpf.exception.VerifyException;
@ClassDeclare(label = "流程提交动作代码样例"
,what="流程提交动作代码样例,演示获取上下文参数、对表单值校验和填值"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyBaseAction<T extends BaseActionParameter> extends CellIntf, BaseActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
//		ToolUtilities.sleep(20*1000);
		Tracer tracer = TraceUtil.getCurrentTracer();
		tracer.info("操作人："+input.getRtx().getOperator());
		tracer.info("动作："+input.getRtx().getActionName());
		tracer.info("提交表单："+input.getRtx().getPdcForm());
		tracer.info("当前操作记录："+input.getRtx().getCurrOpLog());
		
		//获取当前运行上下文
		IDCRuntimeContext rtx = input.getRtx();
		//获取流程节点表单
		PDCForm pdcForm = rtx.getPdcForm();
		//获取属性值的方式与表单一样，具体见IStudyFormOp
		String textField = pdcForm.getString("文本");
		//对流程节点表单值进行校验
		if(CmnUtil.isStringEqual(textField, "测试"))
			throw new VerifyException("表单属性值不合法：" + textField);//抛出VerifyException指定为校验异常
		//对流程节点表单值进行设置，通过setAttrValue为用户定义的模型属性设置，如果是系统属性，直接通过setAttrValueByCode设值，
		pdcForm.setAttrValueByCode(Form.Code, "001");
		pdcForm.setAttrValue("文本", "测试值");
		//设置属性值的方式与表单一样，具体见IStudyFormOp
		//表单值设置后，不同于模型数据的更新操作，这里将数据设置会上下文中，由流程流转时自动更新流程总表单数据
		rtx.setPdcForm(pdcForm);
		//
		I18nIntf i18n = rtx.getI18n();
		String key = rtx.getI18nString(input.getRtx().getActionName());
		tracer.info("国际化资源接口："+i18n);
		tracer.info("国际化资源文本："+rtx.getActionName()+"="+key);
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		//填写类上泛型声明的参数类
		return (Class<? extends T>) BaseActionParameter.class;
	}
}
