package cell.gpf.study.action.view;

import java.util.Map;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import cmn.util.Tracer;
import fe.cmn.app.ability.PopToast;
import fe.cmn.panel.PanelContext;
import fe.cmn.table.TableRowDto;
import fe.cmn.table.ability.QueryTableRows;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import fe.util.component.dto.FeDeliverData;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "菜单项点击响应动作"
,what="菜单项点击后响应动作示例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-03-02"
,updateTime = "2025-03-02")
public interface IStudyPopupMenuResponse<T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		Tracer tracer = newTracer();
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//界面组件对象
		AbsComponent component = (AbsComponent) input.getCurrentComponent();
		
		//在响应动作中，通过listener的getBinaryData获取传递的data
		FeDeliverData<Map<String,Object>> feData = (FeDeliverData<Map<String, Object>>) listener.getBinaryData();
		Map<String,Object> rowInfo = feData.getData();
		String currentRowId = (String) rowInfo.get("currentRowId");
		TableRowDto row = QueryTableRows.queryOne(panelContext, currentRowId);
		PopToast.info(panelContext.getChannel(), row.toString());
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
	
}