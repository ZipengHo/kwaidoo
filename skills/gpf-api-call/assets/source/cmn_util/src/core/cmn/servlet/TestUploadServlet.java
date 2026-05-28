package cmn.servlet;

import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestUploadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        byte[] buffer = new byte[8192];
        long totalRead = 0;
        try (ServletInputStream in = req.getInputStream()) {
            while (in.read(buffer) != -1) {
                // 仅读取，不处理
            }
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}