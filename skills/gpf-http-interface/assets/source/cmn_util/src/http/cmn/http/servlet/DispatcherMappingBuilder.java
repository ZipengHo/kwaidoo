package cmn.http.servlet;

import java.io.Serializable;

/**
 * Url处理映射构造接口，实现此接口方法，平台将通过扫描实现此方法的类将映射配置构建
 */
public interface DispatcherMappingBuilder extends Serializable{
	/**
	 * 匹配路径
	 * @return
	 */
	String[] getIncludePatterns();
	/**
	 * 排除路径
	 * @return
	 */
	String[] getExcludePatterns();
	/**
	 * 分发处理配置
	 * @return
	 */
	HandlerMapping getHandlerMapping();
	/**
	 * 异常处理器
	 * @return
	 */
	default HandlerExceptionResolver getExceptionResolver() {
		return null;
	}
	
}
