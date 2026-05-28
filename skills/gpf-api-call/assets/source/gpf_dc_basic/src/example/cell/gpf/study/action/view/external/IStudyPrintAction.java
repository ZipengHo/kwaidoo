package cell.gpf.study.action.view.external;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import fe.cmn.data.UrlMsgDto;
import fe.cmn.panel.PanelContext;
import fe.cmn.sys.PrintHtml;
import fe.cmn.sys.PrintImage;
import fe.cmn.sys.PrintPdf;
import fe.cmn.sys.QueryUrl;
import fe.cmn.widget.ListenerDto;
import fe.util.component.AbsComponent;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;
@ClassDeclare(label = "PDF、图片、HTML打印代码样例"
,what="演示调用前端能能力打印PDF、图片、HTML，HTML网页只支持同源内容"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-20"
,updateTime = "2025-02-20")
public interface IStudyPrintAction<T extends BaseFeActionParameter> extends CellIntf, BaseFeActionIntf<T> {
	
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


		// 打印PDF文件
		String PDF_SRC = "https://kwaidoo.com/cdn-flutter/example/grid.pdf";
		PrintPdf.printFromFile(panelContext.getChannel(), PDF_SRC);
//		 打印PDF字节数据打印
//		PrintPdf.print(panelContext.getChannel(), bytes)
		// 打印图片文件
		String IMAGE_SRC = "https://kwaidoo.com/cdn-flutter/images/logo.png";
//		PrintImage.printFromFile(panelContext.getChannel(), IMAGE_SRC);
		// 打印图片字节数据
		//需要在布局上指定容器上开启截图功能,才能获取屏幕快照
		byte[] bytes = component.getScreenshot(panelContext, "mainBox");
		PrintImage.print(panelContext.getChannel(), bytes);
		// 打印html文件
		UrlMsgDto urlMsg = QueryUrl.queryUrlMsg(panelContext.getChannel());
		String htmlSrc = urlMsg.getOrigin() + urlMsg.getPathname() + HTML_SRC;
		PrintHtml.printFromFile(panelContext.getChannel(), htmlSrc);
		// 打印html内容
		PrintHtml.print(panelContext.getChannel(), HTML_CONTENT);
		return null;
	}

	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}
