package cell.example.notice;

import cell.ServiceCellIntf;
import cell.concurrent.IPromise;

public interface INoticeAsyncService extends ServiceCellIntf {

    IPromise<String> sendNotice(String title, String receiver);
}

package cell.example.notice.impl;

import bap.cells.BasicServiceCell;
import cell.concurrent.CPromise;
import cell.concurrent.IPromise;
import cell.example.notice.INoticeAsyncService;

public class CNoticeAsyncService extends BasicServiceCell implements INoticeAsyncService {

    @Override
    protected void doStartService() throws Exception {
    }

    @Override
    protected void doStopService() {
    }

    @Override
    public IPromise<String> sendNotice(String title, String receiver) {
        CPromise<String> promise = new CPromise<String>().setProtectTimeout(1000L);
        new Thread(() -> {
            try {
                promise.setResult("已向 " + receiver + " 发送通知: " + title);
            } catch (Exception ex) {
                promise.setError(ex);
            }
        }).start();
        return promise;
    }
}

package test;

import bap.cells.Cells;
import bap.tester.BapTester;
import cell.concurrent.IPromise;
import cell.example.notice.INoticeAsyncService;
import org.junit.Test;

public class NoticeAsyncServiceTest extends BapTester {

    @Test
    public void testAsyncService() throws Exception {
        INoticeAsyncService service = Cells.get(INoticeAsyncService.class);
        IPromise<String> promise = service.sendNotice("审批提醒", "zhangsan");
        promise.thenAccept(result -> System.out.println(result));
        Thread.sleep(1200L);
    }
}
