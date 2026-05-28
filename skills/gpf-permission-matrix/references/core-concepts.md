# GPF 权限领域核心概念

本资料只补充权限领域特有语义，不重复规则函数通用规范。

## `IdentifyMatchParam`

用于描述 `matchUser` 模式下的身份匹配结果。

关键点：

- 身份匹配规则有两种运行模式，由 `$env$` 中的 `$identifyRuleMode$` 区分
- `matchUser`：计算当前用户是否匹配指定身份规则，返回 `IdentifyMatchParam`
- `queryUser`：计算当前身份规则匹配的用户列表，返回 `List<User>`
- 默认运行模式是 `matchUser`
- 身份匹配规则方法通常返回 `Object`，以承接两种模式下的不同返回类型
- `matchUser` 模式可直接返回简单真假表达式，也可构造更复杂的匹配表达式
- `matchUser` 模式表达式应使用占位符语法，不要硬编码表别名
- `queryUser` 模式必须返回身份规则实际命中的用户列表，不能继续返回 `IdentifyMatchParam`

## `IdentifyRuleIntf`

用于读取身份匹配规则运行模式和模型上下文。

关键点：

- 身份匹配方法的运行环境入参使用 `Map<String,Object> env`，通过 `$env$` 注入
- `isMatchUserMode(env)` 判断当前是否为 `matchUser` 模式
- `isQueryUserMode(env)` 判断当前是否为 `queryUser` 模式
- `getUserModelId(env)` 获取用户模型 ID
- `getOrgModelId(env)` 获取组织模型 ID
- 不要按旧接口把身份匹配方法写成 `IContext context`

## 表达式规范

- 当前表字段：`#表单#.#字段名#`
- 关联视图字段：`#视图名#.#字段名#`

不要写死 `T1` 之类的别名。

## `PrivilegeRuleIntf`

用于对权限对象做修改或授权。

关键点：

- 明确当前在处理字段权限还是操作权限
- 明确权限的最终生效范围
- 明确当前是在规则函数中做局部权限控制，还是已经进入完整矩阵建模场景
