import cell.gpf.adur.data.IFormMgr;
import gpf.adur.data.Form;

public class AntiPatternBusinessFieldByCode {

    public void wrong(Form form) throws Exception {
        String customerFieldCode = IFormMgr.get().getFieldCode("客户名称");
        form.setAttrValueByCode(customerFieldCode, "张三");
        String customerName = form.getStringByCode(customerFieldCode);
        if (customerName == null) {
            throw new IllegalStateException("示例仅用于展示误用");
        }
    }
}
