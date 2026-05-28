package cmn.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestDownloadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 设置为二进制流且不缓存
        resp.setContentType("application/octet-stream");
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        
        // 测试数据大小，例如 20MB
        int size = 20 * 1024 * 1024; 
        resp.setContentLength(size);

        byte[] buffer = new byte[8192];
        new Random().nextBytes(buffer); // 填充随机数据防止压缩优化

        try (OutputStream out = resp.getOutputStream()) {
            int bytesSent = 0;
            while (bytesSent < size) {
                out.write(buffer);
                bytesSent += buffer.length;
            }
            out.flush();
        }
    }
}