import cell.cdao.IDao;
import cell.gpf.adur.data.IFormMgr;
import gpf.adur.data.AssociationData;
import gpf.adur.data.Form;

public class FormUpdateWithAssociationExample {

    public Form updateOwner(IDao dao, String userModelId, String userCode, Form form)
            throws Exception {
        form.setAttrValue("负责人", new AssociationData(userModelId, userCode));
        Form updated = IFormMgr.get().updateForm(dao, form);
        dao.commit();
        return updated;
    }
}
