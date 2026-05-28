package cell.example.rule;

import cell.CellIntf;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import gpf.adur.data.Form;

@ClassDeclare(
        label = "数据填值规则",
        what = "保存前自动填充业务字段",
        why = "减少手工录入并保证字段完整",
        how = "在后端数据填值规则中配置使用",
        developer = "开发者",
        createTime = "2026-03-10",
        updateTime = "2026-03-10",
        version = "1.0"
)
public interface DataFillValueRule extends CellIntf {
    String FIELD_ORDER_NO = "订单编号";
    String FIELD_APPLY_TIME = "申请时间";

    @MethodDeclare(
            label = "填充编号时间",
            what = "自动填充订单编号和申请时间",
            how = "在保存前数据填值规则中使用",
            why = "保证关键字段自动生成",
            inputs = {
                    @InputDeclare(desc = "运行时上下文", name = "rtx", label = "运行时上下文", exampleValue = "$IDCRuntimeContext$"),
                    @InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$"),
                    @InputDeclare(desc = "编号前缀", name = "prefix", label = "编号前缀")
            }
    )
    default void fillOrderNoAndTime(IDCRuntimeContext rtx, Form form, String prefix) throws Exception {
        form.setAttrValue(FIELD_ORDER_NO, prefix + System.currentTimeMillis());
        form.setAttrValue(FIELD_APPLY_TIME, System.currentTimeMillis());
    }
}
