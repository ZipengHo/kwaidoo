---
name: gpf-http-interface
description: GPF HTTP 接口开发技能。用于实现和修改 RequestMapping 接口、请求映射实现类、DispatcherMappingBuilder、JWT、SSE、上传下载和常见 HTTP 排障逻辑；适用于最终交付物是 HTTP 接口的任务，不适用于规则函数、权限矩阵或纯基础 API 调用任务。
---

# GPF HTTP 接口开发

## 适用范围

适用：

- 新增 GET、POST、上传下载接口
- 参数绑定、统一响应、异常处理
- JWT、SSE、拦截器
- 404、405、400、401、500 排障

不适用：

- 纯 Cell 服务
- 规则函数
- 权限矩阵规则

## 输入契约

至少确认：

- 请求路径和 HTTP 方法
- 入参与返回值
- 是否需要认证、文件处理、分页或 SSE
- 是否已有接口定义、实现类或分发器
- 接口类和分发配置类是否需要补齐 `@ClassDeclare`
- 接口方法参数是否需要在 `@MethodDeclare.inputs` 中完整声明
- 是否需要使用 `$context$`、`$RequestBody$`、路径参数等特殊参数标记

## 执行流程

1. 先读 `references/overview.md` 判断组件组成和任务落点。
2. 再读 `references/core-concepts.md` 确认接口、实现类和分发器三者关系。
3. 如果接口实现内部需要调用 `IUserMgr`、`IRoleMgr`、`IFormMgr` 或操作 `Form`、`TableData`、`AssociationData`、`Cnd`、`ResultSet` 等 GPF API，对应步骤必须加载 `gpf-api-call` 技能，不得只凭本技能资料直接编写 API 调用代码。
4. 生成代码前读 `references/workflow.md` 和 `references/patterns.md`。
5. 如果在排错，优先读 `references/errors.md`。
6. 输出前按 `references/checklist.md` 自检，并参考 `assets/examples/`。

## 输出契约

输出必须包含：

- 路径、方法、参数和返回结构
- 接口定义与实现方式
- `@ClassDeclare`、`@MethodDeclare` 和 `@InputDeclare` 的声明方式
- 如果使用上下文参数，说明 `RequestMappingContext` 的用法
- 如果使用请求处理器、响应处理器或拦截器，说明装配方式和系统默认实现选择依据
- 认证、拦截器或异常处理说明
- 排障要点或风险说明

## 强制规范

- 所有 HTTP 接口定义类都必须继承 `RequestMappingIntf`，不能只声明 `@RequestMapping` 而不继承该接口
- 所有 HTTP 接口类都必须添加 `@ClassDeclare`
- 所有 HTTP 接口方法都必须添加 `@MethodDeclare`
- 方法参数必须在 `@MethodDeclare.inputs` 中使用 `@InputDeclare` 明确声明
- 使用上下文参数时，必须在 `@InputDeclare` 中声明 `exampleValue = "$context$"`
- 使用请求体参数时，必须按约定声明 `exampleValue = "$RequestBody$"`
- 路径统一使用 `@RequestMapping(path = "...", method = ...)`，不要假定存在 `@GetMapping`、`@PostMapping`
- `@RequestMapping` 的 `method` 除非用户或现有源码明确要求限定单一方法，否则默认同时支持 `RequestMethod.GET` 和 `RequestMethod.POST`
- `DispatcherMappingBuilder` 等对外可维护的分发配置类，推荐补齐 `@ClassDeclare`
- 严禁编造工程中已存在类的包路径、接口定义、方法签名、继承关系或返回类型
- 只要输出里引用现有 `RequestMapping`、处理器、拦截器或分发器类，就必须先以当前工程源码或技能内源码快照为准完成核实
- 如果类名能确认但包路径、接口方法或实现细节不能确认，必须明确标注“待从源码核实”，不得补全猜测值
- 只要 HTTP 接口实现里出现真实 GPF 管理器 API 或数据对象操作，必须同时加载 `gpf-api-call` 技能，不能由本技能单独定义 API 调用规则

## 引用导航

- 总览：`references/overview.md`
- 核心概念：`references/core-concepts.md`
- 执行流程：`references/workflow.md`
- 代码模式：`references/patterns.md`
- 常见错误：`references/errors.md`
- 最终检查：`references/checklist.md`
- 资料索引：`references/资料索引.md`
- 源码类索引：`references/源码类索引.md`
- 源码快照：`assets/source/`
- 示例模板：`assets/examples/`
