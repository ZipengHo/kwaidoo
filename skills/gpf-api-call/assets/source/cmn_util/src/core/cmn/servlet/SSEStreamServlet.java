package cmn.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kwaidoo.ms.tool.CmnUtil;
import com.leavay.common.util.ToolUtilities;
import com.leavay.dfc.gui.LvUtil;
import com.leavay.dfc.gui.LvUtil.AutoTracer;

import cell.bap.servlet.CHttpServlet;
import cell.bap.servlet.IHttpServlet;
import cell.cmn.http.IHttpRequestService;
import cell.cmn.servlet.ICmnServlet;
import cell.cmn.session.ISessionService;
import cmn.dto.session.SessionUtil;
import cmn.http.anotation.RequestMethod;
import cmn.http.multipart.MultipartException;
import cmn.http.multipart.MultipartHttpServletRequest;
import cmn.http.multipart.MultipartResolver;
import cmn.http.server.ServletServerHttpRequest;
import cmn.http.servlet.HandlerAdapter;
import cmn.http.servlet.HandlerExceptionResolver;
import cmn.http.servlet.HandlerExecutionChain;
import cmn.http.servlet.HandlerMapping;
import cmn.http.servlet.HttpRequestHandlerAdapter;
import cmn.http.servlet.HttpResponseHandler;
import cmn.http.servlet.NoHandlerFoundException;
import cmn.http.util.HttpRequestUtils;
import cmn.http.util.HttpSessionUtil;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import cn.hutool.core.util.URLUtil;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import web.dto.Pair;

public class SSEStreamServlet extends HttpServlet{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3554964691036065432L;
	
