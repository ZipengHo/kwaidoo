import cell.gpf.adur.data.IFormMgr;
import org.nutz.dao.Cnd;

public class CndAdvancedUsageExample {

    public Cnd build(String modelId, String status, String type, int level) throws Exception {
        String statusFieldCode = IFormMgr.get().getFieldCode("状态");
        String typeFieldCode = IFormMgr.get().getFieldCode("类型");
        String levelFieldCode = IFormMgr.get().getFieldCode("等级");

        return Cnd.where(statusFieldCode, "=", status)
                .and(Cnd.exps(typeFieldCode, "=", type).or(levelFieldCode, ">", level));
    }
}
