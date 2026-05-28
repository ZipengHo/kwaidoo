package cell.example.order;

import cell.ServiceCellIntf;

public interface IOrderCacheService extends ServiceCellIntf {

    String getOrderName(String orderUuid) throws Exception;

    void putOrderName(String orderUuid, String orderName) throws Exception;
}

package cell.example.order.impl;

import bap.cells.BasicServiceCell;
import cell.example.order.IOrderCacheService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class COrderCacheService extends BasicServiceCell implements IOrderCacheService {

    private ExecutorService executor;
    private Map<String, String> cache;

    @Override
    protected void doStartService() throws Exception {
        executor = Executors.newFixedThreadPool(2);
        cache = new ConcurrentHashMap<>();
    }

    @Override
    protected void doStopService() {
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
        if (cache != null) {
            cache.clear();
            cache = null;
        }
    }

    @Override
    public String getOrderName(String orderUuid) throws Exception {
        return cache.get(orderUuid);
    }

    @Override
    public void putOrderName(String orderUuid, String orderName) throws Exception {
        cache.put(orderUuid, orderName);
    }
}

package test;

import bap.cells.Cells;
import bap.tester.BapTester;
import cell.example.order.IOrderCacheService;
import org.junit.Test;

public class OrderCacheServiceTest extends BapTester {

    @Test
    public void testCacheService() throws Exception {
        IOrderCacheService service = Cells.get(IOrderCacheService.class);
        service.putOrderName("order-001", "采购单-001");
        assert "采购单-001".equals(service.getOrderName("order-001"));
    }
}
