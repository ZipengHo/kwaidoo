package cell.example.rule;

import cell.CellIntf;
import cell.octo.cm.IContext;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import cmn.exception.VerifyException;
import gpf.adur.data.Form;

@ClassDeclare(
        label = "用户注册规则",
        what = "用户注册时的数据校验",
        why = "确保用户数据有效",
        how = "在提交前校验规则配置中使用",
        developer = "开发者",
        createTime = "2026-03-10",
        updateTime = "2026-03-10",
        version = "1.0"
)
public interface UserRegisterRule extends CellIntf {
    String FIELD_PHONE = "手机号";

    @MethodDeclare(
            label = "用户注册校验",
            what = "校验用户注册数据",
            how = "在提交前校验规则中使用",
            why = "防止无效数据进入系统",
            inputs = {
                    @InputDeclare(desc = "运行上下文", name = "context", label = "运行上下文", exampleValue = "$context$"),
                    @InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$")
            }
    )
    default void checkUserRegister(IContext context, Form form) throws Exception {
        String phone = form.getString(FIELD_PHONE);
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            throw new VerifyException("手机号格式不正确");
        }
    }
}
