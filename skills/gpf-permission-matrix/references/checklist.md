# GPF 权限领域检查清单

- 是否明确是鉴权还是授权
- 是否明确控制对象
- 是否使用了占位符表达式
- 是否说明了生效范围
- 是否说明了依赖的业务字段
- 如果是身份匹配，是否使用 `$env$` 注入 `Map<String,Object> env`
- 如果是身份匹配，是否明确支持 `matchUser` 与 `queryUser` 两种运行模式
- 如果是身份匹配的 `matchUser` 模式，是否返回 `IdentifyMatchParam`
- 如果是身份匹配的 `queryUser` 模式，是否返回 `List<User>`
- 如果最终产物仍是规则函数，是否先以 `gpf-rule-function` 为主技能
- 是否区分了局部权限控制与完整矩阵设计
