package cell.example.http.handler;

import cmn.http.anotation.RequestMethod;
import cmn.http.servlet.HttpRequestHandler;
import cmn.http.servlet.impl.DefaultHttpRequestHandler;
import cmn.exception.handler.ErrorHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomHttpRequestHandler implements HttpRequestHandler {

    private final DefaultHttpRequestHandler delegate = new DefaultHttpRequestHandler();

    @Override
    public Object handleRequest(
        HttpServletRequest request,
        HttpServletResponse response,
        RequestMethod requestMethod,
        ErrorHandler errorHandler
    ) throws Exception {
        String tenantCode = request.getHeader("X-Tenant-Code");
        request.setAttribute("tenantCode", tenantCode);
        return delegate.handleRequest(request, response, requestMethod, errorHandler);
    }
}
