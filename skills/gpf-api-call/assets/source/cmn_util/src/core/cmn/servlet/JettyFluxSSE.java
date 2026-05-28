package cmn.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.AsyncContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.kwaidoo.ms.tool.CmnUtil;
import com.leavay.common.util.MppContext;
import com.leavay.common.util.javac.ClassFactory;
import com.leavay.dfc.mgr.starter.CDaoStarter;
import com.leavay.starter.CStarter;

import jetty.emb.ExampleServlet;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

public class JettyFluxSSE {

	public static void main(String[] args) throws Exception {
//        Server server = new Server(8080);
//        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//        context.setContextPath("/");
//        server.setHandler(context);
//        context.addServlet(SSEStreamServlet.class, "/flux-events/abc");
//        context.addServlet(HomeServlet.class, "/");
//
//        server.start();
//        System.out.println("✅ Jetty + Flux SSE Server started on http://localhost:8080");
//        server.join();
		JettyFluxSSE sseServer = new JettyFluxSSE();
		sseServer.start();
	}

	public final static String MODULE_NAME = "jetty";

	// HTTP配置参数
	public static final String CONF_JETTY_PORT = "jetty.http.port";
	public static final String CONF_JETTY_ENABLE_HTTP = "jetty.http.enable";

	// HTTPS配置参数
	public static final String CONF_JETTY_HTTPS_PORT = "jetty.https.port";
	public static final String CONF_JETTY_ENABLE_HTTPS = "jetty.https.enable";

	// --- 原有的 keystore 配置 ---
	public static final String CONF_JETTY_HTTPS_KEYSTORE_PATH = "jetty.https.keystore.path";
	public static final String CONF_JETTY_HTTPS_KEYSTORE_PASSWORD = "jetty.https.keystore.password";
	public static final String CONF_JETTY_HTTPS_KEY_PASSWORD = "jetty.https.key.password";

	// --- 新增的 crt/key 配置 ---
	public static final String CONF_JETTY_HTTPS_CERT_PATH = "com.bap.nio.wss.file.crt";
	public static final String CONF_JETTY_HTTPS_KEY_PATH = "com.bap.nio.wss.file.key";

	public static final String CONF_JETTY_HTTPS_TRUSTSTORE_PATH = "jetty.https.truststore.path";
	public static final String CONF_JETTY_HTTPS_TRUSTSTORE_PASSWORD = "jetty.https.truststore.password";
	public static final String CONF_JETTY_HTTPS_PROTOCOLS = "jetty.https.protocols";
	public static final String CONF_JETTY_HTTPS_CIPHER_SUITES = "jetty.https.cipher.suites";

	// 其他原有配置参数
	public static final String CONF_JETTY_RESOURCE = "jetty.resource.path";
	public static final String CONF_JETTY_SERVLET_PATH = "jetty.servlet.path";
	public static final String CONF_JETTY_SERVLET_CLASS = "jetty.servlet.class";
	public static final String CONF_JETTY_SSE_SERVLET_CLASS = "jetty.sse.servlet.class";
	public static final String CONF_JETTY_RESOURCE_SERVLET_CLASS = "jetty.resource.servlet.class";
	public static final String CONF_JETTY_SESSION_LISTENER_CLASS = "jetty.session.listener.class";
	public static final String CONF_JETTY_POOL_MAX_THREADS = "jetty.threadpool.maxThreads";
	public static final String CONF_JETTY_POOL_MIN_THREADS = "jetty.threadpool.minThreads";

	public void start() throws Exception {
		// 线程池配置
		int maxThreads = MppContext.getInt(CONF_JETTY_POOL_MAX_THREADS, 200);
		int minThreads = MppContext.getInt(CONF_JETTY_POOL_MIN_THREADS, 8);
		System.out.println("==============Jetty最大连接数" + maxThreads);
		QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads, minThreads);
		Server server = new Server(threadPool);

		// 配置连接器
		// 存储所有启用的连接器
		LinkedHashMap<String, Connector> connectors = new LinkedHashMap<>();

		// 配置HTTP连接器
		ServerConnector httpConnector = new ServerConnector(server);
		httpConnector.setPort(8080);
		connectors.put("HTTP", httpConnector);
		// 设置服务器连接器
		server.setConnectors(connectors.values().toArray(new Connector[0]));
		// 设置表单大小不限制
		System.setProperty("org.eclipse.jetty.server.Request.maxFormContentSize", "-1");

