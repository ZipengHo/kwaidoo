# GPF 规则函数类型决策表

前提说明：

- 本表只用于区分规则函数的业务类型，不用于提供多套代码声明方式
- 表内所有规则类型都统一按 `CellIntf` 体系实现
- 表内所有规则函数都统一使用 `cell.{项目名}.rule` 包名

| 场景 | 规则类型 | 推荐样例 | 关键返回 |
|------|----------|----------|----------|
| 提交前拦截校验 | 数据校验 | `assets/examples/user_register_rule.java` | `void` 或抛出异常 |
| 自动修改表单字段 | 数据填值 | `assets/examples/data_fill_value_rule.java` | `void` |
| 列表筛选条件 | 数据过滤 | `assets/examples/data_filter_rule.java` | `Cnd` 或过滤结果 |
| 多表统计、复杂 SQL | SQL 查询 / 统计 | `assets/examples/data_filter_sql_rule.java` | `ResultSet<Form>` |
| 选择关联数据后联动回填界面 | 界面值自动填写 | `assets/examples/frontend_fill_value_rule.java` | `void` |
| 保存后自动触发流程 | 自动提交回调 | `assets/examples/auto_submit_rule.java` | 配置返回值 |
| 状态变化后生成待办 | 待办通知回调 | `assets/examples/todo_notify_rule.java` | 配置返回值 |
| 判断当前目标路由是否命中，或自主接管当前节点全部离开路由 | 路由计算 | `assets/examples/route_compute_rule.java` | `Pair<Boolean, String>` 或 `RouterOption` |
| 判断当前用户是否命中某身份，或查询某身份规则命中的用户列表 | 身份匹配 | `assets/examples/identity_match_rule.java` | `matchUser` 模式返回 `IdentifyMatchParam`；`queryUser` 模式返回 `List<User>` |
| 动态调整字段或操作权限 | 动态权限辅助逻辑 | `assets/examples/privilege_assign_rule.java` | `void` |
| 在规则环境内处理字段、按钮或局部授权控制 | 权限相关规则函数 | `assets/examples/privilege_assign_rule.java` | 与当前规则契约一致 |
| 在一个 CM 服务中调用另一个 CM 服务的操作，例如通过当前表单的关联工程属性调用编号为 `IML_00004` 的 CM 服务的保存操作 | CM服务调用规则 | `assets/examples/ncm_data_service_rule.java` | 目标 CM 操作返回值 |

## 选择规则

- 只做表单提交前校验：优先数据校验
- 只改表单值：优先数据填值
- 需要返回分页统计结果：优先 SQL 查询 / 统计
- 需要前端组件联动：优先界面值自动填写
- 需要配置流程或待办回调：优先回调类规则
- 需要身份匹配或动态权限，但最终产物仍是规则函数：继续使用本技能，并补充权限领域资料；身份匹配需要明确兼容 `matchUser` 与 `queryUser` 两种运行模式
- 需要一个 CM 服务调用另一个 CM 服务的保存、提交、详情查询、列表查询或实例化操作：优先使用 CM服务调用规则，读取 `assets/examples/ncm_data_service_rule.java`
- 需要身份体系、授权对象建模或权限矩阵引擎完整能力：超出本技能边界，补充权限矩阵资料
