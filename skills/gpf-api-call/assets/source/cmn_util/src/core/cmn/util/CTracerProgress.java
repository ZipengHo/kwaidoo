package cmn.util;

import javax.swing.JOptionPane;

import com.leavay.common.util.ProgressCtrl.crpc.IProgress;

import crpc.BasicCallback;

public class CTracerProgress extends BasicCallback implements IProgress<Object>
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 4886637798925036587L;
	
	Tracer tracer;

	public CTracerProgress()
    {
		tracer = TraceUtil.getCurrentTracer();
    }
	
	public CTracerProgress(Tracer tracer)
    {
		this.tracer = tracer;
    }

    public int getMaximum()
    {
        return 100;
    }

    public int getMinimum()
    {
        return 0;
    }

    public boolean isCanceled()
    {
        return false;
    }

    public boolean isTerminated()
    {
        return false;
    }

    public void reset()
    {
        
    }

    public void sendProcess(int process, String msg, boolean blNewLine)
    {
        sendProcess(process, msg, blNewLine, null);
    }

    public void sendProcess(int process, String msg, boolean blNewLine, Object userObject)
    {
        if (blNewLine)
            print("\n("+process+"%) " + msg);
        else
            print(msg);        
    }

    public void sendStopProcess()
    {
        println("Stop Progress");        
    }
    
    public void setMessage(String sMsg, boolean blNewLine)
    {
        if (blNewLine)
            print("\n " + sMsg);
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
    	tracer.print(msg);
//        System.out.print(msg);
    }

    public void sendDataFrame(Object data)
    {
        println("Recieve Data : "+data);
    }
}