package cmn.util;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.mvel2.TraceListener;

import com.leavay.common.util.MppContext;
import com.leavay.common.util.javac.ClassFactory;
import com.leavay.common.util.javac.ClassNameInfo;
import com.leavay.dfc.gui.LvUtil;

import cmn.anotation.ClassDeclare;
import cmn.anotation.MethodDeclare;
import cmn.enums.TraceLevel;
@ClassDeclare(label = "打印输出工具类",what = "提供打印输出的各种标签前缀，并可调整日志级别控制打印输出",why = "",how = "",developer = "陈晓斌",version = "1.0",createTime = "2024-11-27",updateTime = "2024-11-27")
public class TraceUtil implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8087418978077362967L;
	
	public static TraceLevel level = TraceLevel.formValue(MppContext.getString("Tracer.Level"));
	
	private static Map<String,TraceLevel> levelMap = new ConcurrentHashMap<>();
	 
	private static Tracer tracer = new Tracer();
	@MethodDeclare(label = "获取当前线程下的打印输出监听器",what = "",why = "",how = "",developer = "陈晓斌",version = "1.0",createTime = "2024-11-27",updateTime = "2024-11-27",inputs = {})
	public static Tracer getCurrentTracer() {
		return getTracer(LvUtil.getCurrentTraceListener());
	}
	@MethodDeclare(label = "获取当前线程下的打印输出监听器",what = "",why = "",how = "",developer = "陈晓斌",version = "1.0",createTime = "2024-11-27",updateTime = "2024-11-27",inputs = {})
	public static Tracer getCurrentTracer(String topic) {
		return getTracer(topic,LvUtil.getCurrentTraceListener());
	}
	@MethodDeclare(label = "传入输出监听器构建Tracer",what = "",why = "",how = "",developer = "陈晓斌",version = "1.0",createTime = "2024-11-27",updateTime = "2024-11-27",inputs = {})	
	public static Tracer getTracer(TraceListener traceLsnr) {
		return new Tracer(traceLsnr).setLevel(level);
	}
	
	public static Tracer getTracer(String topic,TraceListener traceLsnr) {
		if(!levelMap.containsKey(topic)) {
			levelMap.put(topic, TraceLevel.INFO);
		}
		TraceLevel level = levelMap.get(topic);
		Tracer tracer = new Tracer(traceLsnr);
		tracer.setTopic(topic).setLevel(level);
		return tracer;
	}
	static Long clientPlugTag = -1L;
	public static Map<String,TraceLevel> getTracerLevelMap() throws Exception{
		long currentPluginTag = ClassFactory.getPluginTag();
		if(clientPlugTag != currentPluginTag) {
			Map<String,TraceLevel> map = new ConcurrentHashMap<>();
//			Set<Class> classes = ClassFactory.searchSubClass(TraceLevelRegister.class);
			Set<Class> classes = ClassUtil.searchSubClass(TraceLevelRegister.class, null);
			for(Class clazz : classes) {
				if(clazz.isInterface()) {
					continue;
				}
				TraceLevelRegister register = (TraceLevelRegister) clazz.newInstance();
				Map<String,TraceLevel> map1 = register.regist();
				if(map1 != null) {
					for(String key : map1.keySet()) {
						if(levelMap.containsKey(key)) {
							map.put(key, levelMap.get(key));
						}else {
							map.put(key, map1.get(key));
						}
					}
				}
			}
			levelMap = map;
			clientPlugTag = currentPluginTag;
		}
		return levelMap;
	}
	
	public static void setTracerLevelMap(ConcurrentHashMap<String,TraceLevel> map) {
		TraceUtil.levelMap = map;
	}
	
	public static TraceLevel getLevel() {
		return level;
	}
	
	public static void setLevel(TraceLevel level) {
		TraceUtil.level = level;
	}

//	public static void setShowTimestamp(boolean showTimestamp) {
//		tracer.setShowTimestamp(showTimestamp);
//	}
	
	public static void debug(Object msg) {
		tracer.debug(msg);
	}
	
	public static void debug(String topic,Object msg) {
		tracer.debug(topic, msg);
	}

	public static void info(Object msg) {
		tracer.info(msg);
	}
	
	public static void info(String topic,Object msg) {
		tracer.info(topic, msg);
	}
	
	public static void print(Object msg) {
		tracer.print(msg);
	}
	
	public static void print(String topic,Object msg) {
		tracer.print(topic, msg);
	}
	
	public static void warning(Object msg) {
		tracer.warning(msg);
	}
	
	public static void warning(String topic,Object msg) {
		tracer.warning(topic, msg);
	}
	
	public static void warning(String topic,Exception exp) {
		tracer.warning(topic, exp);
	}
	
	public static void error(Object msg) {
		tracer.error(msg);
	}
	
	public static void error(String topic, Object msg) {
		tracer.error(topic, msg);
	}
	
	public static void error(String topic, Exception exp) {
		tracer.error(topic, exp);
	}
	
	public static void fatal(Object msg) {
		tracer.fatal(msg);
	}
	
	public static void fatal(String topic, Object msg) {
		tracer.fatal(topic, msg);
	}
	
	public static void fatal(String topic, Exception exp) {
		tracer.fatal(topic, exp);
	}
	
//	public static void logStart() {
//		tracer.logStart();
//	}
//	
//	public static void debugCost(String topic,String msg,boolean logNextStartTime) {
//		tracer.debugCost(topic, msg, logNextStartTime);
//	}
//	
//	public static void printCost() {
//		tracer.printCost(false);
//	}
//	
//	public static void printCost(boolean logNextStart) {
//		tracer.printCost(logNextStart);
//	}
//	
//	public static void printCost(String msg,boolean logNextStart) {
//		tracer.printCost(msg,logNextStart);
//	}
//	
//	public static void printCost(String topic,String msg,boolean logNextStart) {
//		tracer.printCost(topic,msg,logNextStart);
//	}
}
