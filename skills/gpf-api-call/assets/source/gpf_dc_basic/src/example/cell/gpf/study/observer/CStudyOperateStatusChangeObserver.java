package cell.gpf.study.observer;

import bap.cells.BasicCell;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.adur.data.Form;
import gpf.dc.runtime.OperateLog;
import gpf.dc.runtime.PDCForm;

public class CStudyOperateStatusChangeObserver extends BasicCell implements IStudyOperateStatusChangeObserver{

	public void print(IDCRuntimeContext rtx,String methodName) throws Exception {
		OperateLog opLog = rtx.getCurrOpLog();
		Form totalForm = rtx.getTotalForm();
		PDCForm pdcForm = rtx.getPdcForm();
		Tracer tracer = TraceUtil.getCurrentTracer();
		StringBuffer sb = new StringBuffer();
		sb.append("---------------["+methodName+"]---------------------\n");
		sb.append("pdfUuid = "+rtx.getPdfUuid()+"\n");
		sb.append("pdfInstance = "+rtx.getPdfInstance().getUuid()+"\n");
		sb.append("node = "+rtx.getRefPDCNode().getName()+"\n");
		sb.append("operator = "+rtx.getOperator()+"\n");
		sb.append("action = "+rtx.getActionName()+"\n");
		sb.append("totalForm = "+totalForm+"\n");
		sb.append("pdcForm = " + pdcForm + "\n");
		sb.append("opLog = "+opLog+"\n");
		if(opLog != null) {
			sb.append("status = "+opLog.getStatus()+"\n");
		}
		sb.append("------------------------------------------\n");
		tracer.info(sb.toString());
//		System.out.println(sb.toString());
	}
	
	@Override
	public void onChangeTodo(IDCRuntimeContext rtx) throws Exception {
		print(rtx,"onChangeTodo");
	}
	

	@Override
	public void onChangeFinish(IDCRuntimeContext rtx) throws Exception {
		print(rtx,"onChangeFinish");
	}

	@Override
	public void onChangeError(IDCRuntimeContext rtx) throws Exception {
		print(rtx,"onChangeError");
	}

	@Override
	public void onChangeRevoke(IDCRuntimeContext rtx) throws Exception {
		print(rtx,"onChangeRevoke");
	}

}
