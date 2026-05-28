package cell.bap.test;

import cell.CellIntf;

public interface DemoCell3 extends CellIntf
{
    public void test();
    
    /**
     * 模拟在两个cell之前传递一个本地资源Dao对象，并分别在两地commit
     * 测试双cell异地联调的情况
     */
    public void testShareDao(DemoDao tran);
}
