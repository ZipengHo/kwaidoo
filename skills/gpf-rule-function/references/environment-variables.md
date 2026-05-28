# GPF 规则函数环境变量说明

本文件只解决三个问题：

- 当前变量是不是系统自动注入的环境变量
- 这个变量在哪类场景下可用
- 写 `@InputDeclare` 时应该如何声明

## 一句话规则

- 环境变量由系统注入，不是业务人员手填参数
- 声明时用 `@InputDeclare(..., exampleValue = "$变量$")`
- `exampleValue` 只用于环境变量；普通业务参数不应填写这个属性
- 变量是否可用强依赖运行场景，不能跨场景乱用

## 按规则类型选环境变量

先按规则类型选变量组合，再补具体声明，不要先把变量堆到方法签名里。

| 规则类型 | 推荐优先变量 | 按需补充变量 | 不应默认带入 |
|------|----------|----------|----------|
| 数据校验 | `$context$`、`$form$` | `$dao$`、`$operator$`、`$progress$` | `$feContext$`、`$ActionParameter$`、`$WfeStepOperator$` |
| 数据填值 | `$IDCRuntimeContext$`、`$form$` | `$dao$`、`$context$` | `$feContext$`、`$FlowDto$` |
| 数据过滤 | `$IDCRuntimeContext$`、`$sysvar_cnd$` | `$rowCodes$`、`$sysvar_pageNo$`、`$sysvar_pageSize$`、`$queryNesting$` | `$ActionParameter$`、`$listener$` |
| SQL 查询 / 统计 | `$IDCRuntimeContext$`、`$sysvar_cnd$`、`$sysvar_pageNo$`、`$sysvar_pageSize$` | `$rowCodes$`、`$ruleNamespace$`、`$env$`、`$includeFields$`、`$excludeFields$` | `$feContext$`、`$currentComponent$` |
| 界面值自动填写 | `$ActionParameter$`、`$form$` | `$feContext$`、`$currentComponent$`、`$listener$`、`$event$` | `$sysvar_cnd$`、`$WfeStepOperator$` |
| 自动提交回调 | `$IDCRuntimeContext$` | `$ruleNamespace$`、`$env$`、`$formSaved$` | `$feContext$`、`$listener$` |
| 待办通知回调 | `$IDCRuntimeContext$`、`$ruleNamespace$` | `$env$`、`$formSaved$` | `$ActionParameter$`、`$sysvar_cnd$` |
| 操作 finally 收尾 | `$context$`、`$afterOperationCallbacks$` | `$dao$`、`$operator$`、`$env$` | `$feContext$`、`$ActionParameter$`、`$sysvar_cnd$` |
| 路由计算 | `$context$`、`$form$` | `$FlowDto$`、`$WfeStepOperator$`、`$NodeTriggerEvent$`、`$output$` | `$feContext$`、`$SelectEditorQuerier$` |
| 身份匹配 | `$env$`、`$identifyRuleMode$` | `$dao$`、`$operator$`、`$userModelId$`、`$orgModelId$` | `$context$`、`$ActionParameter$`、`$sysvar_pageNo$` |
| 动态权限辅助 | `$env$` | `$form$`、`$operator$`、`$ruleNamespace$` | `$FlowDto$`、`$SelectEditorQuerier$` |

### 快速判断

- 规则运行在后端数据侧：优先考虑 `$IDCRuntimeContext$`
- 规则运行在前端界面事件侧：优先考虑 `$ActionParameter$` 和 `$feContext$`
- 规则需要参与流程节点或路由：优先考虑 `$context$`、`$FlowDto$`、`$WfeStepOperator$`
- 规则需要读取前置流程节点或内存流节点结果：按需补充 `$output$`
- 规则要处理列表查询：优先考虑 `$sysvar_cnd$`、`$sysvar_pageNo$`、`$sysvar_pageSize$`
- 如果规则针对一批表单编号做批量处理、过滤或统计：优先考虑 `$rowCodes$`
- 规则只做当前权限对象调整：优先考虑 `$env$`

