package cell.gpf.study.action.view;

import java.util.LinkedHashSet;
import java.util.Set;

import org.nutz.dao.Cnd;

import cell.CellIntf;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.dc.config.IPDFMgr;
import cell.gpf.dc.runtime.IPDFRuntimeMgr;
import cell.gpf.study.action.view.param.ViewDataQueryActionStudyCaseParam;
import cmn.anotation.ClassDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.adur.data.ResultSet;
import gpf.dc.basic.action.intf.CustomQueryIntf;
@ClassDeclare(label = "自定义数据筛选样例"
,what="用于表格、列表、树等视图的结果集 自定义数据筛选代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyCustomQuery <T extends ViewDataQueryActionStudyCaseParam> extends CellIntf, CustomQueryIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		tracer.info("operator:"+input.getRtx().getOperator());
		tracer.info("userModel"+input.getRtx().getUserModelId());
		String modelId = input.getRtx().getPdfUuid();
		String querySql = input.getQuerySql();
		//附加携带查询的字段，需要在查询SQL中定义后该字段的查询表达式
		Set<String> extFields = new LinkedHashSet<>();
		extFields.add(ResultSet.TotalCount);
//		extFields.add("aAA");
		Cnd cnd = input.getCnd();
		if(cnd == null)
			cnd = Cnd.NEW();
		cnd.and("code", "=", "001");
		
		
		tracer.info("cnd = " + cnd);
		int pageNo = input.getPageNo();
		int pageSize = input.getPageSize();
		//界面上用户输入的过滤条件
//		FilterDto filterDto = input.getFilterDto();
//		ResultSetQueryParam privilegeParam = input.getPrivilegeParam();
//		SqlExpressionGroup defaultPrivExpr = input.getDefaultExpr();
//		AbsTableView tableView = (AbsTableView) input.getCurrentComponent();
//		String dataSql = "";//自定义查询SQL
//		//根据自定义查询SQL、数据权限参数、默认过滤条件构建出新的查询SQL
//		String querySql2 = tableView.buildResultSetQuerySqlWithPrivilege(dataSql, privilegeParam, defaultPrivExpr);
		ResultSet rs = null;
//		querySql = "WITH allData AS (select * from tmodel804)\r\n" + 
//				"\r\n" + 
//				"select *,'AA' as aAA,count(1) over() as totalcount from allData  ";
		//注意：如果表格上配置了列筛选，使用自定义查询SQL时需要把SQL缓存起来，才能给列筛选提供筛选视图的查询SQL
//		tableView.setCacheCustomQuerySql(input.getPanelContext(), querySql);
//		tableView.setCacheCnd(input.getPanelContext(), cnd);
		if(IPDFMgr.get().isPDF(modelId))
			rs = IPDFRuntimeMgr.get().queryPDFFormPageBySql(modelId, querySql, extFields, cnd, pageNo, pageSize);
		else 
			rs = IFormMgr.get().queryFormPageBySql(input.getRtx().getDao(), modelId, querySql, extFields, cnd, pageNo, pageSize);
		return rs;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) ViewDataQueryActionStudyCaseParam.class;
	}
	
}
