package cell.gpf.dc.http;

import bap.cells.BasicServiceCell;
import cmn.dto.Progress;

import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.client.util.CmnEvent.EThreadPool;
import gpf.dc.http.ProgressMessage;
import gpf.dc.http.ProgressTypeEnum;
import gpf.dc.http.SSETaskRunnable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.List;

public class CSSETaskPool extends BasicServiceCell implements ISSETaskPool {
    EThreadPool pool = new EThreadPool("SSE Task Pool",0,50,false);
    @Override
    protected void doStartService() throws Exception {

    }

    @Override
    protected void doStopService() {
        pool.shutdown();
    }
    
    @Override
    public void run(SSETaskRunnable runnable) throws Exception {
        pool.run(runnable);
    }

    @Override
    public void terminate(String uuid) throws Exception {
        SSETaskRunnable runnable = (SSETaskRunnable) pool.getRunnable(uuid);
        if(runnable != null){
            long forceKillTime = 30 * 1000;
            ToolUtilities.killThread(runnable.getCurrentThread(),forceKillTime);
            runnable.getProgress().setCanceled(true);
        }
    }

    @Override
    public Flux<ProgressMessage> getProgress(String uuid) throws Exception {
        SSETaskRunnable runnable = (SSETaskRunnable) pool.getRunnable(uuid);
        if(runnable == null){
            //检查缓存中是否存在该任务
            throw new Exception("任务["+uuid+"]不存在");
        }
        SSEProgress prog = runnable.getProgress();
        Flux<ProgressMessage> progressFlux = Flux.create(sink -> {
            // 确保在请求被订阅时，阻塞的 API 调用在一个独立的弹性线程中执行
            Schedulers.boundedElastic().schedule(() -> {
                try {
                    executeCall(prog, sink);
                } catch (IOException e) {
                    sink.error(e);
                }
            });
        });
        return progressFlux;
    }

    private void executeCall(SSEProgress prog, FluxSink<ProgressMessage > sink)
            throws IOException {
        // 1. 逐行读取流式响应
        while (!prog.isTerminated()) {
            List<ProgressMessage> msgs = prog.getCacheMessageAndClear();
            if(msgs.size() > 0) {
                for(ProgressMessage msg : msgs) {
                    sink.next(msg);
                }
            }
            ToolUtilities.sleep(100);
        }
        Throwable error = prog.getError();
        if (error != null) {
            List<ProgressMessage> msgs = prog.getCacheMessageAndClear();
            if(msgs.size() > 0) {
                for(ProgressMessage msg : msgs) {
                    sink.next(msg);
                }
            }
            sink.error(error);
            return;
        }
        sink.complete();
    }

}