	public final static String LOG = SSEStreamServlet.class.getSimpleName();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		boolean flag = false;
		try {
			flag = doDispatch(request, response, RequestMethod.GET);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		if(!flag) {
			flag = ICmnServlet.get().doGet(new CHttpServlet(request, response));
		}
		if(!flag) {
			super.doGet(request, response);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		boolean flag = false;
		try{
			flag = doDispatch(request, response, RequestMethod.POST);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		if(!flag) {
			flag = ICmnServlet.get().doPost(new CHttpServlet(request, response));
		}
		if(!flag) {
			super.doPost(request, response);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		boolean flag = false;
		try{
			flag = doDispatch(req, resp, RequestMethod.DELETE);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		if(!flag) {
			super.doDelete(req, resp);
		}
	}
	
//	@Override
//	protected void doHead(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		boolean flag = false;
//		try{
//			flag = doDispatch(request, response, RequestMethod.HEAD);
//		} catch (Exception e) {
//			throw new ServletException(e);
//		}
//		if(!flag) {
//			super.doHead(request, response);
//		}
//	}
//	
//	@Override
//	protected void doOptions(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		boolean flag = false;
//		try{
//			flag = doDispatch(request, response, RequestMethod.OPTIONS);
//		} catch (Exception e) {
//			throw new ServletException(e);
//		}
//		if(!flag) {
//			super.doOptions(request, response);
//		}
//	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		boolean flag = false;
		try{
			flag = doDispatch(req, resp, RequestMethod.PUT);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		if(!flag) {
			super.doPut(req, resp);
		}
	}
	
//	@Override
//	protected void doTrace(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		boolean flag = false;
//		try{
//			flag = doDispatch(request, response, RequestMethod.TRACE);
//		} catch (Exception e) {
//			throw new ServletException(e);
//		}
//		if(!flag) {
//			super.doTrace(request, response);
//		}
//	}
//	
	
	//---------------------------------分发器处理逻辑-------------------------------------------------------
	public MultipartResolver getMultipartResolver(IHttpServlet servlet) {
		return IHttpRequestService.get().getMultipartResolver(servlet);
	}
	
	public List<HandlerExceptionResolver> getHandlerExceptionResolvers(IHttpServlet servlet,RequestMethod requestMethod) {
		return IHttpRequestService.get().getHandlerExceptionResolvers(servlet, requestMethod);
	}
	
	public HttpResponseHandler getResponeHandler(IHttpServlet servlet,RequestMethod requestMethod) {
		return IHttpRequestService.get().getResponseHandler(servlet,requestMethod);
	}
	
	public List<Pair<HandlerMapping,HandlerExceptionResolver>> getHandlerMappings(IHttpServlet servlet,RequestMethod requestMethod){
		return IHttpRequestService.get().getHandlerMappings(servlet, requestMethod);
	}
	
	protected HandlerExecutionChain getHandler(HttpServletRequest request,IHttpServlet servlet,RequestMethod requestMethod) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer(LOG);
		for (Pair<HandlerMapping,HandlerExceptionResolver> pair : getHandlerMappings(servlet,requestMethod)) {
			HandlerMapping hm = pair.left;
			HandlerExecutionChain handler = hm.getHandler(request);
			tracer.debug("匹配"+hm+"结果:"+(handler != null));
			if (handler != null) {
				HandlerExceptionResolver exceptionResolver = pair.right;
				handler.setExceptionResolover(exceptionResolver);
				return handler;
			}
		}
		return null;
		
//		IHttpRequestService requestService = IHttpRequestService.get();
//		HttpRequestHandler reqHandler = requestService.getRequestHandler(servlet, requestMethod);
//		HttpResponseHandler respHandler = requestService.getResponseHandler(servlet, requestMethod);
//		List<HandlerInterceptor> interceptors = requestService.getInterceptors(servlet, requestMethod);
//		HandlerExecutionChain handler = new HandlerExecutionChain(reqHandler, respHandler, interceptors.toArray(new HandlerInterceptor[0]));
//		return handler;
	}
	
	/**
	 * Return the HandlerAdapter for this handler object.
	 * @param handler the handler object to find an adapter for
	 * @throws ServletException if no HandlerAdapter can be found for the handler. This is a fatal error.
	 */
	protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
		return new HttpRequestHandlerAdapter();
	}
	
	protected boolean doDispatch(HttpServletRequest req,HttpServletResponse resp,RequestMethod requestMethod) throws Exception{
		Tracer tracer = TraceUtil.getCurrentTracer(LOG);
		tracer.debug(LOG, "doDispatch");
//		IHttpRequestService requestService = IHttpRequestService.get();
		String path = URLUtil.getPath(req.getRequestURI());
		tracer.debug(LOG, "path = " + path);
		IHttpServlet servlet = new CHttpServlet(req, resp);
//		RequestMappingDto mapping = requestService.getRequestMapping(servlet,requestMethod);
//		if(mapping == null) {
//			tracer.debug(LOG, "没有匹配到请求映射！path = " + path);
//			return false;
//		}
		HandlerExecutionChain mappedHandler = getHandler(req, servlet, requestMethod);
		if(mappedHandler == null) {
			tracer.debug(LOG, "没有匹配到请求处理器执行链！path = " + path);
			return false;
		}
		HttpServletRequest processedRequest = req;
		boolean multipartRequestParsed = false;
		AutoTracer autoTracer = null;
		try {
			autoTracer = LvUtil.newAutoTracer();
			HttpSessionUtil.setTracer(autoTracer);
			Exception dispatchException = null;
			Object result = null;
			try {
				processedRequest = checkMultipart(servlet,req);
				multipartRequestParsed = (processedRequest != req);

				// Determine handler for the current request.
				if (mappedHandler == null || mappedHandler.getHandler() == null) {
					noHandlerFound(processedRequest, resp);
					return true;
				}
				HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
				if (!mappedHandler.applyPreHandle(processedRequest, resp)) {
					return true;
				}
				// Actually invoke the handler.
				String sessionId = req.getRequestedSessionId();
				if(CmnUtil.isStringEmpty(sessionId)) {
					HttpSession httpSession = req.getSession();
					sessionId = httpSession.getId();
				}
				if(CmnUtil.isStringEmpty(sessionId)) {
					sessionId = SessionUtil.allockThreadSessionIdIfNull();
				}
				ISessionService.get().heartbeat(sessionId);
				result = ha.handle(processedRequest, resp, requestMethod,mappedHandler.getHandler(),mappedHandler.getErrorHandler());

			}
			catch (Exception ex) {
				ex.printStackTrace();
				dispatchException = ex;
			}
			catch (Throwable err) {
				// As of 4.3, we're processing Errors thrown from handler methods as well,
				// making them available for @ExceptionHandler methods and other scenarios.
				dispatchException = new ServletException("请求分发处理失败", err);
			}
//			setAccessControlHeaders(response);
			processResult(servlet,requestMethod,processedRequest, resp, mappedHandler, result, dispatchException);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			triggerAfterCompletion(processedRequest, resp, mappedHandler, ex);
		}
		catch (Throwable err) {
			triggerAfterCompletion(processedRequest, resp, mappedHandler,
					new ServletException("请求响应结果解析处理失败", err));
		}
		finally {
			// Clean up any resources used by a multipart request.
			if (multipartRequestParsed) {
				cleanupMultipart(servlet,processedRequest);
			}
			if(autoTracer != null) {
				if(!autoTracer.isEmpty()) {
					//如果日志没有拦截记录的，那么就在最后这里输出
					System.out.println("["+Thread.currentThread().getName()+"]"+autoTracer.getTrace());
//					IPrintService.get().asyncPrint(autoTracer.getTrace());
				}
				autoTracer.close();
				HttpSessionUtil.removeTracer();
			}
		}
		return true;
	}
	
	/**
	 * Convert the request into a multipart request, and make multipart resolver available.
	 * <p>If no multipart resolver is set, simply use the existing request.
	 * @param request current HTTP request
	 * @return the processed request (multipart wrapper if necessary)
	 * @see MultipartResolver#resolveMultipart
	 */
	protected HttpServletRequest checkMultipart(IHttpServlet servlet,HttpServletRequest request) throws MultipartException {
		Tracer tracer = TraceUtil.getCurrentTracer(LOG);
		tracer.debug("检查是否包含多媒体上传请求");
		if (getMultipartResolver(servlet) != null && getMultipartResolver(servlet).isMultipart(request)) {
			if (HttpRequestUtils.getNativeRequest(request, MultipartHttpServletRequest.class) != null) {
				tracer.debug("Request is already a MultipartHttpServletRequest - if not in a forward, " +
						"this typically results from an additional MultipartFilter in web.xml");
				ToolUtilities.debug("Request is already a MultipartHttpServletRequest - if not in a forward, " +
						"this typically results from an additional MultipartFilter in web.xml");
			}
			else if (hasMultipartException(request)) {
				tracer.debug("Multipart resolution previously failed for current request - " +
						"skipping re-resolution for undisturbed error rendering");
				ToolUtilities.debug("Multipart resolution previously failed for current request - " +
						"skipping re-resolution for undisturbed error rendering");
			}
			else {
				try {
					tracer.debug("封装多媒体上传请求");
					return getMultipartResolver(servlet).resolveMultipart(request);
				}
				catch (MultipartException ex) {
					if (request.getAttribute(HttpRequestUtils.ERROR_EXCEPTION_ATTRIBUTE) != null) {
						tracer.error("Multipart resolution failed for error dispatch", ex);
						ToolUtilities.error("Multipart resolution failed for error dispatch", ex);
						// Keep processing error dispatch with regular request handle below
					}
					else {
						throw ex;
					}
				}
			}
		}else {
			String contentType = request.getContentType();
			if(CmnUtil.isStringEqual(contentType, "application/json")) {
				return new RepeatableReadRequestWrapper(request);
			}else {
				return request;
			}
			
		}
		// If not returned before: return original request.
		tracer.debug("返回常规请求");
		return request;
	}
	
	/**
	 * Clean up any resources used by the given multipart request (if any).
	 * @param request current HTTP request
	 * @see MultipartResolver#cleanupMultipart
	 */
	protected void cleanupMultipart(IHttpServlet servlet,HttpServletRequest request) {
		MultipartHttpServletRequest multipartRequest =
				HttpRequestUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
		if (multipartRequest != null) {
			getMultipartResolver(servlet).cleanupMultipart(multipartRequest);
		}
	}
	
	/**
	 * Check "javax.servlet.error.exception" attribute for a multipart exception.
	 */
	private boolean hasMultipartException(HttpServletRequest request) {
		Throwable error = (Throwable) request.getAttribute(HttpRequestUtils.ERROR_EXCEPTION_ATTRIBUTE);
		while (error != null) {
			if (error instanceof MultipartException) {
				return true;
			}
			error = error.getCause();
		}
		return false;
	}
	
	public static String getRequestUri(HttpServletRequest request) {
		String uri = (String) request.getAttribute(HttpRequestUtils.INCLUDE_REQUEST_URI_ATTRIBUTE);
		if (uri == null) {
			uri = request.getRequestURI();
		}
		return uri;
	}
	
	/**
	 * No handler found -> set appropriate HTTP response status.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @throws Exception if preparing the response failed
	 */
	/** Throw a NoHandlerFoundException if no Handler was found to process this request? **/
	private boolean throwExceptionIfNoHandlerFound = false;
	protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		if (pageNotFoundLogger.isWarnEnabled()) {
			ToolUtilities.warning("No mapping found for HTTP request with URI [" + getRequestUri(request) +
					"] in DispatcherServlet with name '" + getServletName() + "'");
//		}
		if (this.throwExceptionIfNoHandlerFound) {
			throw new NoHandlerFoundException(request.getMethod(), getRequestUri(request),
					new ServletServerHttpRequest(request).getHeaders());
		}
		else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	
	
	private void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response,
			HandlerExecutionChain mappedHandler, Exception ex) throws Exception {
		if (mappedHandler != null) {
			mappedHandler.triggerAfterCompletion(request, response, ex);
		}
		throw ex;
	}
	
	/**
	 * Handle the result of handler selection and handler invocation, which is
	 * either a ModelAndView or an Exception to be resolved to a ModelAndView.
	 */
	private void processResult(IHttpServlet servlet,RequestMethod requestMethod,HttpServletRequest request, HttpServletResponse response,
			HandlerExecutionChain mappedHandler,Object result, Exception exception) throws Exception {

		if (exception != null) {
			result = processHandlerException(servlet,requestMethod,request, response, mappedHandler, exception);
		}
		
		if(mappedHandler.getRespHandler() != null) {
			HttpResponseHandler respHandler = (HttpResponseHandler) mappedHandler.getRespHandler();
			respHandler.handle(request, response, result);
		}else {
			//TODO 处理 result
			HttpResponseHandler respHandler = getResponeHandler(servlet,requestMethod);
			respHandler.handle(request, response, result);
		}
		
		if (mappedHandler != null) {
			if(result instanceof Exception) {
				mappedHandler.triggerAfterCompletion(request, response, (Exception)result);
			}else {
				mappedHandler.triggerAfterCompletion(request, response, null);
			}
		}
	}
	
	/**
	 * Determine an error ModelAndView via the registered HandlerExceptionResolvers.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler, or {@code null} if none chosen at the time of the exception
	 * (for example, if multipart resolution failed)
	 * @param ex the exception that got thrown during handler execution
	 * @return a corresponding ModelAndView to forward to
	 * @throws Exception if no error ModelAndView found
	 */
	protected Object processHandlerException(IHttpServlet servlet,RequestMethod requestMethod,HttpServletRequest request, HttpServletResponse response,
			HandlerExecutionChain mappedHandler, Exception ex) throws Exception {
		// Check registered HandlerExceptionResolvers...
		Object exMv = null;
		Object handler = (mappedHandler != null ? mappedHandler.getHandler() : null);
		Object exceptionResolver = mappedHandler.getExceptionResolver();
		if(exceptionResolver != null && exceptionResolver instanceof HandlerExceptionResolver) {
			exMv = ((HandlerExceptionResolver)exceptionResolver).resolveException(request, response, handler, ex);
			if (exMv != null) {
				return exMv;
			}
		}
		for (HandlerExceptionResolver handlerExceptionResolver : getHandlerExceptionResolvers(servlet,requestMethod)) {
			exMv = handlerExceptionResolver.resolveException(request, response, handler, ex);
			if (exMv != null) {
				return exMv;
			}
		}
		throw ex;
	}
}