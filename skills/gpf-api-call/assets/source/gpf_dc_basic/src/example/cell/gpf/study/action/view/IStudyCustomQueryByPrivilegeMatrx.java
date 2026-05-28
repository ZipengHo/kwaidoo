package cell.gpf.study.action.view;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import cell.CellIntf;
import cell.gpf.dc.config.IPDFMgr;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cell.gpf.study.action.view.param.ViewActionStudyCaseParam;
import cmn.anotation.ClassDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import gpf.adur.data.ResultSet;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.util.PrivilegeMatrixFeRunTool;
import gpf.dc.config.PDF;
@ClassDeclare(label = "自定义数据筛选样例"
,what="用于表格、列表、树等视图的结果集 自定义数据筛选代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyCustomQueryByPrivilegeMatrx <T extends ViewActionStudyCaseParam> extends CellIntf, BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		IDCRuntimeContext rtx = input.getRtx();
		AbsComponent currComponent = (AbsComponent) input.getCurrentComponent();
		ListenerDto listener = input.getListener();
		PanelContext feContext = input.getPanelContext();
		String privilegeMatrixCode = "DocumentMgr(文档管理)";
		Set<String> namespaces = new LinkedHashSet<>();
		namespaces.add("");
		String statusField = "节点名称";
		String pdfUuid = rtx.getPdfUuid();
//		PDF pdf = IPDFMgr.get().queryPDF(pdfUuid);
//		Set<String> statusValues = pdf.getNodes().stream().map(v->v.getName()).collect(Collectors.toSet());
		Map<String,Object> env = new LinkedHashMap<>();
		ResultSet rs = PrivilegeMatrixFeRunTool.queryDataPageWithPrivilege(rtx, currComponent, listener, feContext, namespaces, env, privilegeMatrixCode, statusField, (Set)null);
		return rs;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) ViewActionStudyCaseParam.class;
	}
	
}
