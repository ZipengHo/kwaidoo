package cell.example.rule;

import cell.CellIntf;
import cell.octo.cm.basic.IBasicNotifyTodoCallback;
import cell.octo.cm.todo.IMessageService;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import octo.lang.i18n.OctoCMI18n;
import octo.cm.todo.MessageTemplateDto;

import java.util.Set;

@ClassDeclare(
        label = "待办通知配置",
        what = "状态变化时配置自动生成待办",
        why = "把关键状态流转转成待办通知",
        how = "在保存规则或流程进入规则中配置",
        developer = "开发者",
        createTime = "2026-03-10",
        updateTime = "2026-03-10",
        version = "1.0"
)
public interface TodoNotifyRule extends CellIntf {

    @MethodDeclare(
            label = "生成待办配置",
            what = "状态命中后生成待办配置",
            how = "在保存规则中使用",
            why = "自动把待审批状态转成待办",
            inputs = {
                    @InputDeclare(desc = "运行时上下文", name = "rtx", label = "运行时上下文", exampleValue = "$IDCRuntimeContext$"),
                    @InputDeclare(desc = "命名空间", name = "namespaces", label = "命名空间", exampleValue = "$ruleNamespace$"),
                    @InputDeclare(desc = "消息模板", name = "messageTmplt", label = "消息模板"),
                    @InputDeclare(desc = "状态字段", name = "statusField", label = "状态字段"),
                    @InputDeclare(desc = "状态值", name = "statusValue", label = "状态值"),
                    @InputDeclare(desc = "接收人规则", name = "assigneeRule", label = "接收人规则"),
                    @InputDeclare(desc = "逾期时间", name = "overdueTime", label = "逾期时间")
            }
    )
    default void buildTodoNotify(IDCRuntimeContext rtx, Set<String> namespaces, String messageTmplt,
                                 String statusField, String statusValue, String assigneeRule, Long overdueTime)
            throws Exception {
        MessageTemplateDto msgTmplt = IMessageService.get().queryTemplateByCode(rtx.getDao(), messageTmplt);
        if (msgTmplt == null) {
            throw new Exception(OctoCMI18n.format("消息模板[{1}]不存在！", messageTmplt));
        }

        rtx.addSaveTotalFormCallback(IBasicNotifyTodoCallback.class);
        rtx.addSaveTotalFormCallbackParam(IBasicNotifyTodoCallback.Key_messageTmplt, messageTmplt);
        rtx.addSaveTotalFormCallbackParam(IBasicNotifyTodoCallback.Key_assigneeRule, assigneeRule);
        rtx.addSaveTotalFormCallbackParam(IBasicNotifyTodoCallback.Key_statusValue, statusValue);
        rtx.addSaveTotalFormCallbackParam(IBasicNotifyTodoCallback.Key_$statusField$, statusField);
        rtx.addSaveTotalFormCallbackParam(IBasicNotifyTodoCallback.Key_namespaces, namespaces);
        rtx.addSaveTotalFormCallbackParam(IBasicNotifyTodoCallback.Key_overdueTime, overdueTime);
    }
}