## 声明写法

```java
@InputDeclare(desc = "运行上下文", name = "context", label = "运行上下文", exampleValue = "$context$")
@InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$")
@InputDeclare(desc = "动作参数", name = "input", label = "动作参数", exampleValue = "$ActionParameter$")
```

注意：

- `name` 是代码里的参数名
- `label` 是给配置者看的名称
- `exampleValue` 才用于声明这是环境变量
- 调用规则函数时不要再要求业务侧显式传这些值
- 如果参数是业务人员配置的普通参数，例如“订单表单”“状态字段”“编号前缀”，不要写 `exampleValue`
- 错误示例：`@InputDeclare(desc = "订单表单", name = "form", label = "订单表单", exampleValue = "订单表单")`
- 正确做法：`@InputDeclare(desc = "订单表单", name = "form", label = "订单表单")`

## 核心上下文变量

### `$context$`

- 类型：`IContext`
- 场景：通用规则函数、流程规则、身份匹配、路由计算
- 用途：拿 `dao`、`operator`、`progress` 等运行上下文

常见写法：

```java
IDao dao = context.getDao();
User operator = context.getOperator();
Progress progress = context.getProgress();
```

### `$form$`

- 类型：`Form`
- 场景：数据校验、数据填值、界面联动、路由判断
- 用途：读取当前业务表单

常见写法：

```java
String status = form.getString("订单状态");
Long amount = form.getLong("订单金额");
form.setAttrValue("审批意见", "同意");
```

### `$rowCodes$`

- 类型：`List<String>`
- 场景：批量处理表单、按编号过滤、基于编号集合做统计或导出
- 用途：直接获取当前上下文中的表单编号集合

常见写法：

```java
if (rowCodes == null || rowCodes.isEmpty()) {
    return;
}

for (String code : rowCodes) {
    System.out.println(code);
}
```

注意：

- `$rowCodes$` 表示表单编号集合，不是表单模型 ID 集合。
- 如果同时还需要表单模型 ID，应配合 `$IDCRuntimeContext$` 使用。
- 不要把 `$rowCodes$` 误写成普通业务参数让用户手工传入。

### `$dao$`

- 类型：`IDao`
- 场景：需要直接查库、创建或更新表单
- 用途：调用管理器 API 时复用当前事务上下文

常见写法：

```java
Form form = IFormMgr.get().queryForm(dao, modelId, uuid, true);
form = IFormMgr.get().updateForm(null, dao, form, null);
dao.commit();
```

### `$progress$`

- 类型：`Progress`
- 场景：耗时较长的批处理、迁移、统计、导出类规则
- 用途：输出执行进度和取消检查

常见写法：

```java
progress.info("开始处理", true);
progress.sendProcess(50, "处理中", true);
progress.assertCancel();
progress.finish();
```

### `$operator$`

- 类型：`User`
- 场景：当前操作人参与判断时
- 用途：直接拿当前用户，不必再从 `context` 二次拆取

常见写法：

```java
String userCode = operator.getCode();
String userName = operator.getUsername();
```

### `$output$`

- 类型：`Object`
- 场景：多个规则节点之间传递结果，尤其是内存流节点
- 用途：读取规则执行过程中已经产生的输出结果，典型场景是后续节点读取前置节点返回值

语义：

- `$output$` 是系统注入的运行输出对象，不是业务人员手填参数。
- 规则函数自己的结果仍然通过方法 `return` 返回，不要把 `$output$` 当成“写返回值”的参数。
- 普通规则链场景下只能按 `Object` 读取，实际类型由执行器决定。
- 内存流节点场景下通常是 `RpcMap<Object>`，`key` 是节点名称，`value` 是对应节点规则函数的返回值。
- 只有当前运行场景确实会注入运行输出时才声明或读取 `$output$`，不要为了防空而构造空 `RpcMap` 掩盖场景配置问题。

常见写法：

```java
Object previousOutput = ContextSystemVarKey.$output$.getContextValue(context);
```

