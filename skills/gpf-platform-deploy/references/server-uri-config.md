# `server_uri.config` 决策树

用途：

- JDF 页面会请求 `server_uri.config`
- 返回值是前端要连接的 WebSocket 地址
- 返回错了，页面可能能打开，但登录、实时通信、消息推送会异常

事实来源：

- `工程源码/cmn_util/src/core/cell/cmn/servlet/ICmnServlet.java`

## 一句话结论

- 域名访问：优先看 `jetty.serveruri`，没配就按“域名 + 父路径 + /websocket”拼
- IP 访问：优先看 `jetty.ip.serveruri`，没配就按“IP + RPC端口”拼
- HTTPS 或启用 WSS：前缀优先变成 `wss://`

## 触发条件

只有同时满足下面两个条件，才会动态生成 `server_uri.config`：

- 请求路径以 `server_uri.config` 结尾
- `jetty.serveruri.enable=true`

## 决策树

```text
请求 server_uri.config
|
+- jetty.serveruri.enable = false ?
|  |
|  +- 是 -> 不走动态生成逻辑
|  |
|  +- 否 -> 继续
|
+- 取协议 scheme
|  |
|  +- 先看 X-Forwarded-Proto
|  |
|  +- 没有就看 request.getScheme()
|  |
|  +- scheme=https 或 com.bap.nio.wss.enable=true ?
|     |
|     +- 是 -> wsPrefix = wss://
|     |
|     +- 否 -> wsPrefix = ws://
|
+- 取路径 path
|  |
|  +- 先看 X-Original-URI
|  |
|  +- 没有就看 request.getRequestURI()
|  |
|  +- parentPath = path 去掉 /server_uri.config
|
+- 取 host
|  |
|  +- 初始值：request.getRemoteHost()
|  |
|  +- 如果有 Referer：host 改成 Referer 里的 host
|  |                 remotePort 改成 Referer 里的 port
|
+- host 是域名？
   |
   +- 是
   |  |
   |  +- jetty.serveruri 已配置？
   |  |  |
   |  |  +- 是 -> 直接返回 jetty.serveruri
   |  |  |
   |  |  +- 否 -> 返回 wsPrefix + host + parentPath + /websocket
   |
   +- 否，host 是 IP
      |
      +- jetty.ip.serveruri 已配置？
      |  |
      |  +- 是 -> 直接返回 jetty.ip.serveruri
      |  |
      |  +- 否 -> 继续
      |
      +- httpPort == remotePort ?
      |  |
      |  +- 是 -> 返回 wsPrefix + host + : + rpcPort
      |  |
      |  +- 否 -> 继续
      |
      +- host 命中本机网卡 IP ?
      |  |
      |  +- 是 -> 返回 wsPrefix + host + : + rpcPort
      |  |
      |  +- 否 -> 返回 wsPrefix + localhost + : + rpcPort
```

## 最关键的几个输入

- `X-Forwarded-Proto`
  - 决定返回 `ws://` 还是 `wss://`

- `X-Original-URI`
  - 决定默认拼接时用哪个父路径

- `Referer`
  - 会覆盖 host 判断

- `jetty.serveruri`
  - 域名访问时的最高优先级返回值

- `jetty.ip.serveruri`
  - IP 访问时的最高优先级返回值

- `com.bap.nio.wss.enable`
  - 也会影响前缀是否变成 `wss://`

## LLM 回答用户时的最短规则

可以直接按下面规则回答：

1. 先判断用户是域名访问还是 IP 访问
2. 域名访问先看 `jetty.serveruri`
3. IP 访问先看 `jetty.ip.serveruri`
4. 两边都没配时，再走默认拼接
5. 外部是 HTTPS 或启用 WSS 时，结果应该是 `wss://`
6. 经过 Nginx 时，重点检查 `X-Forwarded-Proto`、`X-Original-URI`、`/websocket` 代理

## 域名访问的默认结果

当满足以下条件：

- 通过域名访问
- 没有配置 `jetty.serveruri`

默认返回：

```text
wsPrefix + 域名 + 父路径 + /websocket
```

例子：

```text
请求地址: https://gpf.example.com/jdf/server_uri.config
返回地址: wss://gpf.example.com/jdf/websocket
```

所以域名代理场景下，最容易漏掉的是：

- 页面代理了 `/jdf/`
- 但没有代理 `/jdf/websocket`

## IP 访问的默认结果

当满足以下条件：

- 通过 IP 访问
- 没有配置 `jetty.ip.serveruri`

默认优先返回：

```text
wsPrefix + IP + :rpcPort
```

如果前面条件判断不稳定，最后可能回退成：

```text
wsPrefix + localhost + :rpcPort
```

这通常不是你想要的现场结果。

## Nginx 代理时只需要记住 3 点

### 1. 最稳妥的方式

域名代理场景直接显式配置：

```properties
jetty.serveruri=wss://你的域名/访问前缀/websocket
```

### 2. 必须传的代理头

- `X-Forwarded-Proto`
- `X-Original-URI`

否则容易出现：

- HTTPS 页面拿到 `ws://`
- 路径前缀丢失

### 3. 必须代理的路径

除了页面路径，还要代理它对应的：

- `/websocket`

如果页面入口是：

```text
/jdf/
```

那通常要同时保证：

- `/jdf/`
- `/jdf/websocket`

都能正确转发。

## 推荐回答模板

如果用户问“为什么域名访问 JDF 不正常”，优先这样回答：

```text
先看 server_uri.config 实际返回什么。
这个值在源码里是动态生成的：
1. 域名访问优先取 jetty.serveruri，没配就拼 域名 + 父路径 + /websocket
2. IP 访问优先取 jetty.ip.serveruri，没配就拼 IP + RPC端口
3. HTTPS 或开启 WSS 时前缀会变成 wss://

如果经过 Nginx，重点检查 3 件事：
- 是否传了 X-Forwarded-Proto
- 是否传了 X-Original-URI
- 是否同时代理了页面路径和 /websocket 路径
```

## 排障优先级

1. 直接访问页面里的 `server_uri.config`，看它实际返回什么
2. 判断返回值是不是用户期望的域名或 IP
3. 判断前缀是不是 `wss://`
4. 判断路径里有没有正确保留代理前缀
5. 判断 `/websocket` 是否被代理
6. 最后再看 `jetty.serveruri`、`jetty.ip.serveruri`、`rpcPort`
