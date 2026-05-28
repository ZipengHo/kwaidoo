package cell.gpf.study.action;

import java.util.Date;

import com.leavay.nio.crpc.RpcMap;

import cell.CellIntf;
import cell.gpf.dc.basic.flow.IAutoSubmitCallback;
import cmn.anotation.ClassDeclare;
import cmn.util.DateUtil;
import gpf.dc.action.intf.BaseActionIntf;
import gpf.dc.action.param.BaseActionParameter;
import gpf.dc.runtime.PDCForm;
import web.dto.Pair;
@ClassDeclare(label = "流程节点进入规则——触发自动提交信号"
,what="流程节点进入规则——触发自动提交信号"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyFlowOperate_AutoSubmitCallback<T extends BaseActionParameter> extends CellIntf,BaseActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		PDCForm pdcForm = input.getRtx().getPdcForm();
		pdcForm.setAttrValue("审批意见", "同意，" + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		input.getRtx().setPdcForm(pdcForm);
		RpcMap<Object> callbackParam = new RpcMap<>();
		callbackParam.put("operator", "root");
		callbackParam.put("actionName", "通过");
		callbackParam.put("synchronize", true);
		input.getRtx().setAutoSubmitCallback(IAutoSubmitCallback.class);
		input.getRtx().setAutoSubmitCallbackParam(callbackParam);
		return new Pair<>(true,"");
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseActionParameter.class;
	}
}
