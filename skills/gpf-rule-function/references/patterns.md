# GPF 规则函数代码模式

## 入门最小模式

适用场景：

- 第一次编写规则函数
- 需要确认 `@ClassDeclare`、`@MethodDeclare` 与环境变量注入的最小结构

推荐样例：

- `assets/examples/quick_start_rule.java`

## 数据校验模式

```java
import cmn.exception.VerifyException;

public interface UserRegisterRule extends CellIntf {
    String FIELD_PHONE = "手机号";

@MethodDeclare(
    label = "用户注册校验",
    what = "校验用户注册数据的有效性",
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
```

## 数据填值模式

适用场景：

- 自动编号
- 自动时间
- 自动默认值

推荐样例：

- `assets/examples/data_fill_value_rule.java`

关键点：

- 用环境变量拿上下文
- 用表单对象回填字段
- 不把技术参数直接暴露给业务配置
- 只有环境变量参数才写 `exampleValue`；普通业务参数不要补这个属性
- 表单属性名不要在方法体里直接写魔法值，统一定义在类定义上再使用

## 数据过滤模式

适用场景：

- 列表筛选
- 按状态、创建人、业务条件过滤

推荐样例：

- `assets/examples/data_filter_rule.java`

关键点：

- 业务侧输入字段名称和值，函数内部再转换字段编码
- 返回 `Cnd` 时优先展示最小可运行写法
- 如果需要多表关联或聚合，不要硬塞到普通过滤，改看 SQL 查询 / 统计样例
- 如果规则内部还要读取或回填表单字段，字段名同样先定义在类定义上，不能直接写在方法体里

## 界面值自动填写模式

适用场景：

- 选中关联对象后回填字段
- 根据数量、单价实时计算小计

推荐样例：

- `assets/examples/frontend_fill_value_rule.java`

关键点：

- 区分 `Form` API 的字段名称和界面交互 API 的字段编码
- 需要前端动作参数或面板上下文
- 当要处理关联字段、价格回填、小计联动时先看这个样例

## SQL 查询 / 统计模式

适用场景：

- 聚合统计
- 多表关联
- 简单 `Cnd` 无法表达的复杂查询

推荐样例：

- `assets/examples/data_filter_sql_rule.java`

关键点：

- 返回 `ResultSet<Form>`
- 需要页码、页大小、查询条件等环境变量时显式声明

## 自动提交回调模式

适用场景：

- 保存后自动触发流程

推荐样例：

- `assets/examples/auto_submit_rule.java`

关键点：

- 这是配置型回调，不是普通校验函数
- 回调参数要贴近真实回调 API，不要只返回占位字符串
- 输出要说明配置位置和触发条件

## 待办通知回调模式

适用场景：

- 状态变更后自动创建待办

推荐样例：

- `assets/examples/todo_notify_rule.java`

关键点：

- 需要状态字段、状态值、接收人规则
- 需要校验消息模板是否存在，并写入保存回调参数
- 输出要说明消息模板和命名空间

## 路由计算模式

适用场景：

- 根据业务条件判断当前目标路由是否命中
- 自主计算当前节点的后续路由，并接管当前节点所有离开路由规则

推荐样例：

- `assets/examples/route_compute_rule.java`

关键点：

- `Pair<Boolean, String>` 和 `RouterOption` 都是可接收的路由计算返回结果。
- 返回 `Pair<Boolean, String>` 时，规则只判断当前目标路由是否命中：
  - 第一个值表示“当前目标路由是否匹配”
  - 第二个值表示提示信息或未命中原因
  - 每次路由计算都只针对一个目标做匹配判断，不是一次返回整个路由名
- 返回 `RouterOption` 时，表示启用自主路由并接管当前节点的所有离开路由规则：
  - 一旦返回 `RouterOption`，当前节点所有离开路由规则都不再按 `Pair<Boolean, String>` 逐条判断
  - `goNextAll` 为 `true` 时表示离开路由包含所有下游节点，优先级高于 `nexts`
  - `nexts` 用于指定下一步节点 key 列表
  - `resetBefore` 用于指定跳转下一步前重置状态的节点
  - `resetAfter` 用于指定跳转下一步后重置状态的节点
- 输出示例时必须明确当前使用的是“单目标路由判断”还是“自主路由接管”。

## 身份匹配模式

适用场景：

- 判断当前用户是否命中某身份
- 查询某个身份规则命中的用户列表

推荐样例：

- `assets/examples/identity_match_rule.java`

关键点：

- 身份匹配规则运行时通过 `$identifyRuleMode$` 区分两种模式：
  - `matchUser`：计算当前用户是否匹配指定身份规则，返回 `IdentifyMatchParam`
  - `queryUser`：计算当前身份规则匹配的用户列表，返回 `List<User>`
