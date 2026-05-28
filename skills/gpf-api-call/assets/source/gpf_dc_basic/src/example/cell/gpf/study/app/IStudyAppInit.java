package cell.gpf.study.app;

import cell.CellIntf;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.anotation.ClassDeclare;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import fe.cmn.app.AppDto;
import fe.cmn.app.Context;
import fe.cmn.app.FontFamilyDto;
import fe.cmn.data.LocaleDto;
import fe.cmn.data.LocaleSettingsDto;
import fe.cmn.panel.PanelBuilder;
import fe.cmn.panel.PanelContext;
import fe.cmn.panel.PanelDto;
import fe.util.component.AbsComponent;
import fe.util.component.param.WidgetParam;
import gpf.adur.action.Action;
import gpf.dc.basic.fe.intf.AppInitIntf;
import gpf.dc.basic.param.view.dto.ApplicationSetting;
@ClassDeclare(label = "应用初始化干预接口实现样例"
,what="应用初始化干预接口实现样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-10"
,updateTime = "2025-02-10")
public interface IStudyAppInit extends CellIntf,AppInitIntf{

	@Override
	default ApplicationSetting afterQueryApplicatinSetting(Context context, ApplicationSetting setting)
			throws Exception {
		// 在查询后的应用配置后干预应用配置缓存
		return setting;
	}
	
	@Override
	default AppDto afterInitApp(Context context, AppDto appDto) throws Exception {
		//可对应用配置初始化干预，例如
		// 设置语言对应的显示字体
		LocaleSettingsDto zhSettings = new LocaleSettingsDto()
				.setLocale(new LocaleDto("zh"))
				.setFontFamily(new FontFamilyDto("Zhi Mang Xing", "https://kwaidoo.com/cdn-flutter/fonts/example/ZhiMangXing-Regular.ttf"));
		
		LocaleSettingsDto enSettings = new LocaleSettingsDto()
				.setLocale(new LocaleDto("en"))
				.setFontFamily(new FontFamilyDto("playwrite-ie-guides-regular", "https://kwaidoo.com/cdn-flutter/fonts/example/PlaywriteIN-VariableFont_wght.ttf"));
		
		LocaleSettingsDto esSettings = new LocaleSettingsDto()
				.setLocale(new LocaleDto("es"))
				.setFontFamily(new FontFamilyDto("BebasNeue-Regular", "https://kwaidoo.com/cdn-flutter/fonts/example/BebasNeue-Regular.ttf"));
		
		appDto.setLocaleSettings(zhSettings, enSettings, esSettings);
		Tracer tracer = TraceUtil.getCurrentTracer();
		tracer.info("应用初始化干预~~");
		System.out.println("应用初始化干预~~");
		return appDto;
	}
	
	@Override
	default void beforeBuildHomePage(PanelBuilder builder, PanelContext context) throws Exception {
		//在构建首页界面前干预应用缓存
	}
	
	@Override
	default PanelDto afterBuildHomePage(Context context, PanelDto homePage) throws Exception {
		//在构建首页界面完成后干预
		return homePage;
	}
	

}