		// 创建Servlet上下文
		ServletContextHandler servletContext = new ServletContextHandler();
		// 配置预压缩和缓存
		servletContext.setInitParameter("precompressed", "br=.br,gzip=.gz,bzip2=.bz");
		servletContext.setInitParameter("gzip", "true");
		servletContext.setInitParameter(DefaultServlet.CONTEXT_INIT + "cacheControl", "max-age=0,public");

		servletContext.addServlet(new ServletHolder(ChatFluxSSEServlet.class), "/flux-events/*");
		servletContext.addServlet(new ServletHolder(ForwardServlet.class), "/static/*");
		servletContext.addServlet(HomeServlet.class, "/");
//        // 配置Gzip压缩
		GzipHandler gzipHandler = new GzipHandler();
		// 禁用 GzipHandler 对 SSE 的处理（如果默认没有禁用）
		gzipHandler.addExcludedMimeTypes("text/event-stream"); 
		gzipHandler.setHandler(servletContext);

//        // 配置处理器集合
		HandlerCollection handlers = new HandlerCollection(
//        		initStasticHandler("logs")
		);
		handlers.addHandler(gzipHandler);

		server.setHandler(handlers);

		// 启动服务器
		server.start();

	}

	// --- 首页 ---
	// JettyFluxSSE.java (HomeServlet 内部)

	public static class HomeServlet extends DefaultServlet {
	    @Override
	    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	        resp.setContentType("text/html;charset=UTF-8");
	        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	        resp.setHeader("Pragma", "no-cache");
	        resp.getWriter()
	            .println("\r\n" + "                <html>\r\n"
	                    + "                <head><title>AI Chat SSE Demo</title></head>\r\n"
	                    + "                <body>\r\n" 
	                    + "                    <h2>AI Chatting with Flux SSE</h2>\r\n"
	                    + "                    <div style='border: 1px solid #ccc; padding: 10px; min-height: 200px;'>\r\n"
	                    + "                        <p><strong>User:</strong> 请给我一个AI回复。</p>\r\n"
	                    + "                        <p><strong>AI:</strong> <span id=\"output\"></span></p>\r\n" // 目标区域
	                    + "                    </div>\r\n" 
	                    + "                    <button onclick=\"startChat()\">Start Chat</button>\r\n"
	                    + "                    <script>\r\n"
	                    + "                        function startChat() {\r\n"
	                    + "                            const output = document.getElementById('output');\r\n"
	                    + "                            output.textContent = ''; // 清空之前的回复\r\n"
	                    + "                            // *** 连接到新的聊天端点 ***\r\n"
	                    + "                            const sse = new EventSource('/static/sse');\r\n" 
	                    + "                            \r\n"
	                    + "                            sse.onmessage = e => {\r\n"
	                    + "                                // 接收到的是单个字符，实现打字机效果\r\n"
	                    + "                                output.textContent += e.data;\r\n"
	                    + "                            };\r\n"
	                    + "                            \r\n"
	                    + "                            sse.onerror = e => {\r\n"
	                    + "                                console.error(\"SSE Error:\", e);\r\n"
	                    + "                                sse.close(); // 发生错误时关闭连接\r\n"
	                    + "                            };\r\n"
	                    + "                        }\r\n"
	                    + "                    </script>\r\n" + "                </body>\r\n"
	                    + "                </html>\r\n" + "            ");
	    }
	}

	public final static String LOG = JettyFluxSSE.class.getSimpleName();

	// --- 核心：Flux 生成事件并推送 ---
	public static class ForwardServlet extends DefaultServlet {
		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			String accept = request.getHeader("Accept");
		    if (accept != null && accept.contains("text/event-stream")) {
		        request.getRequestDispatcher("/flux-events").forward(request, response);
		    }
		}
	}
	
	public static class FluxSSEServlet extends DefaultServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

			resp.setContentType("text/event-stream");
			resp.setCharacterEncoding("UTF-8");
			resp.setHeader("Cache-Control", "no-cache");
			resp.setHeader("Connection", "keep-alive");

			final AsyncContext async = req.startAsync();
			async.setTimeout(0);

			final PrintWriter writer = resp.getWriter();

			// 创建一个每秒发出一个事件的 Flux
			Flux<String> flux = Flux.interval(Duration.ofSeconds(1)).map(i -> "Server Time: " + LocalTime.now())
					.take(30); // 限制 30 条后自动完成
			final AtomicReference<Disposable> subRef = new AtomicReference<>();
			// 订阅 Flux
			Disposable subscription = flux.subscribe(
					// onNext
					data -> {
						try {
							writer.write("data: " + data + "\n\n");
							writer.flush();
						} catch (Exception e) {
							System.err.println("❌ Write failed: " + e.getMessage());
							Disposable s = subRef.get();
							if (s != null && !s.isDisposed()) {
								s.dispose();
							}
							async.complete();
						}
					},
					// onError
					err -> {
						System.err.println("⚠️ Flux error: " + err.getMessage());
						async.complete();
					},
					// onComplete
					() -> {
						System.out.println("✅ Flux completed, closing connection");
						async.complete();
					});
			subRef.set(subscription);
		}
	}
	
	// JettyFluxSSE.java (在文件末尾，作为静态内部类添加)

	// --- 模拟 AI 聊天的 Servlet ---
	public static class ChatFluxSSEServlet extends HttpServlet {
	    
	    private final String AI_RESPONSE = "你好！我是一个由 Reactor Flux 驱动的AI助手。你的问题很有趣，请给我一点时间逐字生成答案。我将以每 100 毫秒的速度，发送我的回复给你。";

	    @Override
	    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	        
	        // 1. 设置 SSE 头部
	        resp.setContentType("text/event-stream");
	        resp.setCharacterEncoding("UTF-8");
	        resp.setHeader("Cache-Control", "no-cache");
	        resp.setHeader("Connection", "keep-alive");

	        // 2. 启动异步上下文 (核心: 保持连接开放)
	        final AsyncContext async = req.startAsync();
	        async.setTimeout(0); // 永不超时
	        
	        final PrintWriter writer = resp.getWriter();

	        // 3. 模拟 AI 响应流
	        // a. 将回复字符串分割成字符流
	        Flux<Character> charFlux = Flux.fromStream(AI_RESPONSE.chars().boxed())
                    .map(i -> (char) i.intValue());
	        
	        // b. 使用 interval 模拟生成延迟 (例如每 100ms 吐出一个字符)
	        Flux<String> delayedFlux = Flux.zip(
	                                            charFlux, 
	                                            Flux.interval(Duration.ofMillis(100))
	                                        )
	                                        .map(tuple -> String.valueOf(tuple.getT1()))
	                                        .doFinally(signal -> {
	                                            // 确保在流完成后关闭连接
	                                            System.out.println("✅ AI 响应流完成.");
	                                            async.complete();
	                                        });

	        final AtomicReference<Disposable> subRef = new AtomicReference<>();
	        
	        // 4. 订阅 Flux 并推送数据
	        Disposable subscription = delayedFlux.subscribe(
	                // onNext (推送每个字符)
	                character -> {
	                    try {
	                        // 推送 'data' 字段，注意：这里是逐个字符发送
	                        // 我们只发送 data: <character>，不加双换行符 \n\n
	                        // 客户端的 EventSource.onmessage 会将所有 data 行合并，但我们希望
	                        // 客户端的 JS 逐个处理，实现“打字机”效果。
	                        // 对于简单的聊天，我们可以在这里直接发送完整的 SSE 事件块。
	                        
	                        // 标准 SSE 格式: data: <内容>\n\n
	                        writer.write("data: " + character + "\n\n");
	                        writer.flush();
	                        
	                    } catch (Exception e) {
	                        System.err.println("❌ 写入失败，连接可能已关闭: " + e.getMessage());
	                        Disposable s = subRef.get();
	                        if (s != null && !s.isDisposed()) {
	                            s.dispose();
	                        }
	                        // 如果写入失败，应立即关闭连接
	                        async.complete();
	                    }
	                },
	                // onError
	                err -> {
	                    System.err.println("⚠️ Flux 错误: " + err.getMessage());
	                    async.complete();
	                }
	                // onComplete 由 doFinally 处理
	        );
	        subRef.set(subscription);
	    }
	}
}
