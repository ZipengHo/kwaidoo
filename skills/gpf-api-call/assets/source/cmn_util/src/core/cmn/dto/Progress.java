package cmn.dto;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kwaidoo.ms.tool.CmnUtil;
import com.leavay.common.util.ProgressCtrl.ChildProgress;
import com.leavay.common.util.ProgressCtrl.crpc.COutputProgress;
import com.leavay.common.util.ProgressCtrl.crpc.IProgress;
import com.leavay.common.util.ProgressCtrl.crpc.IProgressUtil;

import cmn.enums.ProgressConfirmOperation;
import cmn.enums.ProgressMessageType;
import cmn.enums.TraceLevel;
import cmn.util.CTracerProgress;
import cmn.util.Tracer;
import crpc.CRpcContainerIntf;
import web.dto.Pair;
/**
 * 进度通知对象
 * @author chenxb
 *
 * @param <T>
 */
public class Progress<T> implements CRpcContainerIntf,Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6439306363332959198L;
	public static <T> Progress<T> wrap(IProgress<T> prog) {
		return new Progress<T>(prog);
	}
	
	public static <T> Progress<T> newOutput() {
		return new Progress<T>((IProgress<T>) new COutputProgress());
	}
	
	public static <T> Progress<T> newTracer() {
		return new Progress<T>((IProgress<T>) new CTracerProgress());
	}
	
	public static <T> Progress<T> newTracer(Tracer tracer ) {
		return new Progress<T>((IProgress<T>) new CTracerProgress(tracer));
	}
	
	IProgress<T> prog;
	/**
	 * 消息通知级别，指定输出消息的堆栈类的消息级别
	 */
	private Map<String,TraceLevel> messageLevels = new LinkedHashMap<>();
	/**
	 * 默认的消息输出级别，只有大于等于此级别的日志才能够输出
	 */
	private TraceLevel defaultLevel = TraceLevel.INFO;
	boolean printClassName = false;
	boolean printTraceLevel = true;
	public Progress(IProgress<T> prog) {
		this.prog = prog;
	}
	public IProgress<T> getProg() {
		return prog;
	}
	public Map<String, TraceLevel> getMessageLevels() {
		return messageLevels;
	}
	public Progress<T> setMessageLevels(Map<String, TraceLevel> messageLevels) {
		this.messageLevels = messageLevels;
		return this;
	}
	public Progress<T> addMessageLevels(Class clazz,TraceLevel messageLevel) {
		this.messageLevels.put(clazz.getName(), messageLevel);
		return this;
	}
	public TraceLevel getDefaultLevel() {
		return defaultLevel;
	}
	public Progress<T> setDefaultLevel(TraceLevel defaultLevel) {
		this.defaultLevel = defaultLevel;
		return this;
	}
	public boolean isPrintClassName() {
		return printClassName;
	}
	public Progress<T> setPrintClassName(boolean printClassName) {
		this.printClassName = printClassName;
		return this;
	}
	public void reset() throws RemoteException {
		IProgressUtil.resetProcess(prog);
	}
	public void sendProcess(int iProcess, String sMsg, boolean blNewLine) throws RemoteException {
		Pair<String,TraceLevel> pair = getCurrMessageLevel();
		if(pair != null) {
			if(printClassName && pair.left != null) {
				sMsg = "["+pair.left + "]" + sMsg;
			}
			IProgressUtil.sendProcess(prog, iProcess, sMsg, blNewLine);
		}else {
			IProgressUtil.sendProcess(prog, iProcess, "", false);
		}
	}
	public void sendProcess(int iProcess, String sMsg, boolean blNewLine, Object userObject) throws RemoteException {
		Pair<String,TraceLevel> pair = getCurrMessageLevel();
		if(pair != null) {
			if(printClassName && pair.left != null) {
				sMsg = "["+pair.left + "]" + sMsg;
			}
			IProgressUtil.sendProcess(prog, iProcess, sMsg, blNewLine, userObject);
		}else {
			IProgressUtil.sendProcess(prog, iProcess, "", false,userObject);
		}
	}
	public void setMessage(String sMsg, boolean blNewLine) throws RemoteException {
		Pair<String,TraceLevel> pair = getCurrMessageLevel();
		if(pair != null ) {
			if(printClassName && pair.left != null) {
				sMsg = "["+pair.left + "]" + sMsg;
			}
			IProgressUtil.setMessage(prog, sMsg, blNewLine);
		}
	}
	
	public void debug(String sMsg,boolean blNewLine) {
		Pair<String,TraceLevel> pair = getCurrMessageLevel();
		if(pair != null ) {
			if(printClassName && pair.left != null) {
				sMsg = "["+pair.left + "]" + sMsg;
			}
			if(pair.getValue().getLevel() <= TraceLevel.DEBUG.getLevel())
				IProgressUtil.setMessage(prog, printTraceLevel ? "[DEBUG]"+sMsg : sMsg, blNewLine);
		}
	}
	public void info(String sMsg,boolean blNewLine) {
		Pair<String,TraceLevel> pair = getCurrMessageLevel();
		if(pair != null ) {
			if(printClassName && pair.left != null) {
				sMsg = "["+pair.left + "]" + sMsg;
			}
			if(pair.getValue().getLevel() <= TraceLevel.INFO.getLevel())
				IProgressUtil.setMessage(prog, printTraceLevel ? "[INFO]"+sMsg : sMsg, blNewLine);
		}
	}
	public void warn(String sMsg,boolean blNewLine) {
		Pair<String,TraceLevel> pair = getCurrMessageLevel();
		if(pair != null ) {
			if(printClassName && pair.left != null) {
				sMsg = "["+pair.left + "]" + sMsg;
			}
			if(pair.getValue().getLevel() <= TraceLevel.WARN.getLevel())
				IProgressUtil.setMessage(prog, printTraceLevel ? "[WARN]"+sMsg : sMsg, blNewLine);
		}
	}
	public void error(String sMsg,boolean blNewLine) {
		Pair<String,TraceLevel> pair = getCurrMessageLevel();
		if(pair != null ) {
			if(printClassName && pair.left != null) {
				sMsg = "["+pair.left + "]" + sMsg;
			}
			if(pair.getValue().getLevel() <= TraceLevel.ERROR.getLevel())
				IProgressUtil.setMessage(prog, printTraceLevel ? "[ERROR]"+sMsg : sMsg, blNewLine);
		}
	}
	/**
	 * 获取当前的消息通知级别
	 * @return
	 */
	public Pair<String,TraceLevel> getCurrMessageLevel() {
//		System.out.println(ToolUtilities.getCurrentStack());
		TraceLevel traceLevel = null;
		String sClassName = null;
		int layer = 2;
        StackTraceElement[] ele = Thread.currentThread().getStackTrace();
        for(;layer < ele.length;layer++){
            if(!CmnUtil.isStringEqual(ele[layer].getClassName(), getClass().getName())) {
            	sClassName = ele[layer].getClassName();
	            traceLevel = messageLevels.get(sClassName);
	            break;
            }
        }
        if(sClassName == null) {
        	if(traceLevel == null && defaultLevel == null) {
        		return null;
        	}else {
        		return new Pair<>(null,defaultLevel);
        	}
        }else {
        	if(traceLevel == null) {
        		if(defaultLevel == null) {
            		return null;
            	}else {
            		return new Pair<>(null,defaultLevel);
            	}
        	}else {
        		return new Pair<>(sClassName,traceLevel);
        	}
        }
	}
	
	public static void main(String[] args) throws Exception {
		Progress prog = Progress.newOutput().setPrintClassName(true).setDefaultLevel(null);
		prog.setMessage("ssss", true);
		prog.debug("AAA", true);
		prog.info("BBB", true);
		prog.warn("CCC", true);
		prog.error("DDD", true);
		System.out.println();
		System.out.println(prog.getCurrMessageLevel());
	}
	
	public void sendStopProcess() throws RemoteException {
		IProgressUtil.sendStopProcess(prog);
	}
    public void assertCancel()
    {
    	IProgressUtil.assertCancel(prog);
    }
	public boolean isCanceled() throws RemoteException {
		return IProgressUtil.isCancel(prog);
	}
	public boolean isTerminated() throws RemoteException {
		return IProgressUtil.isTerminated(prog);
	}
	public void sendDataFrame(T data) {
		IProgressUtil.sendDataFrame(prog, data);
	}
	public void finish() {
		IProgressUtil.finish(prog);
	}
	public int showConfirmDialog(String sMsg, String sTitle, ProgressConfirmOperation operation) throws RemoteException {
		return IProgressUtil.showConfirmDialog(prog, sMsg, sTitle, operation.getValue());
	}
	
	public int showConfirmDialog(String sMsg, String sTitle, ProgressConfirmOperation optionType, ProgressMessageType messageType) throws RemoteException {
		return IProgressUtil.showConfirmDialog(prog, sMsg, sTitle, optionType.getValue(), messageType.getValue());
	}
    public void showMessageDialog(String sMsg, String sTitle, ProgressMessageType messageType) throws RemoteException {
    	IProgressUtil.showMessageDialog(prog, sMsg, sTitle,messageType.getValue());
    }

    public void showMessageDialog(String sMsg, String sTitle) throws Exception {
    	IProgressUtil.showMessageDialog(prog, sMsg, sTitle);
    }
	
	public Progress newChildProgress(int iStartPercent,int iEndPercent) {
		ChildProgress childProg = new ChildProgress(iStartPercent, iEndPercent, prog);
		return Progress.wrap(childProg)
				.setMessageLevels(messageLevels)
				.setDefaultLevel(defaultLevel)
				.setPrintClassName(printClassName);
	}
	
	public Progress newChildProgress(int start,int end,int totalSize) {
		ChildProgress childProg = new ChildProgress(start, end, totalSize, prog);
		return Progress.wrap(childProg)
				.setMessageLevels(messageLevels)
				.setDefaultLevel(defaultLevel)
				.setPrintClassName(printClassName);
	}
	/**
	 * 静默通知的进度条，只输出完成进度，不输出消息
	 * @return
	 */
	public Progress newSlientProgresss() {
		SlientProgress slientProgress = new SlientProgress(prog);
		return Progress.wrap(slientProgress)
				.setMessageLevels(messageLevels)
				.setDefaultLevel(defaultLevel)
				.setPrintClassName(printClassName);
	}
}
