import gpf.adur.data.Form;
import gpf.adur.data.TableData;

public class SystemFieldUsageExample {

    public void fillFormSystemFields(Form form) throws Exception {
        form.setAttrValueByCode(Form.Code, "ORDER001");

        String code = form.getStringByCode(Form.Code);
        String uuid = form.getStringByCode(Form.UUID);
        String owner = form.getStringByCode(Form.Owner);

        if (code == null) {
            throw new IllegalStateException("系统编号为空");
        }
        if (uuid != null && owner != null) {
            // 示例保留：系统属性已可读取
        }
    }

    public Long firstOrderSeq(Form orderForm) throws Exception {
        TableData details = orderForm.getTable("订单明细");
        if (details == null || details.isEmtpy()) {
            return null;
        }
        Form firstLine = details.getData(0);
        return firstLine.getLongByCode(TableData.OrderSeq);
    }
}
