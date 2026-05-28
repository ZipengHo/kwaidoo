package cell.gpf.study.action;

import com.leavay.dfc.gui.LvUtil;

import bap.cells.Cells;
import cell.CellIntf;
import cell.gpf.dc.basic.flow.ISaveTotalFormCallback;
import cell.gpf.dc.runtime.IDCRuntimeContext;
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
public interface IStudyFlowOperateWritebackBusinessModel<T extends BaseActionParameter> extends CellIntf,BaseActionIntf<T>{

	static IStudyFlowOperateWritebackBusinessModel get() {
		return Cells.get(IStudyFlowOperateWritebackBusinessModel.class);
	}
	@Override
	default Object execute(T input) throws Exception {
		IDCRuntimeContext rtx = input.getRtx();
		rtx.addSaveTotalFormCallback(ISaveTotalFormCallback.class);
		LvUtil.trace(rtx.getSaveTotalFormCallbacks());
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseActionParameter.class;
	}
	
	
}
