# 平台配置说明

## 核心配置文件

- `starter.xml`
  - 适用于常规平台启动
  - 默认单 GPF 启动最先看这个文件
  - 默认最小配置只关注数据库连接、HTTP 端口、RPC 端口

- `agent_starter.xml`
  - 适用于集群成员或代理服务启动
  - 属于集群或代理接入场景，不属于默认单 GPF 最小配置

- `base.conf`
  - 用于声明附加配置文件列表
  - 属于附加配置入口，一般启动服务时不需要优先修改

- `base_advance.conf`
  - 用于高级运行参数
  - 属于高级配置，一般启动服务时不需要优先修改

- `conf/web/service.json`
  - 用于 Web 服务请求映射、拦截器、过滤器、跨域和静态资源目录配置
  - 属于按场景进入的高级配置
  - 仅在用户明确修改 Web 服务配置时进入

## 默认单 GPF 最小配置

默认单 GPF 启动，一般只需要关注 `starter.xml` 中这三项：

- 数据库连接
  - `dao` 模块中的 JDBC URL、用户名、密码

- RPC 端口
  - `crpc` 模块中的端口参数

- HTTP 端口
  - `jetty` 模块中的 `jetty.http.port`

如果用户只是要“把服务跑起来”或“给一个最小启动配置”，优先回答这三项，不默认展开其他配置。

## 高级配置入口

以下内容都属于现场定制时才展开的高级配置。

### `starter.xml`

常改项：

- `dao`
  - 数据库驱动、连接串、用户名、密码、连接池
- `jetty`
  - HTTP 端口、静态资源路径、Servlet 路径、Servlet 类
- `webservice`
  - 是否初始化系统配置
- `gpf` 或 `gpfAgent`
  - 平台业务启动参数

适用场景：

- 默认单体平台启动之外的进一步定制
- 调整静态资源入口或平台业务启动参数

### `agent_starter.xml`

常改项：

- `agentBasic`
  - 代理输出日志重定向
- `dao`
  - 仆从模式、主节点地址、数据库连接
- `tinyServiceServant`
  - 作为集群随从接入首领
- `tinyServiceMember`
  - 作为普通成员接入集群

适用场景：

- 平台作为集群成员部署
- 代理服务或多节点部署
- 具体角色划分、架构关系和参数释义，转到 `cluster-deploy.md`

### `base.conf`

常改项：

- 需要加载的附加配置文件列表

适用场景：

- 增加或移除高级配置文件

### `base_advance.conf`

常改项：

- `web.access.whitelist`
- `jetty.serveruri.enable`
- `jetty.serveruri`
- `jetty.ip.serveruri`
- `jetty.https.enable`
- `jetty.https.port`
- `jetty.https.keystore.*`
- `com.bap.nio.wss.enable`
- `com.bap.nio.wss.file.*`
- `agent.redirect.output.rollingMode`

适用场景：

- 调整 JDF 页面使用的 `server_uri.config` 返回地址
- 启用 HTTPS
- 启用 WSS
- 限制 Web 访问白名单
- 调整日志输出行为

重点参数：

- `web.access.whitelist`
  - 不是简单静态白名单
  - 源码会先做页面地址与 websocket 地址的自动放行校验
  - 自动放行失败后，才进入白名单匹配
  - 白名单匹配目标是页面的 `host` 或 `host:port`
  - 详细规则转到 `web-access-whitelist.md`

## 修改原则

- 如果用户没有明确提出现场定制，优先只处理默认单 GPF 最小配置
- 如果用户明确是集群部署，不要继续按默认单 GPF 最小配置回答，应先转到 `cluster-deploy.md`
- 默认在用户提供的现有配置文件上做最小修改
- 涉及启动角色切换时，优先确认应使用 `starter.xml` 还是 `agent_starter.xml`
- 数据库配置优先在 `starter.xml` / `agent_starter.xml` 的 `dao` 模块里修改
- 如果用户明确提到 JDF 页面、`server_uri.config`、Nginx 域名代理或 WebSocket 地址生成，转到 `server-uri-config.md`
- 如果用户明确提到 `web.access.whitelist`、页面未获得访问授权、白名单校验失败，转到 `web-access-whitelist.md`
- HTTPS 与 WSS 经常需要联动调整，不能只改其中一侧
- 修改 `service.json` 时转到 `web-service-config.md`
