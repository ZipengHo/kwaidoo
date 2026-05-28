package cell.example.rule;

import cell.CellIntf;
import cell.cdao.IDao;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import gpf.adur.data.Form;
import gpf.adur.data.FormModel;
import gpf.adur.data.ResultSet;
import org.nutz.dao.Cnd;

import java.util.LinkedHashSet;
import java.util.Set;

@ClassDeclare(
        label = "SQL统计规则",
        what = "通过自定义SQL返回统计结果",
        why = "支持复杂统计和聚合查询",
        how = "在统计列表或复杂查询规则中使用",
        developer = "开发者",
        createTime = "2026-03-10",
        updateTime = "2026-03-10",
        version = "1.0"
)
public interface DataFilterSqlRule extends CellIntf {
    String FIELD_CUSTOMER = "客户";
    String FIELD_ORDER_AMOUNT = "订单金额";

    @MethodDeclare(
            label = "统计客户订单",
            what = "按客户统计订单数量和金额",
            how = "在订单统计查询规则中使用",
            why = "生成客户维度统计报表",
            inputs = {
                    @InputDeclare(desc = "运行时上下文", name = "rtx", label = "运行时上下文", exampleValue = "$IDCRuntimeContext$"),
                    @InputDeclare(desc = "查询条件", name = "cnd", label = "查询条件", exampleValue = "$sysvar_cnd$"),
                    @InputDeclare(desc = "页码", name = "pageNo", label = "页码", exampleValue = "$sysvar_pageNo$"),
                    @InputDeclare(desc = "每页数量", name = "pageSize", label = "每页数量", exampleValue = "$sysvar_pageSize$")
            }
    )
    default ResultSet<Form> statisticByCustomer(IDCRuntimeContext rtx, Cnd cnd, int pageNo, int pageSize)
            throws Exception {
        IDao dao = rtx.getDao();
        String modelId = rtx.getPdfUuid();
        FormModel formModel = IFormMgr.get().queryFormModel(modelId);
        String tableName = formModel.getTableName();
        String customerFieldCode = IFormMgr.get().getFieldCode(FIELD_CUSTOMER);
        String amountFieldCode = IFormMgr.get().getFieldCode(FIELD_ORDER_AMOUNT);
        String sql = String.format(
                "SELECT %s, COUNT(*) AS orderCount, SUM(%s) AS totalAmount FROM %s GROUP BY %s",
                customerFieldCode, amountFieldCode, tableName, customerFieldCode
        );
        Set<String> extFields = new LinkedHashSet<>();
        return IFormMgr.get().queryFormPageBySql(dao, modelId, sql, extFields, cnd, pageNo, pageSize);
    }
}