内存流中按节点名称读取：

```java
RpcMap<Object> output = (RpcMap<Object>) ContextSystemVarKey.$output$.getContextValue(context);
Object nodeResult = output.get(nodeName);
```

读取上一流程节点输出：

```java
RpcMap<Object> output = (RpcMap<Object>) ContextSystemVarKey.$output$.getContextValue(context);

WfeStepOperator stepOp = WfeContextSystemVarKey.$WfeStepOperator$.getContextValue(context);
FlowDto flow = WfeContextSystemVarKey.$FlowDto$.getContextValue(context);

String lastStepKey = stepOp.getCurrStep().getLastStep();
FlowNodeDto lastNode = flow.getNodeKeyMap().get(lastStepKey);
Object lastNodeResult = output.get(lastNode.getName());
```

声明方式：

```java
@InputDeclare(desc = "运行输出", name = "output", label = "运行输出", exampleValue = "$output$")
Object output
```

如果函数只面向内存流节点，并且当前平台场景已确认 `$output$` 为节点输出集合，可以声明为：

```java
@InputDeclare(desc = "运行输出", name = "output", label = "运行输出", exampleValue = "$output$")
RpcMap<Object> output
```

注意：

- 对业务人员暴露的参数应是“节点名称”“是否读取上一节点”等业务可理解语义，不要要求业务人员填写 `$output$`。
- 需要读取上一节点结果时，优先通过 `$WfeStepOperator$` 和 `$FlowDto$` 推导上一节点，再用节点名称读取输出。
- 文档示例中的 `RpcMap` 来自 `com.leavay.nio.crpc.RpcMap`。

### `$ruleNamespace$`

- 类型：`Set<String>`
- 场景：规则函数内部继续调用其他规则函数
- 用途：带着当前规则命名空间继续执行表达式

常见写法：

```java
IExpressionMgr.get().execute(ruleNamespace, envMap, rule);
```

### `$env$`

- 类型：`Map<String, Object>`
- 场景：动态权限辅助、规则链协作、临时数据传递
- 用途：在规则执行时传递和共享运行环境数据

常见写法：

```java
env.put("tempResult", result);
Object tempResult = env.get("tempResult");
```

### `$sessionInfo$`

- 类型：`AppUserInfo`
- 场景：HTTP 会话相关规则
- 用途：读取应用、用户和会话身份信息

常见写法：

```java
String appCode = sessionInfo.getAppCode();
String userId = sessionInfo.getUserId();
```

注意：

- 只有在会话环境下才考虑声明
- 如果当前任务不是会话侧规则，不要默认带上它

### `$exception$`

- 类型：`Throwable`
- 场景：异常处理或失败回调规则
- 用途：拿异常信息做补偿、告警或日志输出

常见写法：

```java
if (exception != null) {
    String errorMsg = exception.getMessage();
}
```

### `$formSaved$`

- 类型：`Boolean`
- 场景：保存后回调、需要判断表单是否已持久化
- 用途：在后续流程中判断是否已经完成保存

常见写法：

```java
if (Boolean.TRUE.equals(formSaved)) {
    // 执行保存后的后续处理
}
```

### `$afterOperationCallbacks$`

- 类型：`List<IOperationExecuteCallback>`
- 场景：操作执行结束后需要在 `finally` 阶段统一收尾
- 用途：注册操作后置回调，常用于“加锁 + finally 回调释放锁”、审计日志、消息通知、资源清理、`ThreadLocal` 变量设置与清理等必须执行的收尾动作

语义：

- `$afterOperationCallbacks$` 是系统注入的回调列表，不是业务人员手填参数。
- 回调由执行器在规则或流程执行的 `finally` 阶段统一触发。
- 回调入参中的 `result` 是规则或流程最终返回值，可能为 `null`。
- 回调入参中的 `error` 是执行过程中捕获的异常，正常结束时为 `null`。
- 注册回调时只追加必要的收尾逻辑，不要在回调里重新发起主业务操作。
- 需要在主操作结束后做 `ThreadLocal` 清理时，也应挂在这里，避免线程复用带来脏数据残留。

