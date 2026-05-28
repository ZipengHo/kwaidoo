# 集群配置样例说明

本文给出三种最常见的集群部署样例说明，目标是帮助快速判断当前节点该使用哪个文件、保留哪些配置块、哪些参数必须替换为现场值。

样例基于以下现有文件整理：

- `assets/config-examples/starter.xml`
- `assets/config-examples/agent_starter.xml`

本文不新增技能外事实，只对已有样例做场景化拆解。

## 使用原则

- 集群首领默认参考 `starter.xml`
- 集群随从默认参考 `agent_starter.xml` 中的 `tinyServiceServant`
- 集群成员默认参考 `agent_starter.xml` 中的 `tinyServiceMember`
- 地址类参数一律替换成现场真实可达地址，不要保留样例 IP
- 同一节点不要同时启用 `tinyServiceServant` 和 `tinyServiceMember`

## 场景一：集群首领节点

### 适用场景

- 第一个启动的主节点
- 其他节点都要连接到它
- 它对外提供 RPC 基点和平台核心服务

### 目标文件

- `starter.xml`

### 最小关注项

- `com.leavay.nio.port`
- `base.DBPool.driverName`
- `base.DBPool.dbName`
- `base.DBPool.dbuser`
- `base.DBPool.dbpasswd`
- `jetty.http.port`

### 推荐样例

```xml
<Root>
  <starter key="basic" class="bap.BasicStarter" enable="true"/>

  <starter key="crpc" class="com.leavay.nio.crpc.CRpcStarter" enable="true">
    <depend key="basic"/>
    <param key="com.leavay.nio.port" type="int" value="2020"/>
  </starter>

  <starter key="dao" class="com.leavay.dfc.mgr.starter.CDaoStarter" enable="true">
    <depend key="crpc"/>
    <param key="base.DBPool.driverName" type="string" value="org.postgresql.Driver"/>
    <param key="base.DBPool.dbName" type="string" value="jdbc:postgresql://192.168.0.10:5432/gpfdb"/>
    <param key="base.DBPool.dbuser" type="string" value="postgres"/>
    <param key="base.DBPool.dbpasswd" type="string" value="postgres"/>
  </starter>

  <starter key="webservice" class="web.WebServiceStarter" enable="true">
    <depend key="basic"/>
    <depend key="dao"/>
    <param key="init_system_config" type="boolean" value="false"/>
  </starter>

  <starter key="jetty" class="jetty.emb.JettyStarter" enable="true">
    <depend key="basic"/>
    <depend key="dao"/>
    <param key="jetty.http.port" type="int" value="8090"/>
  </starter>

  <starter key="gpf" class="gpf.GpfStarter" enable="true">
    <depend key="basic"/>
    <depend key="webservice"/>
  </starter>
</Root>
```

### 参数替换规则

- `com.leavay.nio.port`
  - 改成首领节点的 RPC 监听端口
- `base.DBPool.dbName`
  - 改成首领节点连接的 PostgreSQL 地址
- `jetty.http.port`
  - 改成首领节点的 Web 端口

### 说明

- 集群首领一般不需要 `tinyServiceServant` 或 `tinyServiceMember`
- 其他成员的 `tiny.service.chief.uri` 通常会指向 `ws://首领IP:RPC端口`

## 场景二：集群随从节点

### 适用场景

- 节点需要跟随首领
- 节点承担“集群访问相关服务”或代理入口职责
- 现场明确要求使用 `tinyServiceServant`

### 目标文件

- `agent_starter.xml`

### 最小关注项

- `agent.redirect.output`
- `cdao.cluster.is.slave`
- `cdao.cluster.master.uri`
- `base.DBPool.driverName`
- `base.DBPool.dbName`
- `tiny.service.chief.uri`
- `tiny.service.my.uri`
- `jetty.http.port`
- `web.access.whitelist`

### 推荐样例

```xml
<Root>
  <starter key="basic" class="bap.BasicStarter"/>

  <starter key="agentBasic" class="bap.ms.agent.AgentBasicStarter">
    <depend key="basic"/>
    <param key="agent.redirect.output" type="boolean" value="true"/>
  </starter>

  <starter key="dao" class="com.leavay.dfc.mgr.starter.CDaoStarter" enable="true">
    <depend key="basic"/>
    <param key="cdao.cluster.is.slave" type="boolean" value="true"/>
    <param key="cdao.cluster.master.uri" value="ws://192.168.0.10:2020"/>
    <param key="base.DBPool.driverName" type="string" value="org.postgresql.Driver"/>
    <param key="base.DBPool.dbName" type="string" value="jdbc:postgresql://192.168.0.10:5432/gpfdb"/>
  </starter>

  <starter key="tinyServiceServant" class="tiny.service.ServantStarter" enable="true">
    <depend key="dao"/>
    <param key="tiny.service.chief.uri" type="string" value="ws://192.168.0.10:2020"/>
    <param key="tiny.service.my.uri" value="ws://192.168.0.11:2020"/>
  </starter>

  <starter key="jetty" class="jetty.emb.JettyStarter" enable="true">
    <depend key="basic"/>
    <depend key="dao"/>
    <param key="jetty.http.port" type="int" value="8091"/>
  </starter>

  <starter key="webservice" class="web.WebServiceStarter" enable="true">
    <depend key="basic"/>
    <depend key="dao"/>
    <param key="init_system_config" type="boolean" value="false"/>
  </starter>

  <starter key="gpfAgent" class="gpf.GpfAgentStarter" enable="true">
    <depend key="basic"/>
    <depend key="webservice"/>
    <param key="web.access.whitelist" type="string" value=""/>
  </starter>
</Root>
```

