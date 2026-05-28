package cell.bap.servlet;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import bap.cells.BasicCell;

public class CHttpServletResponse extends BasicCell implements IHttpServletResponse
{

    private static final long serialVersionUID = -1931313977653352949L;

    @Override
    public void onClose()
    {
    }

    private transient HttpServletResponse originalResponse;

    public CHttpServletResponse(HttpServletResponse response)
    {
        this.originalResponse = response;
    }

    @Override
    public void addCookie(Cookie cookie)
    {
        originalResponse.addCookie(cookie);
    }

    @Override
    public IPrintWriter getWriter() throws IOException
    {
        return new CPrintWriter(originalResponse.getWriter());
    }
    
    @Override
    public boolean containsHeader(String name)
    {
        return originalResponse.containsHeader(name);
    }

    @Override
    public String encodeURL(String url)
    {
        return originalResponse.encodeURL(url);
    }

    @Override
    public String encodeRedirectURL(String url)
    {
        return originalResponse.encodeRedirectURL(url);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException
    {
        originalResponse.sendError(sc, msg);
    }

    @Override
    public void sendError(int sc) throws IOException
    {
        originalResponse.sendError(sc);
    }

    @Override
    public void sendRedirect(String location) throws IOException
    {
        originalResponse.sendRedirect(location);
    }

    @Override
    public void setDateHeader(String name, long date)
    {
        originalResponse.setDateHeader(name, date);
    }

    @Override
    public void addDateHeader(String name, long date)
    {
        originalResponse.addDateHeader(name, date);
    }

    @Override
    public void setHeader(String name, String value)
    {
        originalResponse.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value)
    {
        originalResponse.addHeader(name, value);
    }

    @Override
    public void setIntHeader(String name, int value)
    {
        originalResponse.setIntHeader(name, value);
    }

    @Override
    public void addIntHeader(String name, int value)
    {
        originalResponse.addIntHeader(name, value);
    }

    @Override
    public void setStatus(int sc)
    {
        originalResponse.setStatus(sc);
    }

    @Override
    public void setStatus(int sc, String sm)
    {
        originalResponse.setStatus(sc, sm);
    }

    @Override
    public int getStatus()
    {
        return originalResponse.getStatus();
    }

    @Override
    public String getHeader(String name)
    {
        return originalResponse.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name)
    {
        return originalResponse.getHeaders(name);
    }

    @Override
    public Collection<String> getHeaderNames()
    {
        return originalResponse.getHeaderNames();
    }

    @Override
    public String getCharacterEncoding()
    {
        return originalResponse.getCharacterEncoding();
    }

    @Override
    public String getContentType()
    {
        return originalResponse.getContentType();
    }

    @Override
    public void setCharacterEncoding(String charset)
    {
        originalResponse.setCharacterEncoding(charset);
    }

    @Override
    public void setContentLength(int len)
    {
        originalResponse.setContentLength(len);
    }

    @Override
    public void setContentLengthLong(long len)
    {
        originalResponse.setContentLengthLong(len);
    }

    @Override
    public void setContentType(String type)
    {
        originalResponse.setContentType(type);
    }

    @Override
    public void setBufferSize(int size)
    {
        originalResponse.setBufferSize(size);
    }

    @Override
    public int getBufferSize()
    {
        return originalResponse.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException
    {
        originalResponse.flushBuffer();
    }

    @Override
    public void resetBuffer()
    {
        originalResponse.resetBuffer();
    }

    @Override
    public boolean isCommitted()
    {
        return originalResponse.isCommitted();
    }

    @Override
    public void reset()
    {
        originalResponse.reset();
    }

    @Override
    public void setLocale(Locale loc)
    {
        originalResponse.setLocale(loc);
    }

    @Override
    public Locale getLocale()
    {
        return originalResponse.getLocale();
    }

    // ... 其他方法按需实现

    // 获取原始HttpServletResponse对象
    public HttpServletResponse getOriginalResponse()
    {
        return originalResponse;
    }
}
