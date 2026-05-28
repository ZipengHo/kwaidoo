package cell.gpf.study.action.view;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import cell.CellIntf;
import cell.gpf.study.action.view.param.ViewDataQueryActionStudyCaseParam;
import cmn.anotation.ClassDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import fe.cmn.panel.PanelContext;
import gpf.adur.data.Form;
import gpf.dc.basic.action.intf.CustomQueryIntf;
import gpf.dc.basic.fe.component.view.AbsTableView;
@ClassDeclare(label = "自定义单表单视图数据查询"
,what=""
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudySingleFormCustomQuery <T extends ViewDataQueryActionStudyCaseParam> extends CellIntf, CustomQueryIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		AbsTableView tableView = (AbsTableView) input.getCurrentComponent();
		PanelContext panelContext = input.getPanelContext();
		String filterKeyWord = tableView.getFilterKeyword(panelContext);
		SqlExpressionGroup advFilter = tableView.getAdvFilter(panelContext);
		String querySql = tableView.getCacheCustomQuerySql(panelContext);
		Cnd cnd = tableView.getCacheCnd(panelContext);
		Tracer tracer = TraceUtil.getCurrentTracer();
		tracer.info("filterKeyWord="+filterKeyWord);
		tracer.info("advFilter="+advFilter);
		tracer.info("querySql="+querySql);
		tracer.info("cnd="+cnd);
		Form form = new Form("gpf.md.slave.Sumary");
		form.setAttrValue("汇总条数", Math.random()+"");
		form.setAttrValue("金额", Math.random()+"");
		return form;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) ViewDataQueryActionStudyCaseParam.class;
	}
	
}
