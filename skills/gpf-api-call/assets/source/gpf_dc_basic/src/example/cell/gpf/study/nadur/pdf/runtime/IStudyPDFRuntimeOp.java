package cell.gpf.study.nadur.pdf.runtime;

import java.util.ArrayList;
import java.util.Set;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import bap.cells.Cells;
import cell.CellIntf;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.dc.config.IPDFMgr;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cell.gpf.dc.runtime.IPDFRuntimeMgr;
import cell.gpf.study.nadur.form.IStudyFormOp;
import cell.gpf.study.nadur.pdf.StudyPDFMockConst;
import cmn.i18n.I18nIntf;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.adur.data.ResultSet;
import gpf.dc.basic.i18n.GpfDCBasicI18n;
import gpf.dc.config.PDF;
import gpf.dc.runtime.OperateLog;
import gpf.dc.runtime.PDCForm;
import gpf.dc.runtime.PDFForm;
import gpf.dc.runtime.PDFFormQueryOption;
import gpf.dc.runtime.PdfInstanceStatus;
import gpf.enums.OperateLogStatus;

public interface IStudyPDFRuntimeOp extends CellIntf {

	static IStudyPDFRuntimeOp get() {
		return Cells.get(IStudyPDFRuntimeOp.class);
	}

	default void testPDFFormOp() throws Exception {
		String pdfUuid = StudyPDFMockConst.PdfUuid;
		Tracer tracer = TraceUtil.getCurrentTracer();
		PDCForm pdcForm = null;
		//构建流程表单
		try (IDao dao = IDaoService.newIDao()) {
			IDCRuntimeContext rtx = prepareRuntimeContext(dao);
			//后台执行不需要鉴权时，可调用以下接口，减少鉴权和授权的开销
			boolean executePrivilegeAction = false;
			IPDFRuntimeMgr.get().newStartForm(rtx, pdfUuid, executePrivilegeAction);
		}
		IStudyFormOp.get().setAttrValue(pdcForm, true);
		//新增流程表单
		try (IDao dao = IDaoService.newIDao()) {
			IDCRuntimeContext rtx = prepareRuntimeContext(dao);
			rtx.setActionName(StudyPDFMockConst.TestAction);
			//流程表单创建会在流程流转时自动commit，因此不需要在外部dao.commit
			pdcForm = IPDFRuntimeMgr.get().createAndSubmitPDCForm(pdfUuid, rtx,
					pdcForm);
			//提交后的表单携带有下一步节点的信息
			tracer.info("节点唯一标识："+pdcForm.getNodeKey());
			tracer.info("节点名称："+pdcForm.getNodeName());
			tracer.info("操作记录唯一标识："+pdcForm.getOpLogUuid());
			tracer.info("流程实例唯一标识："+pdcForm.getPdfInstUuid());
		}
		try (IDao dao = IDaoService.newIDao()) {
			IDCRuntimeContext rtx = prepareRuntimeContext(dao);
			rtx.setActionName(StudyPDFMockConst.TestAction);
			String nodeKey = pdcForm.getNodeKey();
			//流程表单提交会在流程流转时自动commit，因此不需要在外部dao.commit
			pdcForm = IPDFRuntimeMgr.get().submitPDCForm(pdcForm.getPdfInstUuid(), nodeKey, rtx, pdcForm);
		}
		//查询流程表单详情
		try (IDao dao = IDaoService.newIDao()) {
			String opLogUuid = pdcForm.getOpLogUuid();
			IDCRuntimeContext rtx = prepareRuntimeContext(dao);
			String[] fields = null;//可指定需要查询的属性，默认传null时查询所有属性
			pdcForm = IPDFRuntimeMgr.get().queryPDCForm(rtx, pdfUuid, opLogUuid, fields);
			//后台执行不需要鉴权时，可调用以下接口，减少鉴权和授权的开销
			boolean executePrivilegeAction = false;
			IPDFRuntimeMgr.get().queryPDCForm(rtx, pdfUuid, opLogUuid, executePrivilegeAction, fields);
		}
		//查询流程表单列表
		ResultSet<PDFForm> rs = queryPDFFormPage(1, 20);
		for(PDFForm form : rs.getDataList()) {
			tracer.info(form);
		}
		//删除流程实例
		IPDFRuntimeMgr.get().deletePDFInstance(pdcForm.getPdfInstUuid());
	}
	
