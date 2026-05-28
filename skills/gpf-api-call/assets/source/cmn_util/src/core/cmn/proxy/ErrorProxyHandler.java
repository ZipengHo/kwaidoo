package cmn.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cmn.anotation.FieldDeclare;
import cmn.exception.handler.ErrorHandler;

public class ErrorProxyHandler implements InvocationHandler {
	@FieldDeclare(label = "服务类",desc = "")
	Object target;
	@FieldDeclare(label = "异常处理器",desc = "")
	ErrorHandler errorHandler;
	
	public ErrorProxyHandler(Object target,ErrorHandler errorHandler) {
		this.target = target;
		this.errorHandler = errorHandler;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
	        Object result = method.invoke(target, args);
	        return result;
		}catch (InvocationTargetException e) {
			if(errorHandler != null) {
				if(e.getCause() != null) {
					Throwable thr = errorHandler.handle(e.getCause());
					throw thr;
				}else {
					Throwable thr = errorHandler.handle(e);
					throw thr; 
				}
			}else {
				throw e;
			}
		}catch (Throwable e) {
			if(errorHandler != null) {
				Throwable thr = errorHandler.handle(e);
				throw thr;
			}else {
				throw e;
			}
		}
	}

}