常见写法：

```java
List<IOperationExecuteCallback> afterCallbacks = ContextSystemVarKey.$afterOperationCallbacks$.getContextValue(ctx);
afterCallbacks.add((context, result, error) -> {
    if (error != null) {
        // 异常收尾
    } else {
        // 正常收尾：审计日志、通知、资源清理等
    }
});
```

经典加锁释放锁示例：

```java
String lockKey = "业务标识锁";
long timeOut = 60 * 1000L;

cell.cdao.ILock.get().lockKey(lockKey, timeOut);

List<IOperationExecuteCallback> afterCallbacks = ContextSystemVarKey.$afterOperationCallbacks$.getContextValue(ctx);
afterCallbacks.add((context, result, error) -> {
    if (error == null) {
        IDao dao = ContextSystemVarKey.$dao$.getContextValue(context);
        dao.commit();
    }
    // 这里可以做 ThreadLocal 清理、日志、通知等收尾动作
    boolean force = false;
    cell.cdao.ILock.get().unlock(lockKey, force);
});
```

ThreadLocal 设置与清理示例：

```java
ThreadLocal<String> holder = new ThreadLocal<>();
holder.set("临时值");

List<IOperationExecuteCallback> afterCallbacks = ContextSystemVarKey.$afterOperationCallbacks$.getContextValue(ctx);
afterCallbacks.add((context, result, error) -> {
    try {
        // 使用 ThreadLocal 中保存的临时值
    } finally {
        holder.remove();
    }
});
```

声明方式：

```java
@InputDeclare(desc = "操作后置回调列表", name = "afterCallbacks", label = "操作后置回调列表", exampleValue = "$afterOperationCallbacks$")
List<IOperationExecuteCallback> afterCallbacks
```

注意：

- 如果方法已经声明了 `$afterOperationCallbacks$` 入参，优先直接使用该入参追加回调；如果只拿到了 `$context$`，再通过 `ContextSystemVarKey.$afterOperationCallbacks$.getContextValue(ctx)` 获取。
- 回调用于保证收尾动作随主操作生命周期执行，不应被设计成业务人员需要手工触发的普通规则函数。
- 如果锁住的是需要提交事务后才能对外可见的持久化内容，应当在 `error == null` 时先 `dao.commit()`，再释放锁。
- 相关接口源码快照见 `assets/source/OctoCM/src/core/cell/octo/cm/adapter/IOperationExecuteCallback.java`。

### `$applicationSetting$`

- 类型：`ApplicationSetting`
- 场景：需要读取当前应用配置时
- 用途：获取用户模型、组织模型、应用标识等配置

常见写法：

```java
String userModelId = applicationSetting.getUserModelId();
String orgModelId = applicationSetting.getOrgModelId();
```

### `$userModelId$`

- 类型：`String`
- 场景：用户相关查询
- 用途：不再手工猜用户模型 ID

### `$orgModelId$`

- 类型：`String`
- 场景：组织相关查询
- 用途：不再手工猜组织模型 ID

## GPF 运行时变量

### `$IDCRuntimeContext$`

- 类型：`IDCRuntimeContext`
- 场景：GPF 后端数据规则、自动提交、待办通知、SQL 查询
- 用途：拿当前运行时、DAO、表单模型、回调配置

常见写法：

```java
String modelId = rtx.getPdfUuid();
IDao dao = rtx.getDao();
rtx.setAutoSubmitCallback(IBasicAutoSubmitCallback.class);
```

如果需要从上下文中获取当前表单的模型 ID 或流程的模型 ID，推荐写法：

```java
IDCRuntimeContext dcRtx = GpfContextSystemVarKey.$IDCRuntimeContext$.getContextValue(context);
String formModelId = dcRtx.getPdfUuid();
```

注意：

