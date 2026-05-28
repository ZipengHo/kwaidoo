# `web.access.whitelist` 决策树

用途：

- 控制 JDF 页面是否允许访问当前后端
- 不是“只要不在白名单就拒绝”这么简单
- 实际逻辑是：先做自动放行判断，只有自动放行失败后才检查白名单

事实来源：

- `工程源码/gpf_dc_basic/src/core/gpf/dc/basic/util/GpfDCBasicUtil.java`

## 一句话结论

- 先比对当前页面的 HTTP 地址和当前 WebSocket 地址是否天然一致
- 一致就直接放行，不看白名单
- 不一致才检查 `web.access.whitelist`
- 白名单先做精确字符串匹配，再做正则匹配

## 配置位置

文件：

- `base_advance.conf`

样例：

```properties
web.access.whitelist=
```

## 决策树

```text
页面访问校验开始
|
+- 读取当前页面地址
|  |
|  +- hostOrigin = 页面 host[:port]
|  +- pathOrigin = 页面 pathname
|  +- protocol = http 或 https
|
+- 读取当前 websocket 地址 wsUrl
|
+- hostOrigin 为空？
|  |
|  +- 是 -> 拒绝访问
|  |
|  +- 否 -> 继续
|
+- wsUrl 不包含 :// ？
|  |
|  +- 是 -> 拒绝访问
|  |
|  +- 否 -> 解析 wsHost / wsPort
|
+- 判断“自动放行前置条件”是否成立
|  |
|  +- HTTPS 分支：
|  |  |
|  |  +- 页面协议是 https
|  |  +- 且 页面端口 == jetty.https.port
|  |
|  +- 或 HTTP 分支：
|     |
|     +- 页面协议不是 https
|     +- 且 页面端口 == jetty.http.port
|     +- 且 websocket 端口 == rpcPort
|
+- 如果前置条件成立，再判断 host 可否视为同一台机器
|  |
|  +- wsHost == httpHost -> 放行
|  |
|  +- 或 wsHost / httpHost 同时属于 localhost、127.0.0.1 -> 放行
|  |
|  +- 或 httpHost 命中本机网卡 IP -> 放行
|
+- 自动放行失败
|  |
|  +- web.access.whitelist 为空？
|  |  |
|  |  +- 是 -> 拒绝访问
|  |  |
|  |  +- 否 -> 继续
|  |
|  +- 逗号分隔白名单项逐个检查
|     |
|     +- 先做 allowHost.trim() == hostOrigin 精确匹配
|     |
|     +- 再做 hostOrigin.matches(allowHost.trim()) 正则匹配
|     |
|     +- 任一命中 -> 放行
|     |
|     +- 全部未命中 -> 拒绝访问
```

## 真正参与匹配的值

### 页面地址

源码里取的是：

- `urlMsgDto.getHost()`

它的格式是：

- `host`
- 或 `host:port`

白名单实际拿来比对的也是这个值，不是完整 URL，不带协议，不带路径。

例子：

```text
127.0.0.1:8090
gpf.example.com
gpf.example.com:8443
```

### WebSocket 地址

源码里取的是：

- `QueryWebSocketUrl.query(...)`

然后从中解析：

- `wsHost`
- `wsPort`

它主要用于前面的“自动放行”判断，不直接拿来和白名单文本逐项比较。

## 白名单格式

配置值是：

- 一个字符串
- 多个项用英文逗号 `,` 分隔

例子：

```properties
web.access.whitelist=gpf.example.com,gpf.example.com:8443,192.168.1.100:8090
```

## 匹配规则

### 规则一：先精确匹配

源码先做：

```text
allowHost.trim() == hostOrigin
```

所以：

- `gpf.example.com` 只能匹配 `gpf.example.com`
- `gpf.example.com:8443` 只能匹配 `gpf.example.com:8443`

### 规则二：精确匹配失败后，再做正则匹配

源码再做：

