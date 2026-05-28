package cell.bap.servlet;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.http.Cookie;

import cell.ResourceCellIntf;

public interface IHttpServletResponse extends ResourceCellIntf
{
        void addCookie(Cookie cookie);

        public IPrintWriter getWriter() throws IOException;
        
        boolean containsHeader(String name);

        public Collection<String> getHeaders(String name);

        public Collection<String> getHeaderNames();
        
        String encodeURL(String url);

        String encodeRedirectURL(String url);

        void sendError(int sc, String msg) throws IOException;

        void sendError(int sc) throws IOException;

        void sendRedirect(String location) throws IOException;

        void setDateHeader(String name, long date);

        void addDateHeader(String name, long date);

        void setHeader(String name, String value);

        void addHeader(String name, String value);

        void setIntHeader(String name, int value);

        void addIntHeader(String name, int value);

        void setStatus(int sc);

        void setStatus(int sc, String sm);

        int getStatus();

        String getHeader(String name);

        String getCharacterEncoding();

        String getContentType();

        void setCharacterEncoding(String charset);

        void setContentLength(int len);

        void setContentLengthLong(long len);

        void setContentType(String type);

        void setBufferSize(int size);

        int getBufferSize();

        void flushBuffer() throws IOException;

        void resetBuffer();

        boolean isCommitted();

        void reset();

        void setLocale(Locale loc);

        Locale getLocale();

        // ... 其他方法按需声明
    }
