package cmn.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.common.util.javac.ClassFactory;

import cell.cmn.util.IServerConfig;
import cmn.anotation.ClassDeclare;
import cmn.exception.handler.ErrorHandler;
import cmn.proxy.ErrorProxyHandler;

@ClassDeclare(label = "服务动态代理类"
,what="提供服务动态代理类，要求服务类需要实现接口，由于动态代理对性能会有所影响，服务代理类应该声明为单例。\n"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2024-11-10"
,updateTime = "2024-11-10")
public class ProxyUtil {
	
	/**
	 * 获取全局定义的异常处理器，
	 * 1.根据服务配置参数 cmn.exception.handlers定义的接口匹配异常处理类
	 * 	2.没有指定的异常处理类是，根据服务配置参数 cmn.exception.globalhandler定义的异常处理类
	 * @param serviceClass	服务类
	 * @return
	 */
	public static ErrorHandler getErrorHandler(Class serviceClass) {
		String errorHandlerClass = IServerConfig.get().getErrorHandlerClass(serviceClass.getName());
		if(CmnUtil.isStringEmpty(errorHandlerClass)) {
			errorHandlerClass = IServerConfig.get().getGlobalErrorHandlerClass();
		}
		if(!CmnUtil.isStringEmpty(errorHandlerClass)) {
			try {
				Class<? extends ErrorHandler> handlerClazz = ClassFactory.loadClass(errorHandlerClass);
				return handlerClazz.newInstance();
			} catch (Exception e) {
				ToolUtilities.warnAndOutput(ProxyUtil.class.getSimpleName(), ToolUtilities.getFullExceptionStack(e));
			}
		}
		return null;
	}
	
	/**
	 * 构建服务的代理类，需要服务类有实现接口，通过接口类来获取代理实例
	 * 
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public static Object newProxyInstance(Object target,ErrorHandler errorHandler){
		if(errorHandler == null) {
			errorHandler = getErrorHandler(target.getClass());
		}
		if(errorHandler == null)
			return target;
		return Proxy.newProxyInstance(
                ClassFactory.getValidClassLoader(),
                target.getClass().getInterfaces(),
                new ErrorProxyHandler(target,errorHandler));
	}
	/**
	 * 构建服务的代理类，需要服务类有实现接口，通过接口类来获取代理实例
	 * 
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public static Object newProxyInstance(Object target,InvocationHandler proxyHandler){
		return Proxy.newProxyInstance(
                ClassFactory.getValidClassLoader(),
                target.getClass().getInterfaces(),
                proxyHandler);
	}

}
