package cell.gpf.study.action.view.operate;

import java.util.List;

import cell.CellIntf;
import cmn.util.Tracer;
import fe.cmn.panel.PanelContext;
import fe.cmn.table.TableContext;
import fe.cmn.table.TableRowDto;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import gpf.adur.data.Form;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.view.AbsNestingEditTableView;
import gpf.dc.basic.fe.component.view.AbsNestingTableView;
import gpf.dc.basic.fe.component.view.AbsTableView;
import gpf.dc.basic.param.view.BaseFeActionParameter;

public interface IStudyFormTableOp <T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

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
			AbsTableView tableView = (AbsTableView) component;
			//1.刷新表格
			tableView.onBtnRefresh(listener, panelContext, null);
			//2.新增或修改表单记录
			Form data = new Form("xxxx");
			//可决定是否在表格上执行事务提交
			boolean commit = false;
			Object[] params = new Object[] {data,commit};
			tableView.addOrUpdateRow(panelContext, params);
			//3.查询勾选的表格行
			List<TableRowDto> selectRows = tableView.querySelected(panelContext);
			for(TableRowDto row : selectRows) {
				//4.获取表格行的表单数据，界面数据
				Form form = (Form) row.getBinaryData();
				//5.获取表格行的表单编辑数据，会重新查询已持久化的数据
				form = (Form) tableView.getEditObject(row);
				tracer.info("form = "+form.getData());
			}
			//5.移除表格行记录
			tableView.onDeleteRows(panelContext, selectRows);
			if(panelContext instanceof TableContext) {
				String rowId = ((TableContext) panelContext).getCurrentRowId();
				//6.查询行操作按钮所在列
				TableRowDto row = tableView.queryRow(panelContext, rowId);
			}
		}else if(component instanceof AbsNestingTableView) {
			AbsNestingTableView tableView = (AbsNestingTableView) component;
		}else if(component instanceof AbsNestingEditTableView) {
			AbsNestingEditTableView tableView = (AbsNestingEditTableView) component;
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}

}
