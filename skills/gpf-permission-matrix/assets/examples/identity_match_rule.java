package cell.example.rule;

import cell.CellIntf;
import cell.cdao.IDao;
import cell.gpf.adur.user.IUserMgr;
import cell.octo.cm.basic.IdentifyRuleIntf;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import com.kwaidoo.ms.tool.CmnUtil;
import gpf.adur.user.User;
import gpf.dc.basic.dto.privilege.IdentifyMatchParam;
import octo.cm.enums.ContextSystemVarKey;
import org.nutz.dao.Cnd;

import java.util.List;
import java.util.Map;

@ClassDeclare(
        label = "身份匹配规则",
        what = "按用户名称识别业务身份",
        why = "为权限矩阵提供身份命中判断和身份成员查询",
        how = "在身份匹配规则中使用",
        developer = "开发者",
        createTime = "2026-05-14",
        updateTime = "2026-05-14",
        version = "1.0"
)
public interface IdentityMatchRuleExample extends CellIntf, IdentifyRuleIntf {

    @MethodDeclare(
            label = "指定用户",
            what = "判断当前用户是否为指定用户，或查询指定用户身份命中的用户列表",
            how = "在身份匹配规则中使用",
            why = "同一个身份规则需要同时支持 matchUser 和 queryUser 两种运行模式",
            inputs = {
                    @InputDeclare(desc = "规则运行环境", name = "env", label = "规则运行环境", exampleValue = "$env$"),
                    @InputDeclare(desc = "用户名称", name = "userName", label = "用户名称")
            }
    )
    default Object matchUser(Map<String, Object> env, String userName) throws Exception {
        if (isMatchUserMode(env)) {
            User operator = ContextSystemVarKey.$operator$.getContextValue(env);
            IdentifyMatchParam param = new IdentifyMatchParam();
            param.setMatchExpression(String.valueOf(CmnUtil.isStringEqual(operator.getUserName(), userName)));
            return param;
        }

        IDao dao = ContextSystemVarKey.$dao$.getContextValue(env);
        String userModelId = getUserModelId(env);
        Cnd cnd = Cnd.where(User.UserName, "=", userName);
        List<User> users = IUserMgr.get().queryUserPage(dao, userModelId, cnd, 1, 1, false).getDataList();
        return users;
    }
}
