package cmn.servlet;


import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class SSEDemo {

    // 管理所有连接的注册表
    private static final Set<AsyncContext> CLIENTS = ConcurrentHashMap.newKeySet();
    private static volatile boolean BROADCAST_ENABLED = true;

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new SSEStreamServlet()), "/events");
        context.addServlet(new ServletHolder(new HomeServlet()), "/");
        context.addServlet(new ServletHolder(new AdminServlet()), "/admin");

        server.start();
        System.out.println("✅ Jetty SSE Server started on http://localhost:8080");
        server.join();
    }

    // --- SSE 流推送 Servlet ---
    public static class SSEStreamServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {

            resp.setContentType("text/event-stream");
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Cache-Control", "no-cache");
            resp.setHeader("Connection", "keep-alive");

            AsyncContext asyncContext = req.startAsync();
            asyncContext.setTimeout(0);
            CLIENTS.add(asyncContext);

            System.out.println("👤 新客户端连接: 当前连接数 = " + CLIENTS.size());

            ScheduledFuture<?> task = Executors.newSingleThreadScheduledExecutor()
                    .scheduleAtFixedRate(() -> {
                        try {
                            if (BROADCAST_ENABLED) {
                                PrintWriter writer = asyncContext.getResponse().getWriter();
                                writer.write("data: " + LocalTime.now() + "\n\n");
                                writer.flush();
                            }
                        } catch (Exception e) {
                            CLIENTS.remove(asyncContext);
                            asyncContext.complete();
                            System.out.println("❌ 客户端断开连接。当前连接数: " + CLIENTS.size());
                        }
                    }, 0, 1, TimeUnit.SECONDS);
            asyncContext.addListener(new AsyncListener() {
                public void onComplete(AsyncEvent event) {
                    task.cancel(true);
                    CLIENTS.remove(asyncContext);
                }

                public void onTimeout(AsyncEvent event) {
                    task.cancel(true);
                    CLIENTS.remove(asyncContext);
                }

                public void onError(AsyncEvent event) {
                    task.cancel(true);
                    CLIENTS.remove(asyncContext);
                }

                public void onStartAsync(AsyncEvent event) {}
            });
        }
    }

    // --- 首页 ---
    public static class HomeServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("\r\n" + 
            		"                <html>\r\n" + 
            		"                <head><title>Jetty SSE Demo</title></head>\r\n" + 
            		"                <body>\r\n" + 
            		"                    <h2>Server-Sent Events (Jetty SSE Demo)</h2>\r\n" + 
            		"                    <p><a href=\"/admin\" target=\"_blank\">打开管理中心</a></p>\r\n" + 
            		"                    <pre id=\"output\"></pre>\r\n" + 
            		"                    <script>\r\n" + 
            		"                        const evtSource = new EventSource('/events');\r\n" + 
            		"                        evtSource.onmessage = e => {\r\n" + 
            		"                            document.getElementById('output').textContent += e.data + \"\\\\n\";\r\n" + 
            		"                        };\r\n" + 
            		"                        evtSource.onerror = e => console.error(\"Error:\", e);\r\n" + 
            		"                    </script>\r\n" + 
            		"                </body>\r\n" + 
            		"                </html>\r\n" + 
            		"            ");
        }
    }

    // --- 管理中心 Servlet ---
    public static class AdminServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("\r\n" + 
            		"                <html>\r\n" + 
            		"                <head><title>Jetty SSE 管理中心</title></head>\r\n" + 
            		"                <body>\r\n" + 
            		"                    <h2>Jetty SSE 管理中心</h2>\r\n" + 
            		"                    <p>当前在线连接数: <b>" + CLIENTS.size() + "</b></p>\r\n" + 
            		"                    <p>广播状态: <b>" + (BROADCAST_ENABLED ? "🟢 开启" : "🔴 停止") + "</b></p>\r\n" + 
            		"                    <form method=\"POST\">\r\n" + 
            		"                        <button type=\"submit\" name=\"action\" value=\"toggle\">\r\n" + 
            		"                            " + (BROADCAST_ENABLED ? "停止推送" : "开启推送") + "\r\n" + 
            		"                        </button>\r\n" + 
            		"                    </form>\r\n" + 
            		"                </body>\r\n" + 
            		"                </html>\r\n" + 
            		"            ");
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            String action = req.getParameter("action");
            if ("toggle".equals(action)) {
                BROADCAST_ENABLED = !BROADCAST_ENABLED;
                System.out.println("🔁 广播状态切换: " + (BROADCAST_ENABLED ? "开启" : "停止"));
            }
            resp.sendRedirect("/admin");
        }
    }
}