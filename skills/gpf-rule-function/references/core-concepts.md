# GPF 规则函数核心概念

## 注解

规则函数至少要关注：

- `@ClassDeclare`
- `@MethodDeclare`
- `@InputDeclare`

类级声明负责说明是什么、为什么、怎么用。
方法级声明负责说明方法用途和输入。

## 包名与接口声明

- 本技能中的规则函数统一属于 Cell 体系，不提供第二套声明方式
- 规则函数接口必须继承 `CellIntf`
- 规则函数包名必须以 `cell.` 开头
- 项目后台规则函数默认放在 `cell.{项目名}.rule`
- 不要把“示例包名”误解成可选风格；这里是强约束，不是展示写法
- 不要输出“也可以不继承 `CellIntf`”或“也可以放在普通 `xxx.rule` 包下”这类建议

最小正确形态：

```java
package cell.example.rule;

public interface IUserRegisterRule extends CellIntf {
}
```

错误形态：

```java
package example.rule;

public interface IUserRegisterRule extends CellIntf {
}
```

错误原因：

- 包名不符合 Cell 体系约束
- 给规则函数引入了不必要的多种声明路径
- 会让技能使用者误以为规则函数存在多套并行规范

## 业务参数与环境变量

- 业务参数面向业务人员配置
- 环境变量由系统自动注入
- 以 `$` 包裹的示例值通常表示环境变量
- 不同运行环境可用变量不同，不能把前端变量、流程变量、查询变量混着声明

常见环境变量分组：

- 通用上下文：`$context$`、`$form$`、`$dao$`、`$operator$`、`$progress$`
- 规则运行链路：`$output$`、`$ruleNamespace$`、`$env$`
- GPF 运行时：`$IDCRuntimeContext$`
- 前端界面：`$ActionParameter$`、`$feContext$`、`$currentComponent$`、`$listener$`、`$event$`
- 查询参数：`$sysvar_cnd$`、`$sysvar_pageNo$`、`$sysvar_pageSize$`、`$queryNesting$`
- 流程引擎：`$FlowDto$`、`$WfeStepOperator$`、`$NodeTriggerEvent$`

继续判断变量类型、适用场景和声明方式时，读取 `environment-variables.md`。

其中 `$output$` 是运行输出对象，只用于读取规则链、流程节点或内存流中已经产生的结果。规则函数自身仍通过 `return` 产生输出，不要把 `$output$` 设计成业务人员需要填写的普通入参；在内存流场景下它通常是 `RpcMap<Object>`，按节点名称读取节点返回值。

### 关于表单模型 ID / 流程模型 ID

- 规则函数中如果需要拿当前上下文里的表单模型 ID，不要把“表单模型 ID”当成一个可直接映射注入的独立环境变量。
- 这类信息应先拿 `$IDCRuntimeContext$`，再从运行时对象中获取。
- 当前常用方式是：

```java
IDCRuntimeContext dcRtx = GpfContextSystemVarKey.$IDCRuntimeContext$.getContextValue(context);
String formModelId = dcRtx.getPdfUuid();
```

说明：

- `dcRtx.getPdfUuid()` 既可用于获取表单的模型 ID，也可用于获取流程的模型 ID。
- 因此在设计规则函数参数时，不要额外声明一个“表单模型 ID 环境变量”并假设系统会自动注入。

## 字段名称与字段编码

- 业务描述层优先说字段名称
- 真正操作表单或查询时，要明确是否需要字段编码
- 字段不确定时不要猜

### 字段规则

- `Form` API 默认优先字段名称，例如 `form.getString("订单状态")`
- 如果涉及真实 `Form` API 的方法名判断，先看 `gpf-api-call` 技能中的 `form-capability-matrix.md`
- 界面交互 API 常先把字段名称转为字段编码再传给组件
- SQL 和 `Cnd` 构造通常要先把业务字段名称转成字段编码
- 不要把技术参数如 `fieldCode` 直接暴露给业务人员配置
