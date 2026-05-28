package cell.gpf.study.action.view.operate;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import cmn.util.Tracer;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import gpf.adur.data.Form;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.view.AbsNestingEditTableView;
import gpf.dc.basic.fe.component.view.AbsNestingTableView;
import gpf.dc.basic.fe.component.view.AbsTableView;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "表格视图重建附加组件"
,what=""
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyFormTableRebuileAdditionalWidget <T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		Tracer tracer = newTracer();
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//界面组件对象
		AbsComponent component = (AbsComponent) input.getCurrentComponent();
		
		if(component instanceof AbsTableView) {
			Form form = new Form("gpf.md.slave.Sumary");
			form.setAttrValue("汇总条数", Math.random()+"");
			form.setAttrValue("金额", Math.random()+"");
			AbsTableView tableView = (AbsTableView) component;
			tableView.rebuildAllAddtionalWidget(panelContext);
//			Set<String> addtionalWidgetIds = new LinkedHashSet<>();
//			addtionalWidgetIds.add("summary");
//			tableView.rebuildAddtionalWidget(panelContext, addtionalWidgetIds);
		}else if(component instanceof AbsNestingTableView) {
			AbsNestingTableView tableView = (AbsNestingTableView) component;
			tableView.rebuildAllAddtionalWidget(panelContext);
		}else if(component instanceof AbsNestingEditTableView) {
			AbsNestingEditTableView tableView = (AbsNestingEditTableView) component;
			tableView.rebuildAllAddtionalWidget(panelContext);
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}

}
