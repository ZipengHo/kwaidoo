package cell.bap.servlet;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.http.Cookie;

import cell.ResourceCellIntf;
public interface IHttpServletRequest extends ResourceCellIntf
{

    String getAuthType();
    Cookie[] getCookies();
    public List<String> getHeaders(String name);
    public List<String> getHeaderNames();
    long getDateHeader(String name);
    String getHeader(String name);
    int getIntHeader(String name);
    public Map<String, String[]> getParameterMap();
    public String getParameter(String param);
    public List<String> getParameterNames();
    public Object getAttribute(String attr);
    public List<String> getAttributeNames();
    String getLocalAddr();
    String getLocalName();
    Locale getLocale();
    int getLocalPort();
    String getServerName();
    int getServerPort();
    String getScheme();
    String getProtocol();
    String getMethod();
    String getPathInfo();
    String getPathTranslated();
    String getContextPath();
    String getQueryString();
    String getRemoteHost();
    String getRemoteAddr();
    int getRemotePort();
    String getRemoteUser();
    boolean isUserInRole(String role);
    String getRequestedSessionId();
    String getRequestURI();
    StringBuffer getRequestURL();
    String getServletPath();
    public int getContentLength();
    public String getCharacterEncoding();
    boolean isRequestedSessionIdValid();
    boolean isRequestedSessionIdFromCookie();
    boolean isRequestedSessionIdFromURL();
    boolean isRequestedSessionIdFromUrl();
    public void setCharacterEncoding(String encode)throws UnsupportedEncodingException;
    AsyncContext startAsync();
}