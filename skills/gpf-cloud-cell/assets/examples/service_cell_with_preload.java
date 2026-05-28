package cell.example.health;

import cell.CellPreloadIntf;
import cell.ServiceCellIntf;

public interface IHealthCheckService extends ServiceCellIntf, CellPreloadIntf {

    boolean isRunning() throws Exception;
}

package cell.example.health.impl;

import bap.cells.BasicServiceCell;
import cell.example.health.IHealthCheckService;

import java.util.concurrent.atomic.AtomicBoolean;

public class CHealthCheckService extends BasicServiceCell implements IHealthCheckService {

    private AtomicBoolean running;

    @Override
    protected void doStartService() throws Exception {
        running = new AtomicBoolean(true);
    }

    @Override
    protected void doStopService() {
        if (running != null) {
            running.set(false);
            running = null;
        }
    }

    @Override
    public boolean isRunning() throws Exception {
        return running != null && running.get();
    }
}

package test;

import bap.cells.Cells;
import bap.tester.BapTester;
import cell.example.health.IHealthCheckService;
import org.junit.Test;

public class HealthCheckServiceTest extends BapTester {

    @Test
    public void testPreloadService() throws Exception {
        IHealthCheckService service = Cells.get(IHealthCheckService.class);
        assert service.isRunning();
    }
}
