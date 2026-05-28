package cell.bap.test;

import com.cdao.model.CDoNamed;

import bap.cells.BasicCell;
import bap.cells.Cells;
import cell.cdao.IDao;
import cell.cdao.IDaoService;

public class DemoCell2Impl extends BasicCell implements DemoCell2
{

    private static final long serialVersionUID = -3569152081847685091L;

    public void testShareDao(DemoDao tran)
    {
        tran.update("Update in cell2");
        
        tran.commit("I'm DemoCell2Impl");
    }

    public void test()
    {
        System.out.println(this.getClass().getName() + "::test()");
    }

    public Object testRealDao(IDao dao, String name) throws Exception
    {
        CDoNamed ndo = new CDoNamed();
        ndo.setName(name);

        ndo = dao.createDo(ndo);

        Object dbData = IDaoService.get().cqueryDo(ndo.getGid());
        Object testData = dao.queryDo(ndo.getGid());
        
        return ndo;
    }

}
