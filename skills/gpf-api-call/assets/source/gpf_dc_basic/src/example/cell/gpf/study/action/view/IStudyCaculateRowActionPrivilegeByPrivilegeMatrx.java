package cell.gpf.study.action.view;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cell.CellIntf;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cell.gpf.study.action.view.param.ViewActionStudyCaseParam;
import cmn.anotation.ClassDeclare;
import gpf.adur.data.Form;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;
import gpf.dc.basic.util.PrivilegeMatrixFeRunTool;
import gpf.dto.model.data.ActionPrivilegeDto;
@ClassDeclare(label = "根据权限矩阵计算行数据权限代码样例"
,what=""
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyCaculateRowActionPrivilegeByPrivilegeMatrx <T extends ViewActionStudyCaseParam> extends CellIntf, BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		IDCRuntimeContext rtx = input.getRtx();
		List<Form> rows = (List<Form>) rtx.getParam(BaseFeActionParameter.FeActionParameter_TableRowDatas);
		String privilegeMatrixCode = "DocumentMgr(文档管理)";
		Set<String> namespaces = new LinkedHashSet<>();
		namespaces.add("");
		String statusField = "节点名称";
		Map<String,Object> env = new LinkedHashMap<>();
		Map<String, Map<String, ActionPrivilegeDto>> actionPrivMap = PrivilegeMatrixFeRunTool.caculateRowActionPrivilege(rtx, rows, namespaces, env, privilegeMatrixCode, statusField);
		return actionPrivMap;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) ViewActionStudyCaseParam.class;
	}
	
}
