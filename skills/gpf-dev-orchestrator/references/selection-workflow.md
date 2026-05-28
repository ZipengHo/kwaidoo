# GPF 选择流程

## 决策顺序

### 1. 先看最终交付物

- 交付 URL 接口：`gpf-http-interface`
- 交付规则函数：`gpf-rule-function`
- 交付权限矩阵设计或授权边界规则：`gpf-permission-matrix`
- 交付 Cell：`gpf-cloud-cell`
- 交付直接数据操作代码：`gpf-api-call`
- 交付命令行操作步骤、脚本执行或云工程 CLI 工作流：`bapdev-cli`

### 2. 再看运行位置

- 表单提交、流程节点、规则执行环境：更像规则函数
- 请求入口、分发映射、拦截链：更像 HTTP 接口
- 长期驻留服务、缓存、线程池、配置装配：更像 Cell
- 权限识别、权限授予、按钮字段控制但仍在规则执行环境中：主技能仍是规则函数，再补充权限领域资料
- 权限识别、权限授予、按钮字段控制且重点在身份体系、授权范围或矩阵配置：更像权限矩阵扩展资料
- 下载、提交、发布、刷新、同步依赖、生成令牌、重定位、本地编译等命令行云工程操作：更像 `bapdev-cli`

### 3. 最后看核心 API

- `IContext`、`@MethodDeclare`：偏规则函数
- `RequestMappingIntf`、`BasicCell_RequestMapping`：偏 HTTP 接口
- `BasicServiceCell`、`@Config`：偏云开发 Cell
- `IUserMgr`、`IRoleMgr`、`IFormMgr`：偏基础 API 调用
- `IdentifyMatchParam`、`PrivilegeRuleIntf`：说明需求带有权限领域特征；如果最终产物仍是规则函数，则主技能仍是 `gpf-rule-function`
- `download.sh`、`commit.sh`、`publish.sh`、`refresh.sh`、`sync-libs.sh`、`generate-token.sh`：偏 `bapdev-cli`
