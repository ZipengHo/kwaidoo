package cell.gpf.study.action.view.operate;

import java.util.ArrayList;
import java.util.List;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import fe.cmn.data.FePojo;
import fe.cmn.listView.ListViewContext;
import fe.cmn.listView.ability.QueryListViewItem;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import fe.util.component.dto.CardViewDtoIntf;
import gpf.adur.data.Form;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.view.AbsTableListViewExt;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "移动端列表组件接口代码样例"
,what="演示通过移动端列表组件新增或修改数据、删除数据、刷新列表"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-19"
,updateTime = "2025-02-19")
public interface IStudyFormListViewOp <T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//界面组件对象
		AbsComponent component = (AbsComponent) input.getCurrentComponent();
		
		if(component instanceof AbsTableListViewExt) {
			//1.新增记录
			AbsTableListViewExt listView = (AbsTableListViewExt) component;
			Form data = new Form("xxx");
			listView.addOrUpdateRow(panelContext, data);
			if(panelContext instanceof ListViewContext) {
				//2.查询选择行记录
				ListViewContext context = (ListViewContext)panelContext;
				FePojo fePojo = QueryListViewItem.queryFePojo(context, context.getClickItemKey());
				CardViewDtoIntf<Form> cardView = (CardViewDtoIntf<Form>) fePojo.getBinaryData();
				//3.获取行记录的编辑表单
				Form editForm = (Form) listView.getEditObject(cardView);
				//4.删除行记录
				List<String> rowIds = new ArrayList<>();
				rowIds.add(context.getClickItemKey());
				listView.onDeleteRows(panelContext, rowIds);
			}
			//5.刷新列表
			listView.onBtnRefresh(listener, panelContext, null);
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}
