package cmn.servlet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RepeatableReadRequestWrapper extends HttpServletRequestWrapper {

    // 缓存请求体字节数组（核心）
    private final byte[] requestBody;

    public RepeatableReadRequestWrapper(HttpServletRequest request) {
        super(request);
        // 读取原始流并缓存到字节数组
        try {
        	this.requestBody = readInputStreamToBytes(request.getInputStream());
        }catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * 读取 InputStream 到字节数组（一次性读完并缓存）
     */
    private byte[] readInputStreamToBytes(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toByteArray();
        }
    }

    /**
     * 重写 getInputStream()：返回缓存的字节流副本（每次调用都新建流，指针重置）
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody);
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // 异步场景需实现，同步场景可忽略
            }
        };
    }

    /**
     * 重写 getReader()：避免字符流与字节流混用导致的流关闭问题
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    // 可选：提供获取缓存请求体的方法（方便业务直接使用）
    public String getRequestBodyAsString() {
        return new String(requestBody, StandardCharsets.UTF_8);
    }
}