package cmn.util;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Stack;

import org.mvel2.TraceListener;

import com.leavay.common.util.MppContext;
import com.leavay.common.util.ToolUtilities;
import com.leavay.dfc.gui.LvUtil;

import cell.cmn.log.IPrintService;
import cmn.enums.TraceLevel;
import cmn.i18n.AbsI18n;
import cmn.i18n.I18nIntf;

public class Tracer implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2101327500120326530L;
	
	public TraceLevel level = TraceLevel.formValue(MppContext.getString("Tracer.Level"));
	TraceListener traceLsnr = null;
	public boolean showTimestamp = false;
	private Stack<Long> start = new Stack<>();
	private String topic = "";
	private I18nIntf i18n = new AbsI18n() {
		@Override
		public String getResourceFileName() {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	public Tracer() {
	}
	public Tracer(TraceListener traceLsnr) {
		this.traceLsnr = traceLsnr;
	}
	public Tracer(TraceListener traceLsnr,I18nIntf i18n) {
		this.traceLsnr = traceLsnr;
		this.i18n = i18n;
	}
	public Tracer(TraceListener traceLsnr,boolean showTimestamp) {
		this.traceLsnr = traceLsnr;
		this.showTimestamp = showTimestamp;
	}
	public String getTopic() {
		return topic;
	}
	public Tracer setTopic(String topic) {
		this.topic = topic;
		return this;
	}
	public TraceLevel getLevel() {
		return level;
	}
	public Tracer setLevel(TraceLevel level) {
		this.level = level;
		return this;
	}
	public boolean isDebug() {
		return this.level == TraceLevel.DEBUG;
	}
	
	public boolean isShowTimestamp() {
		return showTimestamp;
	}
	public Tracer setShowTimestamp(boolean showTimestamp) {
		this.showTimestamp = showTimestamp;
		return this;
	}
	
	public void debug(Object msg) {
		debug(topic, msg);
	}
	
	public void debug(String topic,Object msg) {
		if(level.getLevel() <= TraceLevel.DEBUG.getLevel())
			_trace(topic,TraceLevel.DEBUG,msg,showTimestamp);
	}
	
	public void debug(String topic,String msg,Object... params) {
		if(level.getLevel() <= TraceLevel.DEBUG.getLevel()) {
			if(i18n != null) {
				msg = i18n.format(msg, params);
			}
			_trace(topic,TraceLevel.DEBUG,msg,showTimestamp);
		}
	}
	
	public void debugToJson(String topic,Object msg) {
		if(level.getLevel() <= TraceLevel.DEBUG.getLevel())
			_trace(topic,TraceLevel.DEBUG,msg == null ? msg : JsonUtil.toJson(msg),showTimestamp);
	}

	public void info(Object msg) {
		info("", msg);
	}
	
	public void info(String topic,Object msg) {
		if(level.getLevel() <= TraceLevel.INFO.getLevel())
			_trace(topic,TraceLevel.INFO,msg,showTimestamp);
	}
	
	public void print(Object msg) {
		print(topic, msg);
	}
	
	public void print(String topic,Object msg) {
		_trace(topic,null,msg,showTimestamp);
	}
	
	public void warning(Object msg) {
		warning(topic, msg);
	}
	
	public void warning(String topic,Object msg) {
		if(level.getLevel() <= TraceLevel.WARN.getLevel())
			_trace(topic,TraceLevel.WARN,msg,showTimestamp);
	}
	
	public void warning(String topic, Exception exp) {
		String msg = ToolUtilities.getFullExceptionStack(exp, true);
		if(level.getLevel() <= TraceLevel.WARN.getLevel())
			_trace(topic,TraceLevel.WARN,msg,showTimestamp);
	}
	
	public void error(Object msg) {
		error(topic, msg);
	}
	
	public void error(String topic, Object msg) {
		if(level.getLevel() <= TraceLevel.ERROR.getLevel())
			_trace(topic,TraceLevel.ERROR,msg,showTimestamp);
	}
	
	public void error(String topic, Exception exp) {
		String msg = ToolUtilities.getFullExceptionStack(exp, true);
		if(level.getLevel() <= TraceLevel.ERROR.getLevel())
			_trace(topic,TraceLevel.ERROR,msg,showTimestamp);
	}
	
	public void fatal(Object msg) {
		fatal(topic, msg);
	}
	
	public void fatal(String topic, Object msg) {
		if(level.getLevel() <= TraceLevel.FATAL.getLevel())
			_trace(topic,TraceLevel.FATAL,msg,showTimestamp);
	}
	
	public void fatal(String topic, Exception exp) {
		String msg = ToolUtilities.getFullExceptionStack(exp, true);
		if(level.getLevel() <= TraceLevel.FATAL.getLevel())
			_trace(topic,TraceLevel.FATAL,msg,showTimestamp);
	}
	
	public void logStart() {
		start.push(System.currentTimeMillis());
	}
	
	public Long getCost(boolean logStart) {
		Long time = start.pop();
		long end = System.currentTimeMillis();
		if(time == null)
			return -1L;
		if(logStart) {
			start.push(System.currentTimeMillis());
		}
		Long cost = end - time;
		return cost;
	}
	
	public void printCost(boolean logNextStartTime) {
		printCost(topic,logNextStartTime);
	}
	
	public void printCost(String msg,boolean logNextStartTime) {
		printCost(topic, msg, logNextStartTime);
	}
	
	public void printCost(String topic,String msg,boolean logNextStartTime) {
		Long cost = getCost(logNextStartTime);
		if(cost != -1) {
			_trace(topic,null, msg + "<"+DateUtil.formatMs(cost)+">", showTimestamp);
		}
	}
	
	public void debugCost(String topic,String msg) {
		Long cost = getCost(false);
		if(cost != -1) {
			debug(topic,msg + "<"+DateUtil.formatMs(cost)+">");
		}
	}
	
	public void infoCost(String topic,String msg) {
		Long cost = getCost(false);
		if(cost != -1) {
			info(topic,msg + "<"+DateUtil.formatMs(cost)+">");
		}
	}
	
	private void _trace(String topic,TraceLevel level,Object msg,boolean showTimestamp) {
		Thread thread = Thread.currentThread();
		StringBuffer sb = new StringBuffer();
		if(traceLsnr == null)
			sb.append(String.format("[%s]", thread.getName()));
		if(topic != null && !topic.isEmpty()) {
			sb.append(String.format("[%s]", topic));
		}
		if(showTimestamp) {
			sb.append(String.format("[%s]", ToolUtilities.getCurrentTime(true)));
		}
		if(level != null) {
			sb.append(String.format("[%s]", level.name()));
		}
		sb.append(msg == null ? "" : msg.toString());
		if(traceLsnr != null){
			try {
				traceLsnr.trace(sb.toString());
			} catch (RemoteException e) {
				System.out.println(sb.toString());
			}
		}else {
			try {
				IPrintService.get().asyncPrint(sb.toString());
			} catch (Exception e) {
				System.out.println(sb.toString());
			}
		}
	}
	
	public static void main(String[] args) {
		Tracer util = TraceUtil.getCurrentTracer();
		util.setLevel(TraceLevel.DEBUG);
		util.logStart();
		util.debug("", "{1}不能为空", 111);
		util.printCost(true);
		util.debug("BB");
		util.info("CC");
		util.warning("DD");
		util.error("EE");
		util.fatal("FF");
		util.printCost(true);
		util.print("GGG");
		util.printCost("执行耗时",false);
		ToolUtilities.sleep(5000);
		util.print("GGAAASD");
	}
}
