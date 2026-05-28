package cell.cmn.http;

import java.util.ArrayList;
import java.util.List;

import cmn.anotation.ClassDeclare;
import cmn.http.servlet.DispatcherMappingBuilder;
import cmn.http.servlet.HandlerInterceptor;
import cmn.http.servlet.HandlerMapping;
import cmn.http.servlet.HttpRequestHandler;
import cmn.http.servlet.HttpResponseHandler;
import cmn.http.servlet.impl.DefaultHandlerMapping;
import cmn.http.servlet.impl.DefaultHttpRequestHandler;
import cmn.http.servlet.impl.JsonHttpResponseHandler;
@ClassDeclare(
		label = "短链请求映射", 
		what = "", 
		why = "",
		how = "", 
		developer = "陈晓斌", 
		createTime = "2025-08-22", 
		updateTime = "2025-08-22", 
		version = "1.0"
		)
public class ShortLinkDispatcherMappingBuilder implements DispatcherMappingBuilder{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4563579180165979476L;
	
	@Override
	public String[] getIncludePatterns() {
		return new String[]{"/shortlink/**"};
	}
	
	@Override
	public String[] getExcludePatterns() {
		return null;
	}

	@Override
	public HandlerMapping getHandlerMapping() {
		//分发处理包含哪些链接
		String[] includePatterns = getIncludePatterns();
		//分发处理排除哪些链接
		String[] excludePatterns = getExcludePatterns();
		//默认的请求处理器
		HttpRequestHandler handler = new DefaultHttpRequestHandler();
		//请求处理拦截器，可在请求处理器前后添加处理，如记录日志、鉴权等
		List<HandlerInterceptor> interceptors = new ArrayList<>();
		//构建最终的分发处理映射
		DefaultHandlerMapping handlerMapping = new DefaultHandlerMapping(includePatterns,excludePatterns,handler);
		handlerMapping.setInterceptors(interceptors);
		//设置请求响应处理器
		HttpResponseHandler respHandler = new JsonHttpResponseHandler();
		handlerMapping.setRespHandler(respHandler);
		//设置异常处理器
//		handlerMapping.setErrorHandler(errorHandler)
		return handlerMapping;
	}

}
