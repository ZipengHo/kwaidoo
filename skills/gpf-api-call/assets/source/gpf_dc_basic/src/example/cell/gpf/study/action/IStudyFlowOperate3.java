package cell.gpf.study.action;

import cell.CellIntf;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cell.gpf.dc.runtime.IPDFRuntimeMgr;
import cmn.anotation.ClassDeclare;
import gpf.dc.action.intf.BaseActionIntf;
import gpf.dc.action.param.BaseActionParameter;
@ClassDeclare(label = "流程其他操作代码样例"
,what="流程其他操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyFlowOperate3<T extends BaseActionParameter> extends CellIntf,BaseActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		IDCRuntimeContext rtx = input.getRtx();
		IPDFRuntimeMgr.get().throwSubmitError(rtx.getPdfInstance().getUuid(), rtx.getRefPDCNode().getName(), "演示提交异常", null);
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseActionParameter.class;
	}
}
