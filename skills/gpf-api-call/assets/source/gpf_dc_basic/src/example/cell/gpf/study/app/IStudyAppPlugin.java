package cell.gpf.study.app;

import cell.CellIntf;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.anotation.ClassDeclare;
import fe.util.component.AbsComponent;
import fe.util.component.param.WidgetParam;
import gpf.adur.action.Action;
import gpf.dc.basic.fe.intf.AppPluginIntf;
@ClassDeclare(label = "应用插件接口实现样例"
,what="应用插件接口实现样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyAppPlugin extends CellIntf,AppPluginIntf{

	
	@Override
	default void rewriteViewWidgetParam(IDCRuntimeContext rtx, AbsComponent component, WidgetParam widgetParam)
			throws Exception {
		Action action = (Action) rtx.getParam(IDCRuntimeContext.ContextKey_CurrentActionInstance);
		System.out.println("rewriteViewWidgetParam : "+action.getCode());
	}
}
