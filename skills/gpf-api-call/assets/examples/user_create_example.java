import cell.gpf.adur.user.IUserMgr;
import cell.gpf.adur.user.User;
import cell.cdao.IDao;

public class UserCreateExample {

    public User createUser(IDao dao, User user) throws Exception {
        User created = IUserMgr.get().createUser(dao, user);
        dao.commit();
        return created;
    }
}
