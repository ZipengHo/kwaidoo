# GPF 规则函数执行流程

## 新增规则函数

1. 判断是校验、填值、过滤还是回调
2. 明确触发场景
3. 先核对用户提供的表单元数据，确认有哪些真实字段、状态、模型信息和业务属性
4. 如果缺失必要元数据，先停下来向用户确认，不得自行补字段
5. 设计业务参数
6. 确定需要哪些环境变量
7. 补齐类级和方法级声明
8. 输出使用方式和限制

## CM服务调用规则

1. 先确认这是“一个 CM 服务调用另一个 CM 服务”的操作，而不是普通表单 CRUD。
2. 读取 `assets/examples/ncm_data_service_rule.java`，按该样例组织代码。
3. 目标 CM 操作必须通过 `NCMDataService.get().internalOpeationCall(...)` 调用。
4. 组装 `NCMOperationParameter`，从当前 `$context$` / `$ruleNamespace$` 推导业务域，设置目标 CM 名称和内部操作名。
5. 如果要把当前表单或关联表单交给目标 CM 操作，放入 `ContextSystemVarKey.$form$`。
6. 不要使用 `ContextModelMgr`、`DriverDto`、`Methods`、`cloneContext(targetCm)` 或 `executeCmMethod(...)` 手动执行目标 CM 方法。
7. 不要用 `IFormMgr` 直接查询或保存目标表单来替代目标 CM 服务操作。

## 修改现有规则函数

1. 先保留原有签名
2. 先核对当前逻辑依赖的表单元数据是否来自用户提供信息或现有代码事实
3. 如果缺失必要元数据，先停下来向用户确认，不得补造字段
4. 判断问题在注解、环境变量还是业务逻辑
5. 最小改动修复

## 排障

1. 先查注解是否完整
2. 再查环境变量是否声明正确
3. 再查字段和业务参数是否混用
4. 再查是否误用了用户未提供的表单元数据
