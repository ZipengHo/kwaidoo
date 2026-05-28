package cell.gpf.study.action.view;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import cell.CellIntf;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cell.gpf.study.action.view.param.ViewActionStudyCaseParam;
import cmn.anotation.ClassDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import gpf.adur.data.Form;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.view.AbsFormView;
import gpf.dc.basic.util.PrivilegeMatrixFeRunTool;
import gpf.dto.model.data.FormPrivilegeDto;
@ClassDeclare(label = "自定义数据筛选样例"
,what="用于表格、列表、树等视图的结果集 自定义数据筛选代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyCaculateFormPrivilegeByPrivilegeMatrx <T extends ViewActionStudyCaseParam> extends CellIntf, BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		IDCRuntimeContext rtx = input.getRtx();
		AbsFormView currComponent = (AbsFormView) input.getCurrentComponent();
		ListenerDto listener = input.getListener();
		PanelContext feContext = input.getPanelContext();
		Form form = input.getForm();
		String privilegeMatrixCode = "DocumentMgr(文档管理)";
		Set<String> namespaces = new LinkedHashSet<>();
		namespaces.add("");
		String statusField = "节点名称";
		Map<String,Object> env = new LinkedHashMap<>();
		FormPrivilegeDto formPrivilege = PrivilegeMatrixFeRunTool.caculatePrivilege(rtx, form, namespaces,env,privilegeMatrixCode,statusField);
		if(formPrivilege != null) {
			currComponent.initWidgetParamFormPrivilege(feContext, formPrivilege);
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) ViewActionStudyCaseParam.class;
	}
	
}
