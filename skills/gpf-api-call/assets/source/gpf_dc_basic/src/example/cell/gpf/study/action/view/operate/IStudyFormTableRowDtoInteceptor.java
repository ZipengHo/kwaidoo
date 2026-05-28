package cell.gpf.study.action.view.operate;

import java.util.List;
import java.util.Random;

import cell.CellIntf;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.anotation.ClassDeclare;
import cn.hutool.core.collection.CollUtil;
import fe.cmn.data.CColor;
import fe.cmn.panel.PanelContext;
import fe.cmn.table.TableRowDto;
import fe.cmn.table.decoration.TableRowDecorationDto;
import fe.util.component.AbsComponent;
import fe.util.style.FeStyleSetting;
import gpf.adur.data.Form;
import gpf.dc.basic.fe.intf.TableRowDtoInterceptor;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "表单行数据转换拦截器，可以干预表格行数据的样式"
,what=""
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyFormTableRowDtoInteceptor extends CellIntf,TableRowDtoInterceptor{
	

	@Override
	default void beforeConvertTableRow(IDCRuntimeContext rtx, Form form) throws Exception {
		// TODO Auto-generated method stub
		form.setAttrValue("AAA", ""+System.currentTimeMillis());
		
	}

	@Override
	default boolean onTableCellValueChanged(IDCRuntimeContext rtx, Form form) throws Exception {
		form.setAttrValue("AAA", ""+System.currentTimeMillis());
		//返回true表示值已改变，需要刷新表格行数据
		return true;
	}

	@Override
	default TableRowDto afterConvertTableRow(IDCRuntimeContext rtx, TableRowDto row) throws Exception {
		//界面上下文对象
//		PanelContext panelContext = (PanelContext) rtx.getParam(BaseFeActionParameter.FeActionParameter_PanelContext);
//		//界面组件对象
//		AbsComponent component = (AbsComponent) rtx.getParam(BaseFeActionParameter.FeActionParameter_CurrentComponent);
//		FeStyleSetting styleSetting = component.getFeStyleSetting(panelContext);
//		List<CColor> colors = CollUtil.newArrayList(styleSetting.normalColor,styleSetting.warnningColor,styleSetting.dangerousColor);
//		Random random = new Random();
//		int index = random.nextInt(colors.size());
//		//可以干预单行的样式，也可以干预某个单元格的样式
//		row.setDecoration(new TableRowDecorationDto(colors.get(index)));
		return row;
	}

}
