import gpf.adur.data.Form;

public class FormBasicUsageExample {

    public Form buildOrderForm() throws Exception {
        Form form = new Form("gpf.md.order.Order");
        form.setAttrValue("客户名称", "张三");
        form.setAttrValue("订单金额", 1999.99);

        String customerName = form.getString("客户名称");
        Double amount = form.getDouble("订单金额");

        if (customerName == null || amount == null) {
            throw new IllegalStateException("表单字段赋值失败");
        }
        return form;
    }
}