```text
hostOrigin.matches(allowHost.trim())
```

这意味着白名单项可以写成 Java 正则。

例如：

```properties
web.access.whitelist=192\\.168\\.1\\.\\d+:8090,.*\\.example\\.com(:8443)?
```

## 最容易误解的点

### 1. 白名单不是第一优先级

只要系统判断：

- HTTPS 分支下，页面端口等于 `jetty.https.port`
- 或 HTTP 分支下，页面端口等于 `jetty.http.port` 且 websocket 端口等于 `rpcPort`
- 并且 host 可判定为同一台机器

就会直接放行，不需要命中白名单。

### 2. 白名单匹配的是 `hostOrigin`

不是完整 URL，不是 pathname，不是 websocket 地址。

匹配目标是：

- `host`
- 或 `host:port`

### 3. 端口是否带上非常关键

源码里 `hostOrigin` 可能带端口，也可能不带端口。

所以配置时要和页面实际值一致。

例如：

- 页面实际是 `gpf.example.com:8443`
- 白名单只写 `gpf.example.com`

那精确匹配不会命中。

### 4. 正则是完整匹配，不是包含匹配

因为源码调用的是：

- `String.matches(...)`

所以正则要能覆盖整个 `hostOrigin`，不是只覆盖一部分。

## 最常见的放行场景

### 场景一：单机直连

通常会直接走自动放行：

- 页面端口等于 `jetty.http.port`
- websocket 端口等于 `rpcPort`
- 页面 host 和 ws host 一致

这时通常不需要额外配白名单。

### 场景二：localhost 和 127.0.0.1 混用

源码专门做了兼容：

- 如果 `wsHost` 和 `httpHost` 都属于 `localhost`、`127.0.0.1`
- 直接放行

### 场景三：域名页面访问，但 websocket 指向其他地址

如果自动放行失败，就需要靠 `web.access.whitelist` 放行当前页面地址。

此时白名单里要写的是：

- 页面地址的 `hostOrigin`

不是 websocket 地址。

## 推荐写法

### 固定单域名

```properties
web.access.whitelist=gpf.example.com
```

### 固定域名加端口

```properties
web.access.whitelist=gpf.example.com:8443
```

### 多个固定入口

```properties
web.access.whitelist=gpf.example.com:8443,admin.example.com:8443,192.168.1.100:8090
```

### 网段或子域名

```properties
web.access.whitelist=192\\.168\\.1\\.\\d+:8090,.*\\.example\\.com(:8443)?
```

## LLM 回答用户时的最短规则

可以直接按下面规则回答：

1. `web.access.whitelist` 不是先查的，先看页面地址和 websocket 地址是否自动放行
2. 自动放行失败后，才检查白名单
3. 白名单匹配目标是页面的 `hostOrigin`，不是完整 URL
4. 白名单先做精确匹配，再做正则匹配
5. 如果页面实际带端口，白名单通常也要带端口
6. 按源码现状，`wsPort == rpcPort` 这一条件只明确绑定在 HTTP 分支，回答时不要把它强行说成 HTTPS 分支的必选条件

## 排障优先级

1. 先确认页面实际 `hostOrigin` 是什么
2. 再确认 websocket 实际地址和端口是什么
3. 再确认页面协议、HTTP/HTTPS 端口是否与服务配置一致
4. 如果是 HTTP 页面，再确认 websocket 端口是否等于 `rpcPort`
5. 自动放行失败后，再检查 `web.access.whitelist`
6. 白名单里先看是否写错成完整 URL、路径或 websocket 地址

## 典型报错含义

如果最终未通过，源码会抛出类似错误：

```text
当前页面未获得访问授权！请联系管理员配置web访问白名单。
当前地址[页面hostOrigin]，websocket地址[wsUrl]
```

看到这类报错时，优先对照：

- 当前地址是不是你写进白名单的值
- websocket 地址是不是现场真实返回值
