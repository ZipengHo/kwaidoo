package cmn.enums;

import java.util.HashMap;
import java.util.Map;

import com.kwaidoo.ms.tool.CmnUtil;

/**
 *  TRACE：用于追踪程序的详细执行情况，通常用于诊断问题。
 *	DEBUG：用于调试目的的详细信息，用于开发过程中查看程序运行状态。
 *	INFO：提供一般信息，用于描述程序运行时的重要事件。
 *	WARN：警告信息，表明可能发生了潜在问题，但并不一定是错误。
 *	ERROR：错误信息，表示在程序执行过程中发生了错误，但程序仍然可以继续运行。
 *	FATAL：严重错误信息，表示程序遇到了无法继续运行的严重问题。
 * @param level
 */
public enum TraceLevel {
	
	DEBUG(1),INFO(2),WARN(3),ERROR(4),FATAL(5);
	
	private static Map<String,TraceLevel> map = new HashMap<String,TraceLevel>(){
		{
			put(DEBUG.name(), DEBUG);
			put(INFO.name(), INFO);
			put(WARN.name(), WARN);
			put(ERROR.name(), ERROR);
			put(FATAL.name(), FATAL);
		}
	};
	
	int level;
	private TraceLevel(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return level;
	}
	
	public static TraceLevel formValue(String value) {
		if(CmnUtil.isStringEmpty(value))
			return INFO;
		return map.getOrDefault(value,INFO);
	}
}
