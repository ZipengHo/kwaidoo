package cell.example.config;

import bap.cells.CellConfig;
import cell.ServiceCellIntf;

public class MessageServiceConfig extends CellConfig {

    private String endpoint = "http://127.0.0.1:8080/mock";
    private int timeout = 3000;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}

public interface IMessagePushService extends ServiceCellIntf {

    String currentEndpoint() throws Exception;
}

package cell.example.config.impl;

import bap.cells.BasicServiceCell;
import bap.cells.annotation.Config;
import cell.example.config.IMessagePushService;
import cell.example.config.MessageServiceConfig;

@Config(MessageServiceConfig.class)
public class CMessagePushService extends BasicServiceCell implements IMessagePushService {

    private MessageServiceConfig config;

    @Override
    protected void doStartService() throws Exception {
        config = getConfig(MessageServiceConfig.class);
    }

    @Override
    protected void doStopService() {
        config = null;
    }

    @Override
    public String currentEndpoint() throws Exception {
        return config.getEndpoint();
    }
}

package test;

import bap.cells.Cells;
import bap.tester.BapTester;
import cell.example.config.IMessagePushService;
import org.junit.Test;

public class MessagePushServiceTest extends BapTester {

    @Test
    public void testConfigService() throws Exception {
        IMessagePushService service = Cells.get(IMessagePushService.class);
        assert service.currentEndpoint() != null;
    }
}
