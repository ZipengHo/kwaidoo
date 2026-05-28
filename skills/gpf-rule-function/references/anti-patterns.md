# GPF 规则函数常见反例

## 反例 1：让用户显式传环境变量

- 错误：把 `$context$`、`$form$` 这类环境变量写成业务人员必须手填的参数
- 正确：在 `@InputDeclare` 中用 `exampleValue` 声明，调用时不显式传入

## 反例 1.1：把 `$output$` 当成业务输出参数

- 错误：让业务人员在规则表达式里填写 `$output$`，或把 `$output$` 当成规则函数写返回值的容器
- 错误：当前场景没有注入 `$output$` 时，代码里主动创建空 `RpcMap` 继续执行
- 正确：规则函数自身输出用 `return`；`$output$` 只作为系统注入的运行输出对象读取
- 正确：内存流场景下确认 `$output$` 为 `RpcMap<Object>` 后，按节点名称读取前置节点返回值

## 反例 2：把技术参数暴露给业务人员

- 错误：让用户配置 `fieldCode`、`modelId`、`uuid`
- 正确：参数应尽量使用业务字段名称和业务语义

## 反例 3：把 `Form` API 全部写成字段编码

- 错误：`form.getString("ding4Dan1...")`
- 正确：业务读写优先字段名称；查询和界面交互再按需要转编码

## 反例 4：把自动提交或待办通知回调写成普通校验函数

- 错误：只给一段业务逻辑，不说明配置位置和回调参数
- 正确：明确这是配置型规则，并写清触发位置

## 反例 5：把权限矩阵完整逻辑塞进普通规则函数

- 错误：在规则函数技能里展开矩阵引擎全部职责
- 正确：这里只保留身份匹配、动态权限和局部权限控制等权限领域规则函数；完整矩阵引擎设计另行处理

## 反例 6：覆盖已有函数签名

- 错误：直接把模板套到已有规则函数上，改掉原入参与返回
- 正确：修改任务优先保持原契约

## 反例 7：在方法体里直接写表单属性名魔法值

- 错误：`form.getString("订单状态")`、`form.setAttrValue("申请时间", value)`
- 正确：先在类定义上定义 `FIELD_ORDER_STATUS`、`FIELD_APPLY_TIME` 这类常量，再统一使用

## 反例 7.1：为 `Form` 脑补不存在的 `getXXX` 调用

- 错误：`Date purchaseDate = form.getDate(FIELD_PURCHASE_DATE)`
- 错误：`Map<String, String> classTime = form.getKeyValue(FIELD_CLASS_TIME)`
- 正确：规则函数里只要要调用真实 `Form` API，就先回到 `gpf-api-call` 的 `form-capability-matrix.md` 核实方法名
- 说明：`Date` 默认按 `Long` 读取；`KeyValue` 默认用 `getPropTable(...)` 或 `getPropKeyValueMap(...)`，不能自造 `getDate`、`getKeyValue`

## 反例 8：继承了 `CellIntf` 却不用 `cell.` 包名

- 错误：`package example.rule; public interface IOrderRule extends CellIntf {}`
- 正确：`package cell.example.rule; public interface IOrderRule extends CellIntf {}`
- 说明：这不是示例风格问题，而是 Cell 体系的包名约束；只要继承 `CellIntf`，就必须落在 `cell.` 开头的包下

## 反例 9：给规则函数保留多套声明路径

- 错误：一会儿建议 `extends CellIntf`，一会儿又说也可以走普通 `xxx.rule` 接口
- 正确：本技能统一要求所有规则函数都按 `CellIntf` 体系实现，并使用 `cell.{项目名}.rule`
- 说明：规则函数规范越分叉，模型越容易在生成时漂移；技能应收敛为单一路径

## 反例 10：混淆两套路由返回语义

- 错误：把 `RouterOption` 当作 `Pair<Boolean, String>` 的新版本，或返回 `RouterOption` 后仍按单条离开路由继续判断
- 正确：`Pair<Boolean, String>` 用于当前目标路由命中判断；`RouterOption` 用于自主路由接管，一旦返回就接管当前节点所有离开路由规则

## 反例 11：CM服务调用绕过 `NCMDataService`

- 错误：用 `ContextModelMgr.getCachedContextModel(...)` 查目标 CM，再 `context.cloneContext(targetCm)`，手动设置 `DriverDto`、`Methods`、`setDriverName(...)`、`setMethodName(...)`、`setParameterMappings(...)`，最后调用 `executeCmMethod(...)`
- 错误：为了调用目标 CM 的保存操作，直接用 `IFormMgr.updateForm(...)` 或手动执行目标 CM 驱动方法
- 正确：CM 服务调用规则必须组装 `NCMOperationParameter`，把目标表单放入 `ContextSystemVarKey.$form$`，再通过 `NCMDataService.get().internalOpeationCall(operationParameter)` 调用目标 CM 服务操作
- 判断标准：如果代码里没有 `NCMDataService`，或者出现 `ContextModelMgr` / `DriverDto` / `Methods` / `executeCmMethod`，通常说明没有命中 CM服务调用模式
