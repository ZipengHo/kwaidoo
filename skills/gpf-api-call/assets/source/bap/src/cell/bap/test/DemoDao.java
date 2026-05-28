package cell.bap.test;

import crpc.CRpcCallbackIntf;

public interface DemoDao extends CRpcCallbackIntf
{
    public void insert(String msg);
    public void delete(String msg);
    public void update(String msg);
    public void commit(String msg);
}
