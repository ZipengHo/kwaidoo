package cell.gpf.study.action;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import gpf.dc.action.intf.BaseActionIntf;
import gpf.dc.action.param.BaseActionParameter;
import gpf.dto.cfg.runtime.AutoSubmitResult;
@ClassDeclare(label = "流程自动提交代码样例"
,what="流程自动提交代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyAutoSubmit<T extends BaseActionParameter> extends CellIntf,BaseActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		//自动提交只在节点启动环节执行有效
		String operator = "jitUser_admin";
		String actionName = "是";
		AutoSubmitResult autosubmit = new AutoSubmitResult();
		autosubmit.setOperator(operator);
		autosubmit.setActionName(actionName);
		autosubmit.setSynchronize(false);//可设置同步或异步自动提交
		autosubmit.setAsynCallSleepTime(500);//异步等待调用时间，默认500ms后调用
		return autosubmit;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseActionParameter.class;
	}
}
