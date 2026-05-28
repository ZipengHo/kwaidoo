import gpf.adur.data.Password;
import gpf.adur.user.User;

public class PasswordUsageExample {

    public User buildUserWithPassword(String userName, String plainPassword) throws Exception {
        User user = new User();
        user.setUserName(userName);

        Password password = new Password();
        password.setValue(plainPassword);
        user.setPassword(password);

        return user;
    }

    public String readPlainPassword(User user) throws Exception {
        Password password = user.getPassword();
        return password == null ? null : password.getValue();
    }
}
