package cell.example.rule;

import cell.CellIntf;
import cell.octo.cm.IContext;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import gpf.adur.data.Form;
import gpf.dto.cfg.runtime.RouterOption;
import java.util.Collections;
import web.dto.Pair;

@ClassDeclare(
        label = "路由计算规则",
        what = "根据业务状态计算流程路由",
        why = "让流程分支按业务条件自动选择",
        how = "在流程路由规则中配置使用",
        developer = "开发者",
        createTime = "2026-03-10",
        updateTime = "2026-03-10",
        version = "1.0"
)
public interface RouteComputeRule extends CellIntf {
    String FIELD_ORDER_AMOUNT = "订单金额";
    String FIELD_ORDER_TYPE = "订单类型";
    String NODE_FINANCE_APPROVAL = "financeApproval";
    String NODE_MANAGER_APPROVAL = "managerApproval";

    @MethodDeclare(
            label = "计算是否命中财务审批",
            what = "根据金额判断当前目标路由是否命中",
            how = "在流程路由规则中使用",
            why = "每次路由计算只回答当前目标是否匹配",
            inputs = {
                    @InputDeclare(desc = "运行上下文", name = "context", label = "运行上下文", exampleValue = "$context$"),
                    @InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$")
            }
    )
    default Pair<Boolean, String> matchFinanceApproval(IContext context, Form form) throws Exception {
        Double amount = form.getDouble(FIELD_ORDER_AMOUNT);
        if (amount != null && amount > 10000D) {
            return new Pair<>(true, "");
        }
        return new Pair<>(false, "订单金额未达到财务审批条件");
    }

    @MethodDeclare(
            label = "自主计算订单审批路由",
            what = "根据订单业务信息直接返回路由选项",
            how = "在启用自主路由的流程路由规则中使用",
            why = "返回 RouterOption 后接管当前节点所有离开路由规则",
            inputs = {
                    @InputDeclare(desc = "运行上下文", name = "context", label = "运行上下文", exampleValue = "$context$"),
                    @InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$")
            }
    )
    default RouterOption computeApprovalRouter(IContext context, Form form) throws Exception {
        Double amount = form.getDouble(FIELD_ORDER_AMOUNT);
        String orderType = form.getString(FIELD_ORDER_TYPE);

        if ("特批".equals(orderType)) {
            return new RouterOption().setNexts(Collections.singleton(NODE_MANAGER_APPROVAL));
        }
        if (amount != null && amount > 10000D) {
            return new RouterOption().setNexts(Collections.singleton(NODE_FINANCE_APPROVAL));
        }
        return new RouterOption().setGoNextAll(false);
    }
}