- 当前没有“表单模型 ID”这类可直接映射注入的独立环境变量可用。
- 需要模型 ID 时，应先声明 `$IDCRuntimeContext$`，再通过 `getPdfUuid()` 获取。
- `getPdfUuid()` 同时适用于表单模型 ID 和流程模型 ID 场景。

### `$operatorCode$`

- 类型：`String`
- 场景：只需要当前操作人编码时
- 用途：比拿完整 `User` 对象更轻量

### `$ActionParameter$`

- 类型：`BaseFeActionParameter`
- 场景：前端界面事件、字段值变化、按钮动作
- 用途：拿面板上下文、当前组件、监听器、事件信息

常见写法：

```java
PanelContext panelContext = input.getPanelContext();
AbsComponent currentComponent = input.getCurrentComponent();
ListenerDto listener = input.getListener();
FeCmnEvent event = input.getEvent();
```

## 前端界面变量（JDF）

以下变量仅在前端界面规则中考虑。

### `$feContext$`

- 类型：`PanelContext`
- 场景：前端回调、提示消息、面板上下文相关操作
- 用途：获取当前用户、面板 ID、信道并向前端回调

常见写法：

```java
String userCode = feContext.getCurrentUser();
String panelId = feContext.getCurrentPanelWidgetId();
IWsCallbackChannel channel = feContext.getChannel();
```

### `$currentComponent$`

- 类型：`AbsComponent`
- 场景：当前组件行为依赖组件类型或参数
- 用途：读取当前组件元数据

### `$feAppContext$`

- 类型：`Context`
- 场景：需要读取前端应用级缓存或页面入口参数
- 用途：访问应用配置、缓存、URL 参数

### `$listener$`

- 类型：`ListenerDto`
- 场景：行点击、值变化、监听器触发类规则
- 用途：获取监听器携带的数据

### `$event$`

- 类型：`EventDto`
- 场景：基于事件源、命令或入参做分支判断
- 用途：获取事件来源、命令、识别码和参数

### `$SelectEditorQuerier$`

- 类型：`SelectEditorQuerier`
- 场景：下拉列表动态查询
- 用途：获取关键字、编辑器配置、显示字段

## 查询参数变量

这类变量主要出现在自定义查询、SQL 统计、列表页规则中。

### `$sysvar_cnd$`

- 类型：`Cnd`
- 场景：自定义查询、数据过滤、复杂列表查询
- 用途：接收系统组装好的原始查询条件

### `$sysvar_pageNo$`

- 类型：`Integer`
- 场景：分页查询
- 用途：当前页码

### `$sysvar_pageSize$`

- 类型：`Integer`
- 场景：分页查询
- 用途：每页条数

### `$queryNesting$`

- 类型：`Boolean`
- 场景：列表查询是否需要关联/嵌套数据
- 用途：按需控制查询深度

### `$includeFields$`

- 类型：`List<String>`
- 场景：字段裁剪查询
- 用途：只返回指定字段

注意：

- 列表值是字段名称，不是字段编码

### `$excludeFields$`

- 类型：`List<String>`
- 场景：字段裁剪查询
- 用途：排除部分字段

注意：

- 列表值是字段名称，不是字段编码

## 流程引擎变量（WFE）

以下变量只在流程引擎规则中考虑。

### `$FlowDto$`

- 类型：`FlowDto`
- 场景：流程节点、路由、流程干预
- 用途：读取流程定义、节点、连线、路由信息

### `$FlowBehavior$`

- 类型：`FlowBehavior`
- 场景：需要干预流程整体行为时

### `$RouterOption$`

- 类型：`Map<String, RouterOption>`，其中 `RouterOption` 的真实包名为 `gpf.dto.cfg.runtime.RouterOption`
- 场景：动态计算可选路由，或在路由计算规则中返回 `RouterOption` 接管当前节点所有离开路由
- 语义：
  - 返回 `Pair<Boolean, String>` 时，规则只参与当前目标路由的命中判断
  - 返回 `RouterOption` 时，规则进入自主路由模式，当前节点所有离开路由规则不再继续按 `Pair<Boolean, String>` 判断
  - `goNextAll` 优先级高于 `nexts`

