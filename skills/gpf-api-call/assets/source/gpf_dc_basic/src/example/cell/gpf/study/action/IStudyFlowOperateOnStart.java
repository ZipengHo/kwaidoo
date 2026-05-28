package cell.gpf.study.action;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import gpf.dc.action.intf.BaseActionIntf;
import gpf.dc.action.param.BaseActionParameter;
import gpf.dto.cfg.runtime.AutoSubmitResult;
@ClassDeclare(label = "流程其他操作代码样例"
,what="流程其他操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyFlowOperateOnStart<T extends BaseActionParameter> extends CellIntf,BaseActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
//		if(true)
//			throw new Exception("演示节点启动时异常");
		AutoSubmitResult result = new AutoSubmitResult().setActionName("下一步").setOperator(input.getRtx().getOperator())
				.setSynchronize(false);
		return result;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseActionParameter.class;
	}
}
