package gpf.dc.basic.util.shell;

import com.leavay.common.util.ShellLogger;

import cmn.util.TraceUtil;
import cmn.util.Tracer;

/**
 * 
 * @author chenxb 2018年4月13日
 */
public class ShellErrorLogger implements ShellLogger {
	int maxKeepLine;
	int limitLineLens = -1;
	private LimitQueue<String> queue = null;
	
	public ShellErrorLogger() {
		this(500);
	}
	
	public ShellErrorLogger(int maxKeepLine) {
		this.maxKeepLine = maxKeepLine;
		queue = new LimitQueue<>(maxKeepLine);
	}
	
	public int getLimitLineLens() {
		return limitLineLens;
	}

	public void setLimitLineLens(int limitLineLens) {
		this.limitLineLens = limitLineLens;
	}

	public void print(String msg) {
		Tracer tracer = TraceUtil.getCurrentTracer();
		tracer.error(msg);
		if(limitLineLens == -1)
			queue.offer(msg);
		else{
			if(msg.length() < limitLineLens)
				queue.offer(msg);
			else
				queue.offer(msg.substring(0, limitLineLens)+"...");
		}
	}

	public boolean isError() {
		return !queue.isEmpty();
	}

	public String getErrorMsg() {
		StringBuffer sb = new StringBuffer();
		for (String msg : queue.getQueue()) {
			if (sb.length() > 0)
				sb.append("\n");
			sb.append(msg);
		}
		return sb.toString();
	}
	
	public void clear(){
		queue = new LimitQueue<>(maxKeepLine);
	}
}
