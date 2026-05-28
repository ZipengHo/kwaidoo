package cell.gpf.study.action.view.external;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import fe.cmn.app.ability.LaunchUrl;
import fe.cmn.data.UrlMsgDto;
import fe.cmn.embedPage.InitialSourceType;
import fe.cmn.panel.PanelContext;
import fe.cmn.panel.SinglePanelDto;
import fe.cmn.panel.ability.PopDialog;
import fe.cmn.sys.QueryUrl;
import fe.cmn.widget.EmbedPageDto;
import fe.cmn.widget.ListenerDto;
import fe.cmn.widget.WindowSizeDto;
import fe.util.component.AbsComponent;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;
import gpf.dc.basic.param.view.EmbedViewParameter.UrlOpenType;
@ClassDeclare(label = "打开外部链接代码样例"
,what="演示如何通过前端回调能力LaunchUrl打开外部链接"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-21"
,updateTime = "2025-02-21")
public interface IStudyLaunchUrlAction <T extends BaseFeActionParameter> extends CellIntf, BaseFeActionIntf<T> {
	
	public static final String HTML_SRC = "test.html";
	
	public static final String HTML_CONTENT = "<h1>Heading Example</h1>\r\n"
			+ "    <p>This is a paragraph.</p>\r\n"
			+ "    <blockquote>This is a quote.</blockquote>\r\n"
			+ "    <ul>\r\n"
			+ "      <li>First item</li>\r\n"
			+ "      <li>Second item</li>\r\n"
			+ "      <li>Third item</li>\r\n"
			+ "    </ul>";
	
	@Override
	default Object execute(T input) throws Exception {
		// 界面动作监听传入对象
		ListenerDto listener = input.getListener();
		// 界面上下文对象
		PanelContext panelContext = input.getPanelContext();
		// 界面组件对象
		AbsComponent component = (AbsComponent) input.getCurrentComponent();

		UrlMsgDto urlMsg = QueryUrl.queryUrlMsg(panelContext.getChannel());
		String hyperlink = urlMsg.getOrigin() + urlMsg.getPathname() + HTML_SRC;
		UrlOpenType type = UrlOpenType.redirect;
		if(type == UrlOpenType.redirect) {
			//重定向到地址
			LaunchUrl.launch(panelContext.getChannel(), hyperlink, false);
		}else if(type == UrlOpenType.newTab) {
			//打开浏览器新标签页
			LaunchUrl.launch(panelContext.getChannel(), hyperlink, true);
		}else {
			//弹窗显示内嵌网页
			// 安卓下设置urlBypass可跨域访问，web端暂时不支持
			EmbedPageDto embedPage = new EmbedPageDto().setContent(hyperlink).setInitialSourceType(InitialSourceType.url);
			SinglePanelDto panel = SinglePanelDto.wrap(embedPage);
			panel.setPreferSize(WindowSizeDto.all(0.8, 0.8));
			PopDialog.show(panelContext, "", panel);
			
			//自定义网页内容
			EmbedPageDto embedPage2 = new EmbedPageDto().setContent(HTML_CONTENT).setInitialSourceType(InitialSourceType.html);
			SinglePanelDto panel2 = SinglePanelDto.wrap(embedPage2);
			panel2.setPreferSize(WindowSizeDto.all(0.8, 0.8));
			PopDialog.show(panelContext, "", panel2);
			
		}
		return null;
	}

	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}
