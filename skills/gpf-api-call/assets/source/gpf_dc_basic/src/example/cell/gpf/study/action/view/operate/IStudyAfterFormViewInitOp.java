package cell.gpf.study.action.view.operate;

import java.awt.Color;

import cell.CellIntf;
import cell.gpf.adur.data.IFormMgr;
import cmn.anotation.ClassDeclare;
import fe.cmn.FeUtil;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import fe.cmn.widget.WidgetDto;
import fe.cmn.widget.decoration.BorderDto;
import fe.cmn.widget.decoration.DecorationDto;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "表单视图界面初始化后动作干预样例"
,what=""
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-21"
,updateTime = "2025-02-21")
public interface IStudyAfterFormViewInitOp <T extends BaseFeActionParameter> extends CellIntf,BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		//界面动作监听传入对象
		ListenerDto listener = input.getListener();
		//界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		//拿到界面初始化后的组件对象
		WidgetDto widgetDto = input.getInitedWidget();
		//修改文本组件样式
		WidgetDto textWidget = FeUtil.searchWidgetById(widgetDto, IFormMgr.get().getFieldCode("整数"));
		textWidget.setDecoration(new DecorationDto().setBorder(BorderDto.all(Color.RED, 1)));
		BaseFeActionParameter.setInitedWidget(input.getRtx(), widgetDto);
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}