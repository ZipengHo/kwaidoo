package cell.bap.test;

import cell.CellIntf;
import cell.cdao.IDao;

public interface DemoCell2 extends CellIntf
{
    public void test();
    
    /**
     * 模拟在两个cell之前传递一个本地资源Dao对象，并分别在两地commit
     * 测试双cell异地联调的情况
     */
    public void testShareDao(DemoDao tran);
    
    /**
     * 真实测试cell之间传递真的Dao（数据库连接）
     */
    public Object testRealDao(IDao dao, String name) throws Exception;
}
