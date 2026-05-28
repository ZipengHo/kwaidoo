package cell.gpf.dc.http;

import bap.cells.BasicCell;
import cell.ResourceCellIntf;
import com.leavay.common.util.ProgressCtrl.crpc.IProgress;
import com.leavay.common.util.ToolUtilities;
import com.leavay.ms.tool.CmnUtil;
import fe.cmn.progress.ProgressCtrl;
import gpf.dc.basic.fe.enums.EnumUtil;
import gpf.dc.http.ProgressMessage;
import gpf.dc.http.ProgressTypeEnum;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
/**
 * 用于前端SSE进度通知的类
 * @param <T>
 */
public class SSEProgress<T> extends BasicCell implements ResourceCellIntf, IProgress<T> {

	public final static String LOG = SSEProgress.class.getSimpleName();
	/**
	 *
	 */
	private static final long serialVersionUID = 212261066830617495L;

	// 缓存最近发送的进度（绝对值，非百分比）
	List<ProgressMessage> _cacheMsgs = new LinkedList<>();
	String ctrlUuid = ToolUtilities.allockUUIDWithUnderline();
	int _cacheProc = 0;
	boolean isCanceled = false;
	Throwable error;
	String status = ProgressTypeEnum.process.name();

	public SSEProgress() {
		setTimeout(12*60*60*1000L);
	}

	public String getStatus(){
		return status;
	}

	public ProgressTypeEnum getStatusType(){
		return EnumUtil.getEnumByName(ProgressTypeEnum.class, status);
	}

	@Override
	public int getMinimum() {
		return 0;
	}

	@Override
	public void setMinimum(int iValue) {
		//TODO
	}

	@Override
	public int getMaximum() {
		return 100;
	}

	@Override
	public void setMaximum(int iValue) {
		//TODO
	}

	public String getCtrlUuid() {
		return ctrlUuid;
	}

	// 这是传递给前端的控制器，前端利用uuid来后端获取进度信息
	public ProgressCtrl getController() {
		return ProgressCtrl.wrap(getCtrlUuid());
	}

	// 转换为发送个FE服务的进度（0-1之间的小数）
	public double calcProcess(int process) {
		return (double) process / getMaximum();
	}

	/**
	 * 输出进度信息
	 *
	 * @param iProcess
	 * @param sMsg
	 * @param blNewLine
	 */
	private void _doSendProcess(int iProcess, String sMsg, boolean blNewLine) {
		try {
			_cacheProc = iProcess;
			ProgressMessage msg = new ProgressMessage();
			msg.setUuid(getCtrlUuid());
			msg.setType(ProgressTypeEnum.process.name());
			msg.setProgress(iProcess);
			msg.setMessage(sMsg);
			msg.setTimestamp(System.currentTimeMillis());
			_cacheMsgs.add(msg);
		} catch (Throwable err) {
			ToolUtilities.warnAndOutput(LOG, "Failed to send process : " + ToolUtilities.getFullExceptionStack(err));
		}
	}

	@Override
	public void sendProcess(int iProcess, String sMsg, boolean blNewLine) {
		_doSendProcess(iProcess, sMsg, blNewLine);
	}

	@Override
	public void sendProcess(int iProcess, String sMsg, boolean blNewLine, Object userObject) {
		_doSendProcess(iProcess, sMsg, blNewLine); // 暂时不支持发送对象到前端
	}

	@Override
	public void setMessage(String sMsg, boolean blNewLine) {
		_doSendProcess(_cacheProc, sMsg, blNewLine); // 使用缓存的进度
	}

	@Override
	public void sendStopProcess() {
		try {
			status = ProgressTypeEnum.completed.name();
			ProgressMessage msg = new ProgressMessage();
			msg.setUuid(getCtrlUuid());
			msg.setType(ProgressTypeEnum.completed.name());
			msg.setProgress(_cacheProc);
			msg.setMessage("");
			msg.setTimestamp(System.currentTimeMillis());
			return;
		} catch (Throwable err) {
			ToolUtilities.warnAndOutput(LOG, "Failed to send process : " + ToolUtilities.getExceptionStackMessage(err));
		}
	}

	@Override
	public boolean isCanceled() {
		return isCanceled;
	}

	public SSEProgress setCanceled(boolean canceled) {
		status = ProgressTypeEnum.cancelled.name();
		isCanceled = canceled;
		return this;
	}

	@Override
	public boolean isTerminated() {
		return CmnUtil.isStringEqual(status,ProgressTypeEnum.error.name())
				|| CmnUtil.isStringEqual(status,ProgressTypeEnum.cancelled.name())
				|| CmnUtil.isStringEqual(status,ProgressTypeEnum.completed.name());
	}

	/**
	 * 异常中止
	 * FE 进度特有功能，显示出错信息
	 *
	 */
	public void finishError(Throwable e) {
		this.error = e;
		status = ProgressTypeEnum.error.name();
		ProgressMessage msg = new ProgressMessage();
		msg.setUuid(getCtrlUuid());
		msg.setType(ProgressTypeEnum.error.name());
		msg.setProgress(_cacheProc);
		msg.setTimestamp(System.currentTimeMillis());
		msg.setError(ToolUtilities.getFullExceptionStack(e));
		_cacheMsgs.add(msg);
	}

	public Throwable getError() {
		return error;
	}

	@Override
	public void onClose() {
		sendStopProcess();
	}

	@Override
	public void reset() {
		_cacheMsgs = new LinkedList<>();
		_cacheProc = 0;
		status = ProgressTypeEnum.process.name();
	}


	@Override
	public void sendDataFrame(Object data) {
		if(data != null) {
			if(data instanceof Exception) {
				finishError((Throwable)data);
			}else {
				setMessage(""+data, true);
			}
		}
	}

	@Override
	public void showMessageDialog(String sMsg, String sTitle, int iMessageType) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void showMessageDialog(String sMsg, String sTitle) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public int showConfirmDialog(String sMsg, String sTitle, int iOperation) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int showConfirmDialog(String sMsg, String sTitle, int iOptionType, int iMessageType) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<ProgressMessage> getCacheMessageAndClear() {
		List<ProgressMessage> msgs = _cacheMsgs;
		_cacheMsgs = new LinkedList<>();
		return msgs;
	}
}
