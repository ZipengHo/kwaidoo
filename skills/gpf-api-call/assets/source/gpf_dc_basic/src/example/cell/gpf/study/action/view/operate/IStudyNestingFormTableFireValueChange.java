package cell.gpf.study.action.view.operate;

import cell.CellIntf;
import cmn.util.Tracer;
import fe.cmn.panel.PanelContext;
import fe.cmn.table.TableRowDto;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import gpf.adur.data.Form;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.view.NestingFormTableView;
import gpf.dc.basic.fe.component.view.TableViewActionIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;

public interface IStudyNestingFormTableFireValueChange <T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		Tracer tracer = newTracer();
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//界面组件对象
		AbsComponent component = (AbsComponent) input.getCurrentComponent();
		
		if(component instanceof TableViewActionIntf) {
			TableViewActionIntf tableView = (TableViewActionIntf) component;
			Form form = new Form("gpf.md.slave.TestNotNull");
			TableRowDto row = tableView.convert2TableRowDto(form);
			//插入行并触发值改变事件
			tableView.insertRowsByIndex(panelContext, 0, row);
			tableView.fireCommandListener(panelContext, panelContext.getCurrentPanelWidgetId(), NestingFormTableView.CMD_VALUE_CHANGED, null);
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}

}
