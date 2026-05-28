# 集群部署说明

本文只用于回答 GPF 集群部署场景，不替代单机最小启动说明。用户一旦明确是集群首领、集群随从、普通成员或代理接入，就应优先读取本文，再结合 `configuration.md` 修改具体文件。

如果用户进一步要求“给出可直接参考的配置模板”，再读取 `cluster-config-examples.md`。

## 角色划分

### 单机节点

- 使用 `starter.xml`
- 典型启动链路：`basic -> crpc -> dao -> webservice -> jetty -> gpf`
- 适用于单实例直接对外提供 Web、RPC 和平台服务

### 集群首领

- 一般仍使用 `starter.xml`
- 典型职责：
  - 对外暴露 RPC 端口
  - 承担集群首领地址
  - 提供数据库主访问能力
  - 为其他随从或成员提供接入基点
- 关键特征：
  - 其他节点的 `tiny.service.chief.uri` 会指向首领
  - 若有 DAO 主从关系，成员节点的 `cdao.cluster.master.uri` 也会指向首领

### 集群随从

- 使用 `agent_starter.xml`
- 典型启动链路：`basic -> agentBasic -> dao -> tinyServiceServant -> webservice -> jetty -> gpfAgent`
- 典型职责：
  - 跟随首领加入集群
  - 提供集群访问相关服务
  - 可作为代理入口承接部分访问或转发能力

### 集群成员

- 使用 `agent_starter.xml`
- 典型启动链路：`basic -> agentBasic -> dao -> tinyServiceMember -> webservice -> jetty -> gpfAgent`
- 典型职责：
  - 作为普通成员加入首领管理的集群
  - 按成员身份暴露自身能力
  - 不承担集群访问协调职责

## 典型部署架构

### 架构一：一主多从

适用于最常见的中心化集群部署。

```text
        用户/外部系统
              |
         HTTP / WS / RPC
              |
         [集群首领节点]
          starter.xml
              |
      ---------------------
      |                   |
[集群随从节点]        [集群成员节点]
agent_starter.xml     agent_starter.xml
  tinyServiceServant   tinyServiceMember
```

特点：

- 首领节点提供统一接入基点
- 随从节点偏向“跟随并提供集群访问相关服务”
- 普通成员节点偏向“加入并提供本节点能力”

### 架构二：首领加多个代理节点

适用于希望将访问入口与平台主节点职责分开时。

```text
        用户/外部系统
              |
        [代理/随从节点]
         gpfAgent + jetty
              |
         [集群首领节点]
          starter.xml
              |
         [其他成员节点]
```

特点：

- 首领负责集群协调和核心服务
- 代理或随从节点负责接入与访问承载
- 成员节点按角色加入集群

## 文件选择规则

- 单机部署或集群首领，优先使用 `starter.xml`
- 集群随从、普通成员、代理接入，优先使用 `agent_starter.xml`
- 若用户说“节点加入已有首领”“副节点接入集群”“代理节点部署”，默认先看 `agent_starter.xml`
- 若用户说“主节点部署”“首领节点初始化”“第一个节点先起来”，默认先看 `starter.xml`

## 关键配置参数释义

以下参数是集群场景下最需要解释的字段。

### `agentBasic`

- `agent.redirect.output`
  - 是否把控制台输出重定向到日志
  - 常用于后台部署，避免日志只留在前台终端

### `dao`

- `cdao.cluster.is.slave`
  - 是否以仆从模式运行 DAO
  - `true` 表示本节点不是 DAO 主节点，需要跟随主节点
  - 集群成员或代理节点通常关注这个值

- `cdao.cluster.master.uri`
  - DAO 主节点地址
  - 当本节点采用仆从模式时，用它指向主节点
  - 该地址通常应与集群首领或数据库主访问节点保持一致

- `base.DBPool.driverName`
  - 数据库驱动类
  - PostgreSQL 通常为 `org.postgresql.Driver`

- `base.DBPool.dbName`
  - JDBC 连接串
  - 指向实际数据库地址、端口和库名

- `base.DBPool.dbuser`
  - 数据库用户名

- `base.DBPool.dbpasswd`
  - 数据库密码

### `tinyServiceServant`

- `tiny.service.chief.uri`
  - 集群首领地址
  - 随从节点通过它找到首领并接入集群

- `tiny.service.my.uri`
  - 当前随从节点对外声明的地址
  - 必须是其他节点可访问到的本机地址，不能随意写 `127.0.0.1`

适用判断：

- 如果节点职责是“跟随首领并提供集群访问相关服务”，使用这一组

### `tinyServiceMember`

- `tiny.service.chief.uri`
  - 集群首领地址
  - 普通成员通过它加入集群

- `tiny.service.my.uri`
  - 当前成员节点对外声明的地址
  - 必须写成本节点真实可达地址

适用判断：

- 如果节点职责只是“作为普通成员加入集群”，使用这一组

### `jetty`

- `jetty.http.port`
  - 当前节点的 Web 端口
  - 集群中每个节点都必须避免端口冲突

### `gpfAgent`

- `web.access.whitelist`
  - Web 访问白名单
  - 当节点作为代理或对外入口时，经常需要联动配置

## 参数联动关系

集群配置最容易出错的不是单个字段，而是成组联动。

### 地址联动

- `tiny.service.chief.uri` 必须统一指向首领地址
- `tiny.service.my.uri` 必须写当前节点自身可达地址
- `cdao.cluster.master.uri` 必须指向 DAO 主节点

如果这三类地址混写成同一个值，或者误写为本地回环地址，集群通常无法正常加入。

### 角色联动

- 使用 `tinyServiceServant` 时，表示节点是“随从/代理型节点”
- 使用 `tinyServiceMember` 时，表示节点是“普通成员节点”
- 不要在同一个节点上同时启用两种互斥角色，除非用户明确给出现场约束并已验证

### 数据库联动

- 集群节点即使不承担首领职责，也常需要正确配置数据库连接
- 仆从模式、主节点地址、数据库连接串必须一起核对

## 回答集群部署问题时的最小输出

当用户询问“集群怎么部署”时，输出至少应覆盖：

- 当前节点角色：首领、随从、成员或代理
- 应修改的文件：`starter.xml` 或 `agent_starter.xml`
- 本节点在集群中的上下游关系
- 关键地址参数分别指向谁
- 需要联动核对的数据库、HTTP、RPC 或集群地址
- 验证方式：节点启动、端口监听、成员加入情况

## 常见误区

- 把集群成员仍按单机 `starter.xml` 处理
- 把 `tiny.service.my.uri` 写成 `127.0.0.1`
- 首领地址、主节点地址、本机地址三者混淆
- 在没有确认角色的情况下同时启用 `tinyServiceServant` 和 `tinyServiceMember`
- 只改集群地址，不核对数据库仆从模式和主节点地址