### `$WfeStepOperator$`

- 类型：`WfeStepOperator`
- 场景：需要拿当前节点、上一节点、步骤状态

### `$NodeTriggerEvent$`

- 类型：`String`
- 场景：同一个规则在不同节点触发时机共用
- 用途：判断当前是启动前、提交后、异常通知等哪个时机

### `$DefaultFlowBehavior$`

- 类型：`FlowBehavior`
- 场景：需要回退到默认流程行为

### `$DefaultNodeBehavior$`

- 类型：`NodeBehavior`
- 场景：需要回退到默认节点行为

## 选用建议

### 数据校验 / 数据填值

优先考虑：

- `$context$`
- `$form$`
- `$dao$`

### 界面联动 / 值变化事件

优先考虑：

- `$ActionParameter$`
- `$form$`
- `$feContext$`

### SQL 查询 / 列表过滤

优先考虑：

- `$IDCRuntimeContext$`
- `$sysvar_cnd$`
- `$sysvar_pageNo$`
- `$sysvar_pageSize$`

### 自动提交 / 待办通知回调

优先考虑：

- `$IDCRuntimeContext$`
- `$ruleNamespace$`

### 操作 finally 收尾

优先考虑：

- `$context$`
- `$afterOperationCallbacks$`

### 路由计算 / 流程规则

优先考虑：

- `$context$`
- `$form$`
- `$FlowDto$`
- `$WfeStepOperator$`

### 动态权限辅助

优先考虑：

- `$env$`
- `$form$`
- `$operator$`

## 推荐声明模板

### 数据校验

```java
inputs = {
    @InputDeclare(desc = "运行上下文", name = "context", label = "运行上下文", exampleValue = "$context$"),
    @InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$")
}
```

### 数据过滤 / SQL 查询

```java
inputs = {
    @InputDeclare(desc = "运行时上下文", name = "rtx", label = "运行时上下文", exampleValue = "$IDCRuntimeContext$"),
    @InputDeclare(desc = "查询条件", name = "cnd", label = "查询条件", exampleValue = "$sysvar_cnd$"),
    @InputDeclare(desc = "页码", name = "pageNo", label = "页码", exampleValue = "$sysvar_pageNo$"),
    @InputDeclare(desc = "每页数量", name = "pageSize", label = "每页数量", exampleValue = "$sysvar_pageSize$")
}
```

### 界面值自动填写

```java
inputs = {
    @InputDeclare(desc = "动作参数", name = "input", label = "动作参数", exampleValue = "$ActionParameter$"),
    @InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$")
}
```

### 路由计算

```java
inputs = {
    @InputDeclare(desc = "运行上下文", name = "context", label = "运行上下文", exampleValue = "$context$"),
    @InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$")
}
```

如果路由规则需要基于流程引擎上下文自主接管当前节点全部离开路由，可按实际工程规则声明流程变量：

```java
inputs = {
    @InputDeclare(desc = "运行上下文", name = "context", label = "运行上下文", exampleValue = "$context$"),
    @InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$"),
    @InputDeclare(desc = "流程定义", name = "flowDto", label = "流程定义", exampleValue = "$FlowDto$")
}
```

### 动态权限辅助

```java
inputs = {
    @InputDeclare(desc = "规则运行环境", name = "env", label = "规则运行环境", exampleValue = "$env$")
}
```

## 常见误写

- 把 `$context$`、`$form$` 当作业务参数要求手工输入
- 在后端数据规则里声明 `$feContext$`
- 在前端界面联动里声明 `$WfeStepOperator$`
- 查询规则忘了声明 `$sysvar_cnd$`、`$sysvar_pageNo$`、`$sysvar_pageSize$`
- 把 `$includeFields$`、`$excludeFields$` 当字段编码列表使用
- 未确认运行场景就一次性把所有变量都挂到方法签名上
