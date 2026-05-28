package cmn.http.servlet.mapping;

import java.io.Serializable;
/**
 * 请求映射接口，实现此接口声明可作为Http请求映射处理
 */
public interface RequestMappingIntf extends Serializable{
	/**
	 * 获取请求映射上下文
	 * @return
	 */
	public RequestMappingContext getContext();
	/**
	 * 设置请求映射上下文
	 * @param context
	 */
	public void setContext(RequestMappingContext context);
}
