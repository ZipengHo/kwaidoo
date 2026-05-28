---
name: gpf-platform-deploy
description: GPF 平台服务部署技能。用于确认部署环境要求、修改 starter.xml/agent_starter.xml/base.conf/base_advance.conf/conf/web/service.json、指导服务启停以及排查常见部署问题；适用于平台安装部署、运行配置和运维排障，不适用于 HTTP 接口开发、规则函数或 TinyService 业务实现。
---

# GPF 平台服务部署

## 适用范围

适用：

- 确认平台部署环境要求
- 修改平台启动配置文件
- 修改 Web 服务配置文件
- 指导前台启动、后台启动、调试启动
- 指导服务停止
- 排查部署和运行问题

不适用：

- HTTP 接口代码开发
- 规则函数开发
- TinyService 业务接口实现
- 普通 Cell 代码逻辑修改

## 输入契约

至少确认以下信息：

- 当前是单机部署、集群首领还是集群成员
- 当前环境是否满足 `JDK 1.8` 与 `PostgreSQL 9.3 及以上`
- 用户是否已提供现有配置文件
- 目标是环境确认、配置修改、服务启停、服务停止还是故障排查
- 需要修改的目标文件
- 默认单 GPF 启动是否只需调整数据库连接、HTTP 端口、RPC 端口
- 是否明确涉及 HTTPS、WSS、白名单、FilterMapping、静态资源、Web 路由或集群接入等高级场景

如果用户要修改 `conf/web/service.json`，再读取 `references/web-service-config.md` 和 `assets/config-examples/web-service.json`。

如果用户明确提到 JDF 页面、`server_uri.config`、WebSocket 地址生成、Nginx 域名代理，先读取 `references/server-uri-config.md`。

如果用户明确提到 `web.access.whitelist`、页面未获得访问授权、白名单配置，先读取 `references/web-access-whitelist.md`。

如果用户明确是集群首领、集群成员、集群随从、代理节点，或直接询问集群架构/集群参数含义，先读取 `references/cluster-deploy.md`，再按需读取 `references/configuration.md`。

如果用户要求提供集群配置模板、首领/随从/成员示例，读取 `references/cluster-config-examples.md`。

如果用户要处理启动、后台运行或停止服务，再读取 `references/start-stop.md` 和 `assets/start-scripts/`。

如果用户要排查部署故障，优先读取 `references/troubleshooting.md`。

## 执行流程

1. 先读 `references/资料索引.md` 判断任务属于环境、配置、启停还是排障。
2. 环境要求或环境检查任务，读取 `references/environment.md`。
3. 如果任务明确涉及 JDF 页面、`server_uri.config`、WebSocket 地址生成或 Nginx 域名代理，先读取 `references/server-uri-config.md`。
4. 如果任务明确涉及 `web.access.whitelist`、页面未获得访问授权或白名单校验失败，先读取 `references/web-access-whitelist.md`。
5. 如果任务明确是集群部署、集群接入、集群角色选择或集群参数解释，先读取 `references/cluster-deploy.md`。
6. 如果用户要求集群配置模板、参数替换示例或三种角色样例，读取 `references/cluster-config-examples.md`。
7. 平台配置修改任务，若是默认单 GPF 启动，再按“默认单 GPF 最小配置”处理，只关注数据库连接、HTTP 端口、RPC 端口。
8. 只有用户明确提出现场定制场景时，才展开高级配置，并读取 `references/configuration.md` 中对应部分。
9. Web 服务配置修改任务，只有在用户明确涉及 `service.json`、`filterMapping`、路由、拦截器、静态资源等场景时，才额外读取 `references/web-service-config.md`。
10. 服务启停或停止任务，读取 `references/start-stop.md`。
11. 故障排查任务，优先读取 `references/troubleshooting.md`。
12. 需要配置样例时，从 `assets/config-examples/` 取最接近的文件，不依赖技能目录外路径。
13. 修改配置默认基于用户提供的现有文件做最小改动，未要求调整的配置块保持不变。

## 输出契约

输出必须包含：

- 当前任务类型和目标文件
- 环境要求或环境检查结论
- 如果是集群场景，补充当前节点角色、部署架构和关键参数含义
- 修改项或启停/停止步骤
- 影响范围和风险点
- 验证方式
- 如果是排障，给出优先排查顺序

## 强制规范

- 平台部署环境基线固定为 `JDK 1.8` 和 `PostgreSQL 9.3 及以上`
- 默认单 GPF 最小配置只回答和修改数据库连接、HTTP 端口、RPC 端口
- 如果用户明确询问集群部署，不得只给单机最小配置，必须说明节点角色、架构关系和关键参数语义
- HTTPS、WSS、白名单、`filterMapping`、集群接入等都属于高级配置，只有用户提出相关场景时才展开说明或修改
- 修改配置时优先基于用户提供的现有文件增量修改
- 不得擅自改动无关配置项
- 技能正文不内嵌大段配置样例或大表说明，详细内容放在 `references/` 和 `assets/`
- 所有运行所需资料必须保持技能目录内闭包
- 如果用户明确给出类名、路径或配置值，可按用户确认值修改配置
- 如果结构规则不明确，必须先回查技能内参考文件，不得猜测

## 引用导航

- 资料索引：`references/资料索引.md`
- 环境要求：`references/environment.md`
- 配置说明：`references/configuration.md`
- JDF 地址生成：`references/server-uri-config.md`
- Web 白名单：`references/web-access-whitelist.md`
- 集群部署：`references/cluster-deploy.md`
- 集群样例：`references/cluster-config-examples.md`
- 启停说明：`references/start-stop.md`
- Web 服务配置：`references/web-service-config.md`
- 常见问题：`references/troubleshooting.md`
- 源码与事实索引：`references/源码类索引.md`
- 配置样例：`assets/config-examples/`
- 启动脚本样例：`assets/start-scripts/`