- 默认运行模式是 `matchUser`
- 身份匹配方法的运行环境入参使用 `Map<String,Object> env`，通过 `$env$` 注入；不要再按旧写法声明 `IContext context`
- `isMatchUserMode(...)`、`isQueryUserMode(...)`、`getUserModelId(...)`、`getOrgModelId(...)` 都接收 `env`
- 同一个身份匹配方法通常需要同时支持两种模式，方法返回类型应使用 `Object` 承接两类返回值
- 在 `matchUser` 模式下，必须返回初始化后的 `IdentifyMatchParam` 并设置 `matchExpression`
- 在 `queryUser` 模式下，必须返回符合当前身份规则的 `List<User>`
- 方法名优先使用 `matchXxx`，但不要把方法实现限制为只能处理 `matchUser` 模式
- 只覆盖规则函数侧身份匹配，不展开完整权限矩阵引擎

## 动态权限辅助模式

适用场景：

- 根据表单状态动态调整字段或操作权限

推荐样例：

- `assets/examples/privilege_assign_rule.java`

关键点：

- 只示范“如何根据当前表单和当前权限对象做辅助调整”
- 可以区分 `FieldPrivilegeDto` 与 `ActionPrivilegeDto`
- 不在本技能里展开完整权限矩阵设计、身份建模和授权编排

## CM服务调用模式

适用场景：

- 在一个 CM 服务中调用另一个 CM 服务的保存、提交、详情查询、列表查询或实例化操作
- 通过当前表单的关联属性，例如“关联工程”，拿到目标表单后调用目标 CM 服务操作
- 用户描述为“编写一个CM服务调用规则，通过当前表单的关联工程属性调用编号为 IML_00004 的CM服务的保存操作”

推荐样例：

- `assets/examples/ncm_data_service_rule.java`

关键点：

- 这是 CM 服务之间的调用，不是普通表单 CRUD。
- 目标 CM 的保存、提交、查询和实例化应通过 `NCMDataService.internalOpeationCall(...)` 调用。
- 不要绕过目标 CM 的查询、权限、状态流转和操作编排语义直接使用 `IFormMgr` 查询或操作表单。
- 不要通过 `ContextModelMgr.getCachedContextModel(...)`、`context.cloneContext(targetCm)`、手动设置驱动/方法和 `executeCmMethod(...)` 来调用目标 CM；这会绕开 `NCMDataService` 统一服务入口。
- 生成代码中必须出现 `NCMDataService`、`NCMOperationParameter` 和 `RpcMap`；如果出现 `ContextModelMgr`、`DriverDto`、`Methods` 或 `executeCmMethod`，说明走错了模式。
- 业务域一般使用当前规则运行所在业务域，从 `$context$` / `$ruleNamespace$` 推导；除非明确是跨业务域调用，不要在代码中写死 `domain`。
- 关联字段读取使用 `form.getAssociation("关联工程")`，关联数据编号通过 `AssociationData.getValue()` 获取，关联表单可通过 `AssociationData.getForm()` 获取。
- 目标表单要放入 `ContextSystemVarKey.$form$` 上下文变量，再调用目标 CM 的内部操作。

## 实战整合模式

适用场景：

- 需要在一个业务域内组合校验、填值、过滤、界面联动和统计查询
- 需要给业务方展示“一个规则接口可以承载多种规则方法”的完整示例

推荐样例：

- `assets/examples/order_rule.java`

关键点：

- 仍按单一业务域组织方法，而不是把所有规则类型打散到多个无关接口
- 保持字段常量集中定义，避免一个大型接口里到处散落魔法值
- 涉及 `Cnd`、SQL 统计和界面赋值时，继续遵守字段名称与字段编码的边界

## 何时查阅哪个样例

- 需要最基础的提交前校验：读取 `assets/examples/user_register_rule.java`
- 需要第一个可运行的规则函数骨架：读取 `assets/examples/quick_start_rule.java`
- 需要保存前自动补字段：读取 `assets/examples/data_fill_value_rule.java`
- 需要按业务字段拼接 `Cnd`：读取 `assets/examples/data_filter_rule.java`
- 需要界面字段联动回填：读取 `assets/examples/frontend_fill_value_rule.java`
- 需要统计、聚合、复杂列表查询：读取 `assets/examples/data_filter_sql_rule.java`
- 需要保存后自动触发流程：读取 `assets/examples/auto_submit_rule.java`
- 需要状态命中后创建待办：读取 `assets/examples/todo_notify_rule.java`
- 需要判断当前流程目标是否命中：读取 `assets/examples/route_compute_rule.java`
- 需要给身份匹配返回表达式：读取 `assets/examples/identity_match_rule.java`
- 需要按字段或动作调整权限对象：读取 `assets/examples/privilege_assign_rule.java`
- 需要在一个 CM 服务中调用另一个 CM 服务的保存、提交、详情查询、列表查询或实例化操作：读取 `assets/examples/ncm_data_service_rule.java`
- 需要一套订单类实战组合示例：读取 `assets/examples/order_rule.java`
