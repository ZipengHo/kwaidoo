package cmn.util;

import java.util.Map;

import cmn.enums.TraceLevel;
/**
 * 实现此接口注册分类Tracer的日志级别
 */
public interface TraceLevelRegister {

	/**
	 * 返回分类Tracer的日志级别
	 * @return	key ： 名称 value：日志级别
	 */
	public Map<String,TraceLevel> regist();
}
