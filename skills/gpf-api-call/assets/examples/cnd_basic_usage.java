import cell.gpf.adur.data.IFormMgr;
import org.nutz.dao.Cnd;

public class CndBasicUsageExample {

    public Cnd build(String modelId, String customerName, String status) throws Exception {
        String customerFieldCode = IFormMgr.get().getFieldCode("客户名称");
        return Cnd.where(customerFieldCode, "=", customerName)
                .and("status", "=", status)
                .orderBy("createTime", false);
    }
}
