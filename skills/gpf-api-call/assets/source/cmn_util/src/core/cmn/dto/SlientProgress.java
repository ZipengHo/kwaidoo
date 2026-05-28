package cmn.dto;

import java.rmi.RemoteException;

import javax.swing.JOptionPane;

import com.leavay.common.util.ProgressCtrl.ProgressControllerFEIntf;
import com.leavay.common.util.ProgressCtrl.crpc.IProgress;

import crpc.BasicCallback;

public class SlientProgress extends BasicCallback implements IProgress<Object>
{
    private static final long serialVersionUID = 2526237825932026594L;
    ProgressControllerFEIntf _totalProg;
    public SlientProgress(ProgressControllerFEIntf totalProg)
    {
    	if (totalProg instanceof BasicCallback)
        {
            __callbackTimout = ((BasicCallback) totalProg).getTimeout();
            __expireTime = ((BasicCallback) totalProg).getExpireTime();
        }
    }

    public int getMaximum() throws RemoteException
    {
    	if (_totalProg == null)
            return 0;

        return _totalProg.getMaximum();
    }

    public int getMinimum() throws RemoteException
    {
    	if (_totalProg == null)
            return 0;

        return _totalProg.getMinimum();
    }

    public boolean isCanceled() throws RemoteException
    {
    	if (_totalProg == null)
            return false;

        return _totalProg.isCanceled();
    }

    public boolean isTerminated() throws RemoteException
    {
    	if (_totalProg == null)
            return false;

        return _totalProg.isTerminated();
    }

    public void reset() throws RemoteException
    {
    	if (_totalProg == null)
            return;

        _totalProg.reset();
    }

    public void sendProcess(int process, String msg, boolean blNewLine) throws RemoteException
    {
        sendProcess(process, msg, blNewLine, null);
    }

    public void sendProcess(int process, String msg, boolean blNewLine, Object userObject) throws RemoteException
    {
    	if (_totalProg == null)
            return;
    	_totalProg.sendProcess(process, null, blNewLine, userObject);
    }

    public void sendStopProcess() throws RemoteException
    {
    	if (_totalProg == null)
            return;
    	_totalProg.sendStopProcess();;
    }
    
    public void setMessage(String sMsg, boolean blNewLine)
    {
    	if(sMsg == null)
    		return;
    	if(blNewLine)
    		println(sMsg);
    	else
    		print(sMsg);
    }

    public void setMaximum(int value)
    {
        
    }

    public void setMinimum(int value)
    {
        
    }

    public int showConfirmDialog(String msg, String title, int operation)
    {
        println(msg);
        return JOptionPane.YES_OPTION;
    }

    public int showConfirmDialog(String msg, String title, int optionType, int messageType)
    {
        println(msg);
        return JOptionPane.YES_OPTION;
    }

    public void showMessageDialog(String msg, String title, int messageType)
    {
        println(msg);
    }

    public void showMessageDialog(String msg, String title) throws Exception
    {
        println(msg);
    }

    public void println(String msg)
    {
        print(msg+"\n");
    }
    
    public void print(String msg)
    {
        System.out.print(msg);
    }

    public void sendDataFrame(Object data)
    {
        println("Recieve Data : "+data);
    }
}
