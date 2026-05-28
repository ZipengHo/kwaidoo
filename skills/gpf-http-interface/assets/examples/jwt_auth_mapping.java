package cell.example.http.auth;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import cmn.dto.jwt.JwtUserInfo;
import cmn.http.anotation.RequestMapping;
import cmn.http.anotation.RequestMethod;
import cmn.http.servlet.mapping.RequestMappingIntf;

@ClassDeclare(
    label = "认证HTTP接口",
    what = "提供登录和令牌验证接口",
    why = "支持JWT认证和受保护接口访问",
    how = "通过HTTP请求获取并验证JWT令牌",
    developer = "张三",
    createTime = "2025-01-24",
    updateTime = "2025-01-24",
    version = "1.0"
)
@RequestMapping(path = "/example/auth")
public interface IAuthHttpMapping extends CellIntf, RequestMappingIntf {

    @MethodDeclare(
        label = "用户登录",
        what = "用户登录并生成JWT令牌",
        why = "为后续受保护接口调用提供认证凭证",
        how = "通过POST请求访问/example/auth/login",
        inputs = {
            @InputDeclare(name = "request", label = "登录请求", desc = "登录账号和密码", nullable = false)
        }
    )
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    String login(LoginRequest request) throws Exception;

    @MethodDeclare(
        label = "验证JWT令牌",
        what = "验证JWT令牌并返回用户信息",
        why = "判断当前令牌是否有效",
        how = "通过GET请求访问/example/auth/verify?token=xxx",
        inputs = {
            @InputDeclare(name = "token", label = "JWT令牌", desc = "待校验的JWT字符串", nullable = false)
        }
    )
    @RequestMapping(path = "/verify", method = RequestMethod.GET)
    JwtUserInfo verifyToken(String token) throws Exception;
}
