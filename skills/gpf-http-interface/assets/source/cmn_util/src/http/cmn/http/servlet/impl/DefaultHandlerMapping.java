package cmn.http.servlet.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.leavay.common.util.ObjectUtils;

import cmn.exception.handler.ErrorHandler;
import cmn.http.servlet.HandlerExecutionChain;
import cmn.http.servlet.HandlerInterceptor;
import cmn.http.servlet.HandlerMapping;
import cmn.http.servlet.MappedInterceptor;
import cmn.http.util.AntPathMatcher;
import cmn.http.util.PathMatcher;
import cmn.http.util.UrlPathHelper;


public class DefaultHandlerMapping implements HandlerMapping{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 771526646067453842L;

	/**
	 * 请求处理器
	 */
	private Object defaultHandler;
	/**
	 * 请求响应处理器
	 */
	private Object respHandler;
	/**
	 * 匹配路径
	 */
	private final String[] includePatterns;
	/**
	 * 排除路径
	 */
	private final String[] excludePatterns;
	
	/**
	 * 请求处理拦截器
	 */
	private List<HandlerInterceptor> interceptors = new ArrayList<HandlerInterceptor>();
	
	/**
	 * 异常处理类
	 */
	private ErrorHandler errorHandler;
	
	private UrlPathHelper urlPathHelper = new UrlPathHelper();

	private PathMatcher pathMatcher = new AntPathMatcher();
	
	public DefaultHandlerMapping() {
		this(null,null,null);
	}
	
	public DefaultHandlerMapping(String[] includePatterns, String[] excludePatterns,Object handler) {
		this.includePatterns = includePatterns;
		this.excludePatterns = excludePatterns;
		this.defaultHandler = handler;
	}
	
	public DefaultHandlerMapping(String[] includePatterns, String[] excludePatterns,Object handler,Object respHandler) {
		this.includePatterns = includePatterns;
		this.excludePatterns = excludePatterns;
		this.defaultHandler = handler;
		this.respHandler = respHandler;
	}
	
	public List<HandlerInterceptor> getInterceptors() {
		return interceptors;
	}
	
	public void setInterceptors(List<HandlerInterceptor> interceptors) {
		this.interceptors = interceptors;
	}
	
	/**
	 * Set the default handler for this handler mapping.
	 * This handler will be returned if no specific mapping was found.
	 * <p>Default is {@code null}, indicating no default handler.
	 */
	public void setDefaultHandler(Object defaultHandler) {
		this.defaultHandler = defaultHandler;
	}

	/**
	 * Return the default handler for this handler mapping,
	 * or {@code null} if none.
	 */
	public Object getDefaultHandler() {
		return this.defaultHandler;
	}
	
	public Object getRespHandler() {
		return respHandler;
	}
	
	public void setRespHandler(Object respHandler) {
		this.respHandler = respHandler;
	}
	
	/**
	 * Determine a match for the given lookup path.
	 * @param lookupPath the current request path
	 * @param pathMatcher a path matcher for path pattern matching
	 */
	public boolean matches(String lookupPath, PathMatcher pathMatcher) {
		PathMatcher pathMatcherToUse = (this.pathMatcher != null ? this.pathMatcher : pathMatcher);
		if (!ObjectUtils.isEmpty(this.excludePatterns)) {
			for (String pattern : this.excludePatterns) {
				if (pathMatcherToUse.match(pattern, lookupPath)) {
					return false;
				}
			}
		}
		if (ObjectUtils.isEmpty(this.includePatterns)) {
			return true;
		}
		for (String pattern : this.includePatterns) {
			if (pathMatcherToUse.match(pattern, lookupPath)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Look up a handler for the given request, falling back to the default
	 * handler if no specific one is found.
	 * @param request current HTTP request
	 * @return the corresponding handler instance, or the default handler
	 * @see #getHandlerInternal
	 */
	@Override
	public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
		if(!matches(lookupPath, pathMatcher))
			return null;
		Object handler = getDefaultHandler();
//		if (handler == null) {
//			handler = new DefaultServletHttpRequestHandler();
//		}
		if(handler == null)
			return null;
		HandlerExecutionChain executionChain = getHandlerExecutionChain(handler, request,errorHandler);
//		if (CorsUtils.isCorsRequest(request)) {
//			CorsConfiguration globalConfig = this.globalCorsConfigSource.getCorsConfiguration(request);
//			CorsConfiguration handlerConfig = getCorsConfiguration(handler, request);
//			CorsConfiguration config = (globalConfig != null ? globalConfig.combine(handlerConfig) : handlerConfig);
//			executionChain = getCorsHandlerExecutionChain(request, executionChain, config);
//		}
		return executionChain;
	}

	/**
	 * Build a {@link HandlerExecutionChain} for the given handler, including
	 * applicable interceptors.
	 * <p>The default implementation builds a standard {@link HandlerExecutionChain}
	 * with the given handler, the common interceptors of the handler mapping, and any
	 * {@link MappedInterceptor MappedInterceptors} matching to the current request URL. Interceptors
	 * are added in the order they were registered. Subclasses may override this
	 * in order to extend/rearrange the list of interceptors.
	 * <p><b>NOTE:</b> The passed-in handler object may be a raw handler or a
	 * pre-built {@link HandlerExecutionChain}. This method should handle those
	 * two cases explicitly, either building a new {@link HandlerExecutionChain}
	 * or extending the existing chain.
	 * <p>For simply adding an interceptor in a custom subclass, consider calling
	 * {@code super.getHandlerExecutionChain(handler, request)} and invoking
	 * {@link HandlerExecutionChain#addInterceptor} on the returned chain object.
	 * @param handler the resolved handler instance (never {@code null})
	 * @param request current HTTP request
	 * @return the HandlerExecutionChain (never {@code null})
	 * @see #getAdaptedInterceptors()
	 */
	protected HandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest request,ErrorHandler errorHandler) {
		HandlerExecutionChain chain = (handler instanceof HandlerExecutionChain ?
				(HandlerExecutionChain) handler : new HandlerExecutionChain(handler,respHandler));

		String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
		for (HandlerInterceptor interceptor : getInterceptors()) {
			if (interceptor instanceof MappedInterceptor) {
				MappedInterceptor mappedInterceptor = (MappedInterceptor) interceptor;
				if (mappedInterceptor.matches(lookupPath, this.pathMatcher)) {
					chain.addInterceptor(mappedInterceptor.getInterceptor());
				}
			}
			else {
				chain.addInterceptor(interceptor);
			}
		}
		chain.setErrorHandler(getErrorHandler());
		return chain;
	}
	
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}
	public DefaultHandlerMapping setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
		return this;
	}
}
