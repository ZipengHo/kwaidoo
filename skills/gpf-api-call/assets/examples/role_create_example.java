import cell.cdao.IDao;
import cell.gpf.adur.role.IRoleMgr;
import gpf.adur.role.Role;

public class RoleCreateExample {

    public Role createRole(IDao dao, String orgModelId, String orgUuid, Role role) throws Exception {
        Role created = IRoleMgr.get().createRole(dao, orgModelId, orgUuid, role);
        dao.commit();
        return created;
    }
}
