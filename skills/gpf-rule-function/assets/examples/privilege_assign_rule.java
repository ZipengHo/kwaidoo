package cell.example.rule;

import cell.CellIntf;
import cell.octo.cm.basic.PrivilegeRuleIntf;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import gpf.adur.data.Form;
import gpf.dto.model.data.ActionPrivilegeDto;
import gpf.dto.model.data.FieldPrivilegeDto;
import octo.cm.enums.ContextSystemVarKey;

import java.util.Map;

@ClassDeclare(
        label = "动态权限规则",
        what = "根据表单状态调整字段权限",
        why = "实现规则函数侧动态权限辅助",
        how = "在动态权限规则中配置使用",
        developer = "开发者",
        createTime = "2026-03-10",
        updateTime = "2026-03-10",
        version = "1.0"
)
public interface PrivilegeAssignRule extends CellIntf, PrivilegeRuleIntf {
    String FIELD_ORDER_STATUS = "订单状态";
    String STATUS_PENDING = "待审批";
    String STATUS_FINISHED = "已完成";
    String ACTION_SUBMIT = "提交";
    String ACTION_DELETE = "删除";

    @MethodDeclare(
            label = "设置动态权限",
            what = "根据状态设置字段或动作权限",
            how = "在动态权限规则中使用",
            why = "实现状态驱动的权限辅助控制",
            inputs = {
                    @InputDeclare(desc = "规则运行环境", name = "env", label = "规则运行环境", exampleValue = "$env$")
            }
    )
    default void assignPrivilege(Map<String, Object> env) throws Exception {
        Form form = ContextSystemVarKey.$form$.getContextValue(env);
        Object priv = getPrivilege(env);
        String status = form.getString(FIELD_ORDER_STATUS);
        if (priv instanceof FieldPrivilegeDto) {
            FieldPrivilegeDto fieldPrivilege = (FieldPrivilegeDto) priv;
            if (STATUS_PENDING.equals(status)) {
                fieldPrivilege.setVisible(true).setWritable(false);
            } else {
                fieldPrivilege.setVisible(true).setWritable(true);
            }
            return;
        }
        if (priv instanceof ActionPrivilegeDto) {
            ActionPrivilegeDto actionPrivilege = (ActionPrivilegeDto) priv;
            if (ACTION_SUBMIT.equals(actionPrivilege.getName())) {
                actionPrivilege.setVisible(true).setOperatable(!STATUS_FINISHED.equals(status));
            } else if (ACTION_DELETE.equals(actionPrivilege.getName())) {
                actionPrivilege.setVisible(!STATUS_PENDING.equals(status)).setOperatable(!STATUS_PENDING.equals(status));
            }
        }
    }
}
