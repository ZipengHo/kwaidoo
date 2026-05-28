import org.nutz.dao.Cnd;

public class AntiPatternCndBusinessFieldName {

    public Cnd wrong() {
        return Cnd.where("客户名称", "=", "张三");
    }
}
