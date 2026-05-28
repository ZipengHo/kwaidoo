package cell.gpf.study.action;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.dfc.gui.LvUtil;

import cell.CellIntf;
import cell.gpf.dc.runtime.IPDFRuntimeMgr;
import cmn.anotation.ClassDeclare;
import gpf.adur.data.Form;
import gpf.adur.data.ResultSet;
import gpf.dc.action.intf.BaseActionIntf;
import gpf.dc.action.param.BaseActionParameter;
import gpf.dc.runtime.PDFForm;
import gpf.dc.runtime.PDFInstance;
import gpf.exception.VerifyException;
@ClassDeclare(label = "流程其他操作代码样例"
,what="流程其他操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyFlowOperate<T extends BaseActionParameter> extends CellIntf,BaseActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		PDFInstance pdfInstance = input.getRtx().getPdfInstance();
		String nodeName = input.getRtx().getRefPDCNode().getName();
//		String msg = "关闭流程实例信号";
//		IPDFRuntimeMgr.get().throwClosePDFInstance(pdfInstance.getUuid(), nodeName, msg);
//		msg = "删除流程实例信号";
//		IPDFRuntimeMgr.get().throwDeletePDFInstance(pdfInstance.getUuid(), nodeName, msg);
//		try {
//			if(true)
//				throw new VerifyException("演示抛出异常捕捉后用提交异常再抛出，这个信号会将流程节点状态设置为error");
//		}catch (Exception e) {
//			String msg1 = "提交异常信号";
//			IPDFRuntimeMgr.get().throwSubmitError(pdfInstance.getUuid(), nodeName, msg1, e);
////			throw e;
//		}
		Cnd cnd = Cnd.NEW();
		cnd.and(new SqlExpressionGroup().andEquals(Form.Code, input.getRtx().getTotalForm().getStringByCode(Form.Code)));
		ResultSet<PDFForm> rs = IPDFRuntimeMgr.get().queryPDFFormPage(input.getRtx().getPdfUuid(), cnd, null, 1, 1);
		if(rs.isEmpty())
			throw new VerifyException("未找到提交记录");
		LvUtil.trace("PDFFOrm = "+rs.getDataList().get(0));
		ToolUtilities.sleep(10000);
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseActionParameter.class;
	}
}
