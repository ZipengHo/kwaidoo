package cmn.util;

import java.rmi.RemoteException;

import cmn.dto.Progress;
import cmn.enums.ProgressConfirmOperation;
import cmn.enums.ProgressMessageType;

public class ProgressUtil {
	public static void reset(Progress prog) throws RemoteException {
		if(prog != null)
			prog.reset();
	}
	public static void sendProcess(Progress prog,int iProcess, String sMsg, boolean blNewLine) throws RemoteException {
		if(prog != null)
			prog.sendProcess(iProcess, sMsg, blNewLine);
	}
	public static void sendProcess(Progress prog,int iProcess, String sMsg, boolean blNewLine, Object userObject) throws RemoteException {
		if(prog != null)
			prog.sendProcess(iProcess, sMsg, blNewLine, userObject);
	}
	public static void setMessage(Progress prog,String sMsg, boolean blNewLine) throws RemoteException {
		if(prog != null)
			prog.setMessage(sMsg, blNewLine);
	}
	public static void sendStopProcess(Progress prog) throws RemoteException {
		if(prog != null)
			prog.sendStopProcess();
	}
    public static void assertCancel(Progress prog)
    {
    	if(prog != null)
    		prog.assertCancel();
    }
	public static boolean isCanceled(Progress prog) throws RemoteException {
		if(prog == null) {
			return false;
		}
		return prog.isCanceled();
	}
	public static boolean isTerminated(Progress prog) throws RemoteException {
		if(prog == null) {
			return false;
		}
		return prog.isTerminated();
	}
	public static void sendDataFrame(Progress prog,Object data) {
		if(prog != null)
			prog.sendDataFrame(data);
	}
	public static void finish(Progress prog) {
		if(prog != null)
			prog.finish();
	}
	public static int showConfirmDialog(Progress prog,String sMsg, String sTitle, ProgressConfirmOperation operation) throws RemoteException {
		if(prog == null)
			return ProgressConfirmOperation.YES.getValue();
		return prog.showConfirmDialog(sMsg, sTitle, operation);
	}
	
	public static int showConfirmDialog(Progress prog,String sMsg, String sTitle, ProgressConfirmOperation optionType, ProgressMessageType messageType) throws RemoteException {
		if(prog == null)
			return ProgressConfirmOperation.YES.getValue();
		return prog.showConfirmDialog(sMsg, sTitle, optionType, messageType);
	}
    public static void showMessageDialog(Progress prog,String sMsg, String sTitle, ProgressMessageType messageType) throws RemoteException {
    	if(prog != null)
			prog.showMessageDialog(sMsg, sTitle, messageType);
    }

    public void showMessageDialog(Progress prog,String sMsg, String sTitle) throws Exception {
    	if(prog != null)
			prog.showMessageDialog(sMsg, sTitle);
    }
	
	public Progress newChildProgress(Progress prog,int iStartPercent,int iEndPercent) {
		if(prog != null)
			prog.newChildProgress(iStartPercent, iEndPercent);
		return null;
	}
	
	public Progress newChildProgress(Progress prog,int start,int end,int totalSize) {
		if(prog != null)
			prog.newChildProgress(start, end, totalSize);
		return null;
	}
}
