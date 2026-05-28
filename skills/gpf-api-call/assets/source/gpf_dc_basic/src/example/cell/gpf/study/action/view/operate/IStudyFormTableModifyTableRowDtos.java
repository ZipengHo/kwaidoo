package cell.gpf.study.action.view.operate;

import java.util.List;
import java.util.Random;

import com.kwaidoo.ms.tool.CmnUtil;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import cmn.util.Tracer;
import cn.hutool.core.collection.CollUtil;
import fe.cmn.data.CColor;
import fe.cmn.panel.PanelContext;
import fe.cmn.table.TableRowDto;
import fe.cmn.table.decoration.TableRowDecorationDto;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import fe.util.style.FeStyleSetting;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "表格视图查询后干预TableRowDto的内容、样式"
,what="可干预TableoRowDto的样式,TableCellDto单元格的样式"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyFormTableModifyTableRowDtos <T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		Tracer tracer = newTracer();
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//界面组件对象
		AbsComponent component = (AbsComponent) input.getCurrentComponent();
		
		List<TableRowDto> rows = (List<TableRowDto>) input.getRtx().getParam(BaseFeActionParameter.FeActionParameter_TableRowDatas);
		if(CmnUtil.isCollectionEmpty(rows))
			return rows;
		FeStyleSetting styleSetting = component.getFeStyleSetting(panelContext);
		List<CColor> colors = CollUtil.newArrayList(styleSetting.normalColor,styleSetting.warnningColor,styleSetting.dangerousColor);
		Random random = new Random();
		for(TableRowDto row : rows) {
			int index = random.nextInt(colors.size());
			//可以干预单行的样式，也可以干预某个单元格的样式
			row.setDecoration(new TableRowDecorationDto(colors.get(index)));
//			for(String column : row.getMapFields().keySet()) {
//				TableCellDto cell = row.getMapFields().get(column);
//				cell.setDecoration(new TableCellDecorationDto(backgroundColor));
//			}
		}
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}

}
