import gpf.adur.data.Form;
import gpf.adur.data.TableData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TableDataUsageExample {

    public TableData buildOrderDetails() throws Exception {
        TableData details = new TableData("gpf.md.order.OrderDetail");

        Form line1 = new Form("gpf.md.order.OrderDetail");
        line1.setAttrValue("商品编号", "SKU001");
        line1.setAttrValue("数量", 2L);
        line1.setAttrValue("单价", 199.00D);

        Form line2 = new Form("gpf.md.order.OrderDetail");
        line2.setAttrValue("商品编号", "SKU002");
        line2.setAttrValue("数量", 1L);
        line2.setAttrValue("单价", 299.00D);

        details.add(line1).add(line2);
        return details;
    }

    public void setOrderDetails(Form orderForm) throws Exception {
        TableData details = buildOrderDetails();
        orderForm.setAttrValue("订单明细", details);
    }

    public void resetOrderDetails(Form orderForm) throws Exception {
        List<Form> rows = new ArrayList<>();

        Form row = new Form("gpf.md.order.OrderDetail");
        row.setAttrValue("商品编号", "SKU100");
        row.setAttrValue("数量", 5L);
        row.setAttrValue("单价", 88.00D);
        rows.add(row);

        TableData details = new TableData("gpf.md.order.OrderDetail");
        details.setRows(rows);
        orderForm.setAttrValue("订单明细", details);
    }

    public Form getFirstLine(Form orderForm) throws Exception {
        TableData details = orderForm.getTable("订单明细");
        if (details == null || details.isEmtpy()) {
            return null;
        }
        return details.getData(0);
    }

    public Form getLineByUuid(Form orderForm, String lineUuid) throws Exception {
        TableData details = orderForm.getTable("订单明细");
        if (details == null || details.isEmtpy()) {
            return null;
        }
        return details.getData(lineUuid);
    }

    public Long getFirstLineOrderSeq(Form orderForm) throws Exception {
        Form firstLine = getFirstLine(orderForm);
        if (firstLine == null) {
            return null;
        }
        return firstLine.getLongByCode(TableData.OrderSeq);
    }

    public void removeFirstLine(Form orderForm) throws Exception {
        TableData details = orderForm.getTable("订单明细");
        if (details == null || details.isEmtpy()) {
            return;
        }
        details.delete(0);
    }

    public void removeLine(Form orderForm, Form line) throws Exception {
        TableData details = orderForm.getTable("订单明细");
        if (details == null || details.isEmtpy() || line == null) {
            return;
        }
        details.delete(line);
    }

    public void removeLinesByUuid(Form orderForm, String... lineUuids) throws Exception {
        TableData details = orderForm.getTable("订单明细");
        if (details == null || details.isEmtpy()) {
            return;
        }
        details.deleteByUuids(lineUuids);
    }

    public void traverseLines(Form orderForm) throws Exception {
        TableData details = orderForm.getTable("订单明细");
        if (details == null || details.isEmtpy()) {
            return;
        }

        for (Form line : details.getRows()) {
            String sku = line.getString("商品编号");
            Long qty = line.getLong("数量");
            Double price = line.getDouble("单价");
            Long orderSeq = line.getLongByCode(TableData.OrderSeq);
            if (sku != null && qty != null && price != null && orderSeq != null) {
                // 示例保留：明细行业务字段与系统字段都已可读取
            }
        }
    }

    public Map<String, Form> indexByUuid(Form orderForm) throws Exception {
        TableData details = orderForm.getTable("订单明细");
        if (details == null) {
            return null;
        }
        return details.getRowUuidMap();
    }
}