### 参数替换规则

- `cdao.cluster.is.slave`
  - 通常保持 `true`
- `cdao.cluster.master.uri`
  - 改成 DAO 主节点地址，通常与首领地址一致
- `tiny.service.chief.uri`
  - 改成首领节点地址
- `tiny.service.my.uri`
  - 改成当前随从节点自己的真实可达地址
- `jetty.http.port`
  - 与同机其他节点错开

### 说明

- 这一场景下保留 `tinyServiceServant`
- 如果现场不是“随从/代理型节点”，不要套这份样例

## 场景三：集群成员节点

### 适用场景

- 节点只是普通成员
- 节点需要加入首领，但不承担随从访问协调职责
- 现场明确要求使用 `tinyServiceMember`

### 目标文件

- `agent_starter.xml`

### 最小关注项

- `agent.redirect.output`
- `cdao.cluster.is.slave`
- `cdao.cluster.master.uri`
- `base.DBPool.driverName`
- `base.DBPool.dbName`
- `tiny.service.chief.uri`
- `tiny.service.my.uri`
- `jetty.http.port`

### 推荐样例

```xml
<Root>
  <starter key="basic" class="bap.BasicStarter"/>

  <starter key="agentBasic" class="bap.ms.agent.AgentBasicStarter">
    <depend key="basic"/>
    <param key="agent.redirect.output" type="boolean" value="true"/>
  </starter>

  <starter key="dao" class="com.leavay.dfc.mgr.starter.CDaoStarter" enable="true">
    <depend key="basic"/>
    <param key="cdao.cluster.is.slave" type="boolean" value="true"/>
    <param key="cdao.cluster.master.uri" value="ws://192.168.0.10:2020"/>
    <param key="base.DBPool.driverName" type="string" value="org.postgresql.Driver"/>
    <param key="base.DBPool.dbName" type="string" value="jdbc:postgresql://192.168.0.10:5432/gpfdb"/>
  </starter>

  <starter key="tinyServiceMember" class="tiny.service.MemberStarter" enable="true">
    <depend key="dao"/>
    <param key="tiny.service.chief.uri" type="string" value="ws://192.168.0.10:2020"/>
    <param key="tiny.service.my.uri" value="ws://192.168.0.12:2020"/>
  </starter>

  <starter key="jetty" class="jetty.emb.JettyStarter" enable="true">
    <depend key="basic"/>
    <depend key="dao"/>
    <param key="jetty.http.port" type="int" value="8092"/>
  </starter>

  <starter key="webservice" class="web.WebServiceStarter" enable="true">
    <depend key="basic"/>
    <depend key="dao"/>
    <param key="init_system_config" type="boolean" value="false"/>
  </starter>

  <starter key="gpfAgent" class="gpf.GpfAgentStarter" enable="true">
    <depend key="basic"/>
    <depend key="webservice"/>
  </starter>
</Root>
```

### 参数替换规则

- `tiny.service.chief.uri`
  - 改成首领节点地址
- `tiny.service.my.uri`
  - 改成当前成员节点自己的真实地址
- `cdao.cluster.master.uri`
  - 改成 DAO 主节点地址

### 说明

- 这一场景下保留 `tinyServiceMember`
- 不要同时保留 `tinyServiceServant`

## 快速判断表

- 如果节点是主节点、第一台节点、其他节点都连它：使用 `starter.xml`
- 如果节点要“跟随首领并提供集群访问相关服务”：使用 `agent_starter.xml` + `tinyServiceServant`
- 如果节点只是“作为普通成员加入”：使用 `agent_starter.xml` + `tinyServiceMember`

## 部署前核对清单

- 首领节点 RPC 端口是否已明确
- 所有 `tiny.service.chief.uri` 是否统一指向首领
- 所有 `tiny.service.my.uri` 是否写成各节点自己的真实地址
- `cdao.cluster.master.uri` 是否指向 DAO 主节点
- 每个节点的 `jetty.http.port` 是否冲突
- 数据库连接串是否指向正确库
