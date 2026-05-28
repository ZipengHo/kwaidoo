import cell.cdao.IDao;
import cell.gpf.adur.data.IFormMgr;
import gpf.adur.data.Form;
import gpf.adur.data.ResultSet;
import org.nutz.dao.Cnd;

public class QueryFormPageExample {

    public ResultSet<Form> query(IDao dao, String formModelId, String statusFieldCode, String status)
            throws Exception {
        Cnd cnd = Cnd.NEW();
        cnd.where().andEquals(statusFieldCode, status);
        return IFormMgr.get().queryFormPage(dao, formModelId, cnd, 1, 20, true, false, statusFieldCode);
    }
}
