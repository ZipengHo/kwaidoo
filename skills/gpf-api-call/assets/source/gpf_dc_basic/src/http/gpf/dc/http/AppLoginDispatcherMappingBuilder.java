package gpf.dc.http;

import cmn.anotation.ClassDeclare;
import cmn.http.servlet.*;
import cmn.http.servlet.impl.DefaultHandlerMapping;
import cmn.http.servlet.impl.DefaultHttpRequestHandler;
import cmn.http.servlet.impl.DefaultHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

@ClassDeclare(
		label = "GPF应用登录认证分发处理配置", 
		what = "", 
		why = "",
		how = "", 
		developer = "陈晓斌", 
		createTime = "2025-04-27", 
		updateTime = "2025-04-27", 
		version = "1.0"
		)
public class AppLoginDispatcherMappingBuilder implements DispatcherMappingBuilder{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2442594869857370133L;

	@Override
	public String[] getIncludePatterns() {
		return new String[]{"/gpfdc/app/**"};
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
		//除了登录、登出操作，其他操作都需要加会话有效认证
		MappedInterceptor validateLoginInterceptor = new MappedInterceptor(null, new String[]{"/gpfdc/app/login","/gpfdc/app/logout"
				,"/gpfdc/app/events/**"},new ValidateSessionInterceptor());
		interceptors.add(validateLoginInterceptor);
//		interceptors.add(new HttpRequestLogInterceptor());
		
		//构建最终的分发处理映射
		DefaultHandlerMapping handlerMapping = new DefaultHandlerMapping(includePatterns,excludePatterns,handler);
		handlerMapping.setInterceptors(interceptors);
		//设置请求响应处理器
		HttpResponseHandler respHandler = new DefaultHttpResponseHandler();
		handlerMapping.setRespHandler(respHandler);
//		JsonHttpResponseHandler respHandler = new JsonHttpResponseHandler();
//		handlerMapping.setRespHandler(respHandler);
		//设置异常处理器
//		handlerMapping.setErrorHandler(errorHandler)
		return handlerMapping;
	}

}
