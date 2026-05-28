# GPF 规则函数总览

## 典型类型

- 数据校验
- 数据填值
- 数据过滤
- 界面值自动填写
- SQL 查询 / 统计
- 自动提交回调
- 待办通知回调
- 路由计算
- 身份匹配
- 流程回调
- 动态权限辅助逻辑
- 权限相关规则函数

## 统一开发约束

- 本技能覆盖的所有规则函数都统一按 `CellIntf` 体系实现
- 包名统一使用 `cell.{项目名}.rule`
- 不在本技能内提供非 `CellIntf` 规则函数写法
- 身份匹配、动态权限、字段/按钮权限控制等权限领域规则函数，仍属于本技能范围
- 如果需求本质上不是 Cell 规则函数，就不应继续使用本技能生成代码

## 类型与样例映射

- 数据校验：`assets/examples/user_register_rule.java`
- 数据填值：`assets/examples/data_fill_value_rule.java`
- 数据过滤：`assets/examples/data_filter_rule.java`
- 界面值自动填写：`assets/examples/frontend_fill_value_rule.java`
- SQL 查询 / 统计：`assets/examples/data_filter_sql_rule.java`
- 自动提交回调：`assets/examples/auto_submit_rule.java`
- 待办通知回调：`assets/examples/todo_notify_rule.java`
- 路由计算：`assets/examples/route_compute_rule.java`
- 身份匹配：`assets/examples/identity_match_rule.java`
- 动态权限辅助：`assets/examples/privilege_assign_rule.java`

## 学习路径样例

- 入门最小示例：`assets/examples/quick_start_rule.java`
- 端到端实战整合：`assets/examples/order_rule.java`

## 适合规则函数的场景

- 运行在规则执行环境中
- 需要利用环境变量
- 由业务人员在配置层使用

## 不适合规则函数的场景

- 对外暴露 URL
- 长期维护缓存和线程池
- 完整权限矩阵引擎逻辑或授权模型设计
