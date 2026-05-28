# GPF 权限领域代码模式

这些模式用于补充权限领域约束；如果最终代码仍是规则函数实现，其通用写法仍以 `gpf-rule-function` 为准。

## 简单身份匹配模式

身份匹配规则运行环境使用 `$env$` 注入的 `Map<String,Object> env`，并需要同时兼容两种运行模式：

- `matchUser`：返回 `IdentifyMatchParam`
- `queryUser`：返回 `List<User>`

```java
default Object matchAllUser(Map<String, Object> env) throws Exception {
    if (isMatchUserMode(env)) {
        IdentifyMatchParam param = new IdentifyMatchParam();
        param.setMatchExpression("true");
        return param;
    }

    IDao dao = ContextSystemVarKey.$dao$.getContextValue(env);
    String userModelId = getUserModelId(env);
    return IUserMgr.get().queryUserPage(dao, userModelId, null, 1, Integer.MAX_VALUE, false).getDataList();
}
```

## 指定用户匹配模式

```java
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
    return IUserMgr.get().queryUserPage(dao, userModelId, cnd, 1, 1, false).getDataList();
}
```

## 授权规则模式

- 明确当前控制对象
- 修改权限 DTO
- 输出中说明哪些身份获得了哪些能力
- 如果只是规则函数中的局部权限控制，不要把示例扩展成完整矩阵引擎
