# GPF 路由矩阵

## 技能与触发词

### `gpf-cloud-cell`

触发词：

- Cell
- ServiceCell
- 资源 Cell
- 异步 Cell
- 云配置
- 云 UDF
- 远程联调

最终产物：

- 一个可复用的 Cell 接口或实现
- 一段围绕 Cell 生命周期、资源或配置的实现代码

### `bapdev-cli`

触发词：

- 命令行
- CLI
- 下载云工程
- 提交云工程
- 发布云工程
- 刷新云工程
- 同步依赖库
- 同步 lib
- 生成令牌
- token
- connect
- relocate
- 本地编译
- fetch-current

最终产物：

- 一次通过命令行执行的云工程操作
- 一套围绕 `.bapdev-cli`、令牌、下载、提交、发布、刷新、同步依赖、重定位、本地编译的命令流程
- 一条或一组需要执行的 `bapdev-cli` 包装脚本命令

### `gpf-api-call`

触发词：

- `IUserMgr`
- `IRoleMgr`
- `IFormMgr`
- 用户管理
- 角色管理
- 表单管理
- `Cnd`
- `ResultSet`

最终产物：

- 一段直接调用 GPF 管理器 API 的代码
- 一段围绕用户、角色、组织、表单、查询分页的数据操作逻辑

### `gpf-http-interface`

触发词：

- `@RequestMapping`
- GET
- POST
- 上传
- 下载
- JWT
- SSE
- 拦截器
- DispatcherMappingBuilder

最终产物：

- 一个对外 HTTP 接口
- 与请求分发、参数绑定、认证、响应处理相关的代码

### `gpf-rule-function`

触发词：

- 规则函数
- `@ClassDeclare`
- `@MethodDeclare`
- 数据校验
- 数据填值
- 数据过滤
- 环境变量
- 提交前校验
- 身份匹配
- 动态权限

最终产物：

- 一个规则函数接口或实现
- 一段在规则运行上下文中执行的代码
- 一段运行在规则环境中的权限控制逻辑

### `gpf-permission-matrix`

触发词：

- 权限矩阵
- 鉴权规则
- 授权规则
- 身份匹配
- 动态授权
- 字段权限
- 按钮权限

最终产物：

- 一段权限模型、身份体系或矩阵授权边界说明
- 一段需要补充身份识别或权限授予语义约束的资料

## 冲突判定

- 如果同时出现“规则函数”和“权限矩阵”，先看最终交付物是否仍是规则函数接口或实现；如果是，主技能优先 `gpf-rule-function`
- 如果需求重点是身份体系、授权范围、矩阵配置或授权边界，优先 `gpf-permission-matrix`
- 如果同时出现“接口”和“管理器 API”，只要最终要对外暴露 URL，优先 `gpf-http-interface`
- 如果同时出现“Cell”和“API 调用”，只要最终交付物是一个长期复用的服务单元，优先 `gpf-cloud-cell`
- 如果需求重点是“如何通过命令行操作云工程”，即使涉及下载源码、同步依赖、发布、刷新、重定位或编译，也优先 `bapdev-cli`
