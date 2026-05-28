package gpf.dc.http;

import cell.gpf.dc.http.SSEProgress;
import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.client.util.CmnEvent.CmnRunnable;

public class SSETaskRunnable extends CmnRunnable {
    Thread currentThread;
    SSEProgress progress;
    Object instance;
    String method;
    Object[] params;
    public SSETaskRunnable(SSEProgress progress, Object instance, String method, Object[] params) {
        super(ToolUtilities.allocUUIDWithUnderline());
        this.progress = progress;
        this.instance = instance;
        this.method = method;
        this.params = params;
    }

    public SSEProgress getProgress() {
        return progress;
    }

    public SSETaskRunnable setProgress(SSEProgress progress) {
        this.progress = progress;
        return this;
    }

    public Object getInstance() {
        return instance;
    }

    public SSETaskRunnable setInstance(Object instance) {
        this.instance = instance;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public SSETaskRunnable setMethod(String method) {
        this.method = method;
        return this;
    }

    @Override
    public Object[] getParams() {
        return params;
    }

    public SSETaskRunnable setParams(Object[] params) {
        this.params = params;
        return this;
    }

    public Thread getCurrentThread() {
        return currentThread;
    }

    @Override
    public void run() {
        try {
            currentThread = Thread.currentThread();
            ToolUtilities.callFunction(instance, method, params);
        }catch (Exception e) {
            progress.finishError(e);
        }
    }
}
