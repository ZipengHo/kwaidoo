package cell.bap.test;

import bap.cells.BasicCell;
import bap.cells.Cells;

// 默认的cell1，模拟服务端实现
public class DemoCell1Impl extends BasicCell implements DemoCell1
{
    private static final long serialVersionUID = 8416950220604792271L;

    public Object test(Object o)
    {
        try (DemoCell2 c2 = Cells.get(DemoCell2.class))
        {
            c2.test();

            // 模拟调用另外的cell，而且要传入一个服务器资源Dao进去
            // 用完后，哪里发起哪里销毁
            try (DemoDao dao = new DemoDaoImpl())
            {
                dao.insert("In cell1");

                c2.testShareDao(dao);

                DemoCell3 c3 = Cells.get(DemoCell3.class);
                c3.testShareDao(dao);
            }

            return "This is default demo cell1 : " + o;
        }
    }

    public void hello(String msg)
    {
        System.out.println("Hi : " + msg);
    }

}
