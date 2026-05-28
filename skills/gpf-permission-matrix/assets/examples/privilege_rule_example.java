import java.util.Map;

public class PrivilegeRuleExample {

    public void applyPrivilege(Map<String, Object> env) throws Exception {
        Object privilege = env.get("$privilege");
        if (privilege == null) {
            throw new IllegalStateException("权限对象不存在");
        }
    }
}
