package cell.bap.servlet;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.kwaidoo.ms.tool.CmnUtil;

import bap.cells.BasicCell;

public class CHttpServletRequest extends BasicCell implements IHttpServletRequest
{
    /**
     * 
     */
    private static final long serialVersionUID = 4256903638715979472L;
    private transient HttpServletRequest originalRequest;
    

    public CHttpServletRequest(HttpServletRequest originalRequest) {
        this.originalRequest = originalRequest;
    }
    

    @Override
    public String getAuthType() {
        return originalRequest.getAuthType();
    }
    
    @Override
    public Map<String, String[]> getParameterMap() {
        return originalRequest.getParameterMap();
    }

    @Override
    public List<String> getParameterNames() {
        return CmnUtil.enumerToList(originalRequest.getParameterNames());
    }

    @Override
    public String getParameter(String param) {
        return originalRequest.getParameter(param);
    }
    
    @Override
    public Object getAttribute(String attr) {
        return originalRequest.getAttribute(attr);
    }

    @Override
    public List<String> getAttributeNames() {
        return CmnUtil.enumerToList(originalRequest.getAttributeNames());
    }
    
    @Override
    public Cookie[] getCookies() {
        return originalRequest.getCookies();
    }

    @Override
    public long getDateHeader(String name) {
        return originalRequest.getDateHeader(name);
    }

    @Override
    public String getHeader(String name) {
        return originalRequest.getHeader(name);
    }

    @Override
    public List<String> getHeaders(String name) {
        return CmnUtil.enumerToList(originalRequest.getHeaders(name));
    }

    @Override
    public List<String> getHeaderNames() {
        return CmnUtil.enumerToList(originalRequest.getHeaderNames());
    }

    @Override
    public int getIntHeader(String name) {
        return originalRequest.getIntHeader(name);
    }
    
    @Override
    public String getLocalAddr() {
    	return originalRequest.getLocalAddr();
    }
    
    @Override
    public String getLocalName() {
    	return originalRequest.getLocalName();
    }
    
    @Override
    public int getLocalPort() {
    	return originalRequest.getLocalPort();
    }
    
    @Override
    public Locale getLocale() {
    	return originalRequest.getLocale();
    }
    
    @Override
    public String getServerName() {
    	return originalRequest.getServerName();
    }
    
    @Override
    public int getServerPort() {
    	return originalRequest.getServerPort();
    }
    
    @Override
    public String getScheme() {
    	return originalRequest.getScheme();
    }
    
    @Override
    public String getProtocol() {
    	return originalRequest.getProtocol();
    }

    @Override
    public String getMethod() {
        return originalRequest.getMethod();
    }

    @Override
    public String getPathInfo() {
        return originalRequest.getPathInfo();
    }

    @Override
    public String getPathTranslated() {
        return originalRequest.getPathTranslated();
    }

    // ... 此处应继续实现其他所有的方法

    // 提供获取原始HttpServletRequest的方法
    public HttpServletRequest getOriginalRequest() {
        return originalRequest;
    }

    // 实现ServletRequest接口中的其他方法
    @Override
    public String getCharacterEncoding() {
        return originalRequest.getCharacterEncoding();
    }

    @Override
    public int getContentLength() {
        return originalRequest.getContentLength();
    }

    @Override
    public String getQueryString() {
        return originalRequest.getQueryString();
    }
    
    @Override
    public String getRemoteHost() {
    	return originalRequest.getRemoteHost();
    }
    
    @Override
    public String getRemoteAddr() {
    	return originalRequest.getRemoteAddr();
    }

    @Override
    public int getRemotePort() {
    	return originalRequest.getRemotePort();
    }
    
    @Override
    public String getRemoteUser() {
        return originalRequest.getRemoteUser();
    }

    @Override
    public String getContextPath()
    {
        return originalRequest.getContextPath();
    }

    @Override
    public boolean isUserInRole(String role)
    {
        return originalRequest.isUserInRole(role);
    }

    @Override
    public String getRequestedSessionId()
    {
        return originalRequest.getRequestedSessionId();
    }

    @Override
    public String getRequestURI()
    {
        return originalRequest.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL()
    {
        return originalRequest.getRequestURL();
    }

    @Override
    public String getServletPath()
    {
        return originalRequest.getServletPath();
    }

    @Override
    public boolean isRequestedSessionIdValid()
    {
        return originalRequest.isRequestedSessionIdValid();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie()
    {
        return originalRequest.isRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromURL()
    {
        return originalRequest.isRequestedSessionIdFromUrl();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl()
    {
        return originalRequest.isRequestedSessionIdFromUrl();
    }
    
    @Override
    public void setCharacterEncoding(String encode) throws UnsupportedEncodingException {
    	originalRequest.setCharacterEncoding(encode);
    }
    
    @Override
    public AsyncContext startAsync() {
    	return originalRequest.startAsync();
    }

    @Override
    public void onClose()
    {
        
    }
}
