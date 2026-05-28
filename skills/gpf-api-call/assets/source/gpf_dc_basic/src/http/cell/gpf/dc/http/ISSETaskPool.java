package cell.gpf.dc.http;

import bap.cells.Cells;
import cell.ServiceCellIntf;
import cmn.dto.Progress;
import gpf.dc.http.ProgressMessage;
import gpf.dc.http.SSETaskRunnable;
import reactor.core.publisher.Flux;

public interface ISSETaskPool extends ServiceCellIntf {
    static ISSETaskPool get(){
        return Cells.get(ISSETaskPool.class);
    }

    /**
     * 运行事件流任务
     * @param runnable
     * @throws Exception
     */
    public void run(SSETaskRunnable runnable)throws Exception;
    /**
     * 终止事件流任务
     * @param uuid
     * @throws Exception
     */
    public void terminate(String uuid)throws Exception;

    /**
     * 获取事件流任务进度
     * @param uuid
     * @return
     * @throws Exception
     */
    public Flux<ProgressMessage> getProgress(String uuid)throws Exception;
}
