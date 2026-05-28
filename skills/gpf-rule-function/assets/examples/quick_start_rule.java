package cell.example.rule;

import cell.CellIntf;
import cell.octo.cm.IContext;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import gpf.adur.data.Form;

@ClassDeclare(
        label = "入门规则",
        what = "演示第一个规则函数的最小结构",
        why = "帮助开发者快速理解规则函数的声明方式",
        how = "在简单界面动作或调试场景中使用",
        developer = "开发者",
        createTime = "2026-03-11",
        updateTime = "2026-03-11",
        version = "1.0"
)
public interface QuickStartRule extends CellIntf {
    String FIELD_REMARK = "备注";

    @MethodDeclare(
            label = "初始化备注",
            what = "演示规则函数如何读取环境变量并回填表单字段",
            how = "在按钮动作或调试规则中使用",
            why = "提供可直接仿写的最小入门示例",
            inputs = {
                    @InputDeclare(desc = "运行上下文", name = "context", label = "运行上下文", exampleValue = "$context$"),
                    @InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$"),
                    @InputDeclare(desc = "提示文本", name = "message", label = "提示文本")
            }
    )
    default void initRemark(IContext context, Form form, String message) throws Exception {
        String operatorCode = context.getOperator() == null ? "未知用户" : context.getOperator().getCode();
        form.setAttrValue(FIELD_REMARK, operatorCode + ":" + message);
    }
}
