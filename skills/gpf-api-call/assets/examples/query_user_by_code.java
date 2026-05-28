import cell.cdao.IDao;
import cell.gpf.adur.user.IUserMgr;
import gpf.adur.user.User;

public class QueryUserByCodeExample {

    public User query(IDao dao, String userModelId, String code) throws Exception {
        return IUserMgr.get().queryUserByCode(dao, userModelId, code);
    }
}
