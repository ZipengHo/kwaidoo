package cell.bap.test;

import bap.cells.BasicCell;
import bap.cells.Cells;

public class DemoCell3Impl extends BasicCell implements DemoCell3
{

    private static final long serialVersionUID = -3569152081847685091L;

    public void testShareDao(DemoDao tran)
    {
        tran.delete("In Cell3");
        tran.commit("I'm DemoCell3Impl");
        
        DemoCell1 c1= Cells.get(DemoCell1.class);
        
        c1.hello("I'm cell3");
        
        // 一般传入的callback不在这里close，哪里创建哪里close的原则
//        tran.close();
    }

    public void test()
    {
        System.out.println("DemoCell3Impl::test()");
    }

}
