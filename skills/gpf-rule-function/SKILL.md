---
name: gpf-rule-function
description: GPF 规则函数开发主技能。用于实现和修改数据校验、数据填值、数据过滤、流程回调、身份匹配、动态权限等运行在规则执行环境中的规则函数代码；本技能内所有规则函数统一按 `CellIntf` 体系开发，要求使用 `cell.{项目名}.rule` 包名，并补齐注解、输入声明和环境变量使用；不适用于 HTTP 接口、独立权限引擎设计或 Cell 生命周期任务。
---

# GPF 规则函数开发

## 适用范围

适用：

- 数据校验
- 数据填值
- 数据过滤
- 回调处理
- 路由计算
- 身份匹配
- 动态权限
- CM服务调用规则，例如在一个CM服务中调用另一个CM服务的保存、提交、详情查询、列表查询或实例化操作
- 环境变量声明
- 注解补齐
- 统一按 `CellIntf` 体系编写的规则函数
- 定时任务中的自定义定时规则（`TimeRule`）

不适用：

- 对外 HTTP 接口
- 完整权限矩阵引擎设计
- 独立于规则执行环境的授权基础设施
- 长生命周期服务 Cell

## 输入契约

至少确认：

- 规则函数用途
- 触发场景
- 用户提供的表单元数据信息
- 业务参数和环境变量
- 现有函数签名或目标返回类型
- 对外暴露的入参是否为业务可理解语义，而不是技术实现语义

## 执行流程

1. 先读 `references/overview.md` 判断规则函数类型。
2. 再读 `references/rule-decision-table.md` 和 `references/core-concepts.md`，确认规则类型、注解、环境变量与业务参数约束，尤其检查方法入参是否对业务人员友好。
3. 按任务类型补充资料：
   - 涉及测试：读 `references/testing.md`
   - 涉及排错：读 `references/errors.md`
   - 涉及权限：补读 `gpf-permission-matrix`
   - 涉及 CM 服务调用、`NCMDataService`、一个 CM 服务调用另一个 CM 服务、按关联工程调用目标 CM 保存操作：读 `assets/examples/ncm_data_service_rule.java`
   - 开发规则函数：同时加载 `gpf-api-call`
   - 编写测试代码：同时加载 `gpf-api-call`
   - 开发自定义定时规则：阅读 `references/自定义定时规则.md`
4. 生成代码前读 `references/workflow.md` 和 `references/patterns.md`。
5. 如果想避免典型误写，读 `references/anti-patterns.md`。
6. 输出前按 `references/checklist.md` 自检，并参考 `assets/examples/`。

## 输出契约

输出必须包含：

- 规则函数用途和触发场景
- 类级与方法级声明
- 业务参数、环境变量和返回值说明
- 对外暴露入参是否满足业务语义约束的说明
- 风险和前提

如果任务是“自定义定时规则”，还必须额外包含：

- 定时规则的匹配语义与适用时间范围
- `isMatch(...)` 返回值与时间推进策略说明
- XML 序列化/反序列化方式
- 是否为秒级规则、是否需要预加载数据
- 与调度引擎加载方式相关的类路径前提

## 硬规则

- 本技能范围内的规则函数统一按 `CellIntf` 体系开发，不提供并行声明风格。
- 仅当任务明确是“自定义定时规则”时，不走 `CellIntf` 体系，而是实现 `cmn.dto.scheduletask.timerule.TimeRule`，具体规范见 `references/自定义定时规则.md`。
- 规则函数接口必须继承 `CellIntf`，包名必须使用 `cell.{项目名}.rule`。
- 编写 CM 服务调用规则时，必须通过 `NCMDataService` 调用目标 CM 服务的操作，不能绕过目标 CM 的查询、权限、状态流转和操作编排语义直接使用 `IFormMgr` 做等价 CRUD。
- 编写 CM 服务调用规则时，不要通过 `ContextModelMgr` 查目标 CM、不要手动 `cloneContext(...)` 后设置驱动和方法、不要直接调用 `executeCmMethod(...)`；这些属于绕开 `NCMDataService` 的内部执行路径。
- CM 服务调用规则的业务域一般就是当前规则运行所在业务域，应从 `IContext`、`$context$` 或 `$ruleNamespace$` 推导；除非用户明确要求跨业务域调用，否则不要把 `domain`、业务域编码或业务域常量写死在代码里，也不要暴露成业务人员需要填写的规则入参。
- 不得编造工程中现有类的包路径、接口定义、方法签名、继承关系或返回类型。
- 如果类名能确认但包路径、接口方法或实现细节不能确认，必须明确标注“待从源码核实”，不得补全猜测值。
- 表单元数据只能来自用户明确提供的信息；缺失时必须先确认，不能猜测补齐。
- 开发规则函数时必须同时加载 `gpf-api-call` 技能。
- 编写规则函数测试代码时也必须同时加载 `gpf-api-call` 技能，用于核对相关参数源码和构建示例。
- 只要规则函数测试中通过 `Cells.get(...)` 获取实例，测试类必须继承 `bap.tester.SilentBapTester`。
- 除通过上下文环境变量注入的参数外，规则函数方法对外暴露的所有入参都必须是业务语义参数，不能设计成技术含义参数。
- 所谓“技术含义参数”，包括但不限于：类全名、模型编码、字段路径表达式、数据库表名、SQL 片段、JSONPath、Bean 名称、Spring 容器键、内部服务标识、脚本片段、实现类名、上下文键名等需要业务人员理解技术实现细节的参数。
- 规则函数的直接使用者是业务人员，使用体验应接近“在 Excel 单元格中填写表达式”；因此方法入参名称、入参含义和可填写值都必须可被业务人员直接理解和填写。
- 如果某项信息本质上属于技术上下文、运行时依赖或平台注入信息，必须优先通过上下文环境变量、框架注入能力或代码内部推导获取，不能要求业务人员在规则函数表达式里显式传入。
- 只有在该参数确实对应业务概念、且业务人员能够稳定理解其含义与取值时，才允许将其设计为规则函数方法入参。
- 设计或审查规则函数签名时，必须单独检查“业务人员是否能在不理解底层技术实现的前提下正确填写该入参”；如果答案是否定的，则应重构为环境变量、内部推导或更高层级的业务语义参数。

## 详细规范

- 注解、环境变量与元数据约束：`references/core-concepts.md`
- 测试代码规范：`references/testing.md`
- 常见反例：`references/anti-patterns.md`
- 常见错误：`references/errors.md`
- 最终检查：`references/checklist.md`

## 引用导航

- 总览：`references/overview.md`
- 规则决策表：`references/rule-decision-table.md`
- 核心概念：`references/core-concepts.md`
- 环境变量：`references/environment-variables.md`
- 执行流程：`references/workflow.md`
- 代码模式：`references/patterns.md`
- 自定义定时规则：`references/自定义定时规则.md`
- 测试规范：`references/testing.md`
- 常见反例：`references/anti-patterns.md`
- 常见错误：`references/errors.md`
- 最终检查：`references/checklist.md`
- 资料索引：`references/资料索引.md`
- 源码类索引：`references/源码类索引.md`
- 源码快照：`assets/source/`
- 示例模板：`assets/examples/`
