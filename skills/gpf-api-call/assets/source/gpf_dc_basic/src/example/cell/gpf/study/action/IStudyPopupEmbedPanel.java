package cell.gpf.study.action;

import java.util.HashMap;
import java.util.Map;

import com.leavay.common.util.ToolUtilities;

import cell.CellIntf;
import cell.fe.gpf.dc.basic.IAppFeLoginPage;
//import cell.fe.jit.IJITEmbeddedPage;
import cmn.anotation.ClassDeclare;
import fe.cmn.panel.EmbedLocationDto;
import fe.cmn.panel.EmbedPanelDto;
import fe.cmn.panel.PanelBuilder;
import fe.cmn.panel.PanelContext;
import fe.cmn.panel.ability.PopDialog;
import fe.cmn.sys.QueryWebSocketUrl;
import fe.cmn.widget.WindowSizeDto;
import gpf.dc.action.param.BaseActionParameter;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.app.AppCacheUtil;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "流程提交动作代码样例"
,what="流程提交动作代码样例,演示获取上下文参数、对表单值校验和填值"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyPopupEmbedPanel<T extends BaseFeActionParameter> extends CellIntf, BaseFeActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
//		ToolUtilities.sleep(20*1000);
		PanelContext ctx = input.getPanelContext();
        String location = QueryWebSocketUrl.query(ctx.getChannel());
        String serviceName = IAppFeLoginPage.class.getName();

        Long animateTime = null;
        Boolean needLogin = true;
        Map<String, String> appInfoMap = new HashMap<>();
//        appInfoMap.put(IJITEmbeddedPage.sWorkSpace, "common");
        appInfoMap.put(AppCacheUtil.InitParam_SystemUuid, "fecd3c3a_e00d_4db6_b870_ade397f7b0f0");
        appInfoMap.put(AppCacheUtil.InitParam_AppCode, "JIT");

        String uuid = ToolUtilities.allockUUID();
        EmbedPanelDto panel = new EmbedPanelDto().setWidgetId("EMBED_" + uuid);
        panel.setLocation(new EmbedLocationDto(location));
        panel.setPanelBuilder(new PanelBuilder().setServiceName(serviceName));
        panel.setNeedLogin(needLogin);
        panel.setAppInfo(appInfoMap);
        if (animateTime != null) {
            panel.setAnimateTime(800L);
        }
        panel.setPreferSize(WindowSizeDto.all(0.8, 0.8));
        PopDialog.show(ctx, "", panel);
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		//填写类上泛型声明的参数类
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}
