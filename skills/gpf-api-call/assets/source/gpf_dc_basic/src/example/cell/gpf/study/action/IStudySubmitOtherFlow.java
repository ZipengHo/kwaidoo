package cell.gpf.study.action;

import cell.CellIntf;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cell.gpf.dc.runtime.IPDFRuntimeMgr;
import gpf.dc.action.intf.BaseActionIntf;
import gpf.dc.action.param.BaseActionParameter;
import gpf.dc.runtime.PDCForm;

public interface IStudySubmitOtherFlow <T extends BaseActionParameter> extends CellIntf,BaseActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		IDCRuntimeContext rtx = input.getRtx();
		//调用其他流程执行，需要克隆一个不带当前流程上下文参数的上下文对象
		IDCRuntimeContext newRtx = rtx.cloneRtxWithoutPDF();
		String userCode = "User_001";//传入上下文的用户编号
		String otherPdfUuid = "";//其他流程Uuid
		String otherActionName = "";//其他流程开始节点提交动作流名称
		try(IDao dao = IDaoService.newIDao()){
			//构建开始节点的流程表单
			PDCForm otherPdcForm = IPDFRuntimeMgr.get().newStartForm(newRtx, otherPdfUuid);
			//必要的设置参数
			newRtx.setDao(dao);//其他流程的提交一般与当前流程节点的提交不在一个事务上，需要重新设置新的事务
			newRtx.setActionName(otherActionName);
			//可选的设置参数
			newRtx.setOperator(userCode);//一般不需要重新设置提交操作人，只有在需要重新指定时设置
//			newRtx.setParam(key, value);//其他需要传入上下文的运行参数
			//创建并提交开始节点表单动作
			IPDFRuntimeMgr.get().createAndSubmitPDCForm(otherPdfUuid, newRtx, otherPdcForm);
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseActionParameter.class;
	}
}