package cell.example.rule;

import cell.CellIntf;
import cell.cdao.IDao;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import fe.cmn.panel.PanelContext;
import fe.util.component.AbsComponent;
import cmn.exception.VerifyException;
import gpf.adur.data.AssociationData;
import gpf.adur.data.Form;
import gpf.adur.data.FormModel;
import gpf.adur.data.ResultSet;
import gpf.dc.basic.fe.component.view.AbsFormView;
import gpf.dc.basic.param.view.BaseFeActionParameter;
import org.nutz.dao.Cnd;

import java.util.LinkedHashSet;
import java.util.Set;

@ClassDeclare(
        label = "订单管理规则",
        what = "演示一个业务域内如何组合多种规则函数能力",
        why = "提供端到端实战级参考样例",
        how = "在订单类表单和列表配置中组合使用",
        developer = "开发者",
        createTime = "2026-03-11",
        updateTime = "2026-03-11",
        version = "1.0"
)
public interface OrderRule extends CellIntf {
    String FIELD_CUSTOMER = "客户";
    String FIELD_CUSTOMER_BALANCE = "客户余额";
    String FIELD_ORDER_NO = "订单编号";
    String FIELD_ORDER_STATUS = "订单状态";
    String FIELD_PRODUCT = "产品";
    String FIELD_UNIT_PRICE = "单价";
    String FIELD_QUANTITY = "数量";
    String FIELD_TOTAL_AMOUNT = "订单金额";
    String STATUS_PENDING = "待审批";

    @MethodDeclare(
            label = "校验客户余额",
            what = "提交订单前检查客户余额是否足够",
            how = "在订单提交前校验规则中使用",
            why = "防止余额不足的订单进入流程",
            inputs = {
                    @InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$"),
                    @InputDeclare(desc = "数据访问对象", name = "dao", label = "数据访问对象", exampleValue = "$dao$")
            }
    )
    default void checkCustomerBalance(Form form, IDao dao) throws Exception {
        AssociationData customer = form.getAssociation(FIELD_CUSTOMER);
        if (customer == null || customer.getForm() == null) {
            throw new VerifyException("请选择客户");
        }
        Double balance = customer.getForm().getDouble(FIELD_CUSTOMER_BALANCE);
        Double totalAmount = form.getDouble(FIELD_TOTAL_AMOUNT);
        if (balance == null || totalAmount == null) {
            throw new VerifyException("客户余额或订单金额不能为空");
        }
        if (balance < totalAmount) {
            throw new VerifyException("客户余额不足，无法提交订单");
        }
    }

    @MethodDeclare(
            label = "生成订单编号",
            what = "根据前缀和时间戳生成订单编号",
            how = "在订单新增填值规则中使用",
            why = "保证订单编号统一且易追踪",
            inputs = {
                    @InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$"),
                    @InputDeclare(desc = "编号前缀", name = "prefix", label = "编号前缀")
            }
    )
    default void generateOrderNo(Form form, String prefix) throws Exception {
        String orderNo = prefix + System.currentTimeMillis();
        form.setAttrValue(FIELD_ORDER_NO, orderNo);
    }

    @MethodDeclare(
            label = "按状态过滤订单",
            what = "根据订单状态拼装列表过滤条件",
            how = "在订单列表过滤规则中使用",
            why = "支持按待审批、已完成等状态查看订单",
            inputs = {
                    @InputDeclare(desc = "运行时上下文", name = "rtx", label = "运行时上下文", exampleValue = "$IDCRuntimeContext$"),
                    @InputDeclare(desc = "状态值", name = "statusValue", label = "状态值")
            }
    )
    default Cnd filterByStatus(IDCRuntimeContext rtx, String statusValue) throws Exception {
        String statusFieldCode = IFormMgr.get().getFieldCode(FIELD_ORDER_STATUS);
        Cnd cnd = Cnd.NEW();
        cnd.where().andEquals(statusFieldCode, statusValue);
        return cnd;
    }

    @MethodDeclare(
            label = "计算订单金额",
            what = "选择产品并填写数量后自动计算订单金额",
            how = "在产品或数量字段值变化规则中使用",
            why = "减少人工计算和录入错误",
            inputs = {
                    @InputDeclare(desc = "动作参数", name = "input", label = "动作参数", exampleValue = "$ActionParameter$"),
                    @InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$")
            }
    )
    default void calculateOrderAmount(BaseFeActionParameter input, Form form) throws Exception {
        PanelContext panelContext = input.getPanelContext();
        AbsFormView formView = (AbsFormView) ((AbsComponent) input.getCurrentComponent());
        String unitPriceFieldCode = IFormMgr.get().getFieldCode(FIELD_UNIT_PRICE);
        String totalAmountFieldCode = IFormMgr.get().getFieldCode(FIELD_TOTAL_AMOUNT);
        AssociationData product = form.getAssociation(FIELD_PRODUCT);
        if (product == null || product.getForm() == null) {
            formView.setEditorValue(panelContext, form, unitPriceFieldCode, null);
            formView.setEditorValue(panelContext, form, totalAmountFieldCode, null);
            return;
        }
        Double unitPrice = product.getForm().getDouble(FIELD_UNIT_PRICE);
        Long quantity = form.getLong(FIELD_QUANTITY);
        formView.setEditorValue(panelContext, form, unitPriceFieldCode, unitPrice);
        if (unitPrice != null && quantity != null) {
            formView.setEditorValue(panelContext, form, totalAmountFieldCode, unitPrice * quantity);
        }
    }

    @MethodDeclare(
            label = "统计客户订单",
            what = "按客户统计订单数量和金额",
            how = "在订单统计查询规则中使用",
            why = "输出客户维度的订单统计结果",
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
        String amountFieldCode = IFormMgr.get().getFieldCode(FIELD_TOTAL_AMOUNT);
        String sql = String.format(
                "SELECT %s, COUNT(*) AS orderCount, SUM(%s) AS totalAmount FROM %s GROUP BY %s",
                customerFieldCode, amountFieldCode, tableName, customerFieldCode
        );
        Set<String> extFields = new LinkedHashSet<>();
        return IFormMgr.get().queryFormPageBySql(dao, modelId, sql, extFields, cnd, pageNo, pageSize);
    }

    @MethodDeclare(
            label = "审批订单",
            what = "将待审批订单更新为已审批",
            how = "在审批动作规则中使用",
            why = "演示业务动作型规则函数写法",
            inputs = {
                    @InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$")
            }
    )
    default void approveOrder(Form form) throws Exception {
        String status = form.getString(FIELD_ORDER_STATUS);
        if (!STATUS_PENDING.equals(status)) {
            throw new VerifyException("只有待审批的订单才能执行审批");
        }
        form.setAttrValue(FIELD_ORDER_STATUS, "已审批");
    }
}