	default ResultSet<PDFForm> queryPDFFormPage(int pageNo,int pageSize) throws Exception{
		String pdfUuid = StudyPDFMockConst.PdfUuid;
		//查询流程表单列表
		PDFFormQueryOption queryOption = null;//可选设置，当流程表单数量较多导致性能查询缓慢时，可指定设置表单的查询列和筛选条件，缩小查询范围
		Cnd cnd = null;//构建好的流程表单查询视图的查询条件
		SqlExpressionGroup privilegeExpr = null;//构建好的表单数据权限过滤条件
		ResultSet<PDFForm> rs = IPDFRuntimeMgr.get().queryPDFFormPage(pdfUuid, queryOption, cnd, privilegeExpr, pageNo, pageSize);
		return rs;
	}
	
	default void testOpereateLogOp() throws Exception {
		String pdfUuid = StudyPDFMockConst.PdfUuid;
		Tracer tracer = TraceUtil.getCurrentTracer();
		ResultSet<PDFForm> rs = queryPDFFormPage(1, 20);
		for(PDFForm form : rs.getDataList()) {
			String opLogUuid = form.getOpLogUuid();
			//查询单条操作记录
			OperateLog  opLog = IPDFRuntimeMgr.get().queryOperateLog(pdfUuid, opLogUuid);
			tracer.info(opLog);
			//查询操作记录分页
			String pdfFormUuid = form.getUuid();
			Cnd cnd = Cnd.where(OperateLog.StepOperator, "=", StudyPDFMockConst.TestUserCode);
			ResultSet<OperateLog> rs2 = IPDFRuntimeMgr.get().queryOperateLogPage(pdfUuid, pdfFormUuid, cnd, 1, 20);
			for(OperateLog opLog2 : rs2.getDataList()) {
				tracer.info(opLog2);
			}
		}
	}
	/**
	 * 流程实例的状态管理操作一般只在运维时需要使用
	 * @throws Exception
	 */
	default void testPdfInstanceStatusOp() throws Exception {
		String pdfUuid = StudyPDFMockConst.PdfUuid;
		Tracer tracer = TraceUtil.getCurrentTracer();
		PDF pdf = IPDFMgr.get().queryPDF(pdfUuid);
		ResultSet<PDFForm> rs = queryPDFFormPage(1, 20);
		for(PDFForm form : rs.getDataList()) {
			String pdfInstUuid = form.getPdfInstUuid();
			//查询流程实例状态
			PdfInstanceStatus instanceStatus = IPDFRuntimeMgr.get().queryPDFInstanceStatus(pdfInstUuid);
			tracer.info(instanceStatus.getAllNodeStatus());
			Set<String> todoNodes = instanceStatus.getNodesByStatus(OperateLogStatus.todo);
			tracer.info("待办节点：" + todoNodes);
			//重置指定节点状态
			IPDFRuntimeMgr.get().resetPDFInstance(pdfInstUuid, new ArrayList<>(todoNodes));
			//重置所有节点状态
			IPDFRuntimeMgr.get().resetPDFInstanceAllNode(pdfInstUuid);
			//启动流程实例
			IPDFRuntimeMgr.get().startPDFInstance(pdfInstUuid);
			//停止流程实例
			IPDFRuntimeMgr.get().stopPDFInstance(pdfInstUuid);
		}
	}

	default IDCRuntimeContext prepareRuntimeContext(IDao dao) throws Exception {
		IDCRuntimeContext rtx = IPDFRuntimeMgr.get().newRuntimeContext();
		rtx.setDao(dao);
		rtx.setOperator(StudyPDFMockConst.TestUserCode);
		rtx.setUserModelId(StudyPDFMockConst.TestUserModelId);
		rtx.setOrgModelId(StudyPDFMockConst.TestOrgModelId);
		//设置国际化资源接口,应用上要根据应用上下文获取
		I18nIntf i18n = GpfDCBasicI18n.get();
		rtx.setI18n(i18n);
		return rtx;
	}
}
