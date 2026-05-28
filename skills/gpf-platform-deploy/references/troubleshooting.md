# 常见问题

## 排查顺序

1. 先确认环境是否满足 `JDK 1.8` 和 `PostgreSQL 9.3+`
2. 再确认使用了正确的启动文件和脚本
3. 再查数据库、端口、HTTPS/WSS、白名单、Web 路由
4. 最后查日志和配置联动问题

## 常见现象

### 启动失败

优先检查：

- `JAVA_HOME` 是否可用
- 是否使用了正确的启动脚本
- 启动文件中的必填参数是否缺失

### ClassNotFoundException: com/sun/xml/internal/rngom/parse/compact/EOFException

**现象**：启动时报 `java.lang.ClassNotFoundException: com/sun/xml/internal/rngom/parse/compact/EOFException`

**原因**：`JAVA_HOME` 路径未正确设置，未将 `$JAVA_HOME/lib` 下的 `dt.jar` 和 `tools.jar` 引入 `CLASSPATH`

**解决**：

1. 确认 `JAVA_HOME` 环境变量已正确指向 JDK 安装目录

2. 在启动脚本或环境配置中添加：
   ```bash
   export JAVA_HOME=/path/to/jdk1.8
   export CLASSPATH=$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$CLASSPATH
   ```

### JDK 版本不匹配

优先检查：

- `java -version` 输出是否为 1.8
- `JAVA_HOME` 是否指向错误版本

### 数据库连接失败

优先检查：

- PostgreSQL 版本是否满足 `9.3+`
- `dao` 模块中的 JDBC URL、用户名、密码
- 数据库地址和端口是否可达

### HTTP 端口不生效

优先检查：

- `starter.xml` 或 `agent_starter.xml` 中 `jetty.http.port`
- 是否有端口冲突
- 是否启动了正确的配置文件

### HTTPS 或 WSS 异常

优先检查：

- `jetty.https.enable`
- `jetty.https.port`
- `jetty.https.keystore.*`
- `com.bap.nio.wss.enable`
- `com.bap.nio.wss.file.crt`
- `com.bap.nio.wss.file.key`

### 页面可访问但登录、接口或路由异常

优先检查：

- `conf/web/service.json`
- `handlerMappings`
- `interceptors`
- `servletMapping`

### JDF 页面可打开但通信异常

优先检查：

- `server_uri.config` 实际返回内容
- `jetty.serveruri.enable`
- `jetty.serveruri`
- `jetty.ip.serveruri`
- `com.bap.nio.wss.enable`
- Nginx 是否传递 `X-Forwarded-Proto`
- Nginx 是否传递 `X-Original-URI`
- Nginx 是否代理了页面路径对应的 `/websocket` 路径

### 页面提示未获得访问授权

优先检查：

- `web.access.whitelist`
- 页面实际 `hostOrigin` 是否带端口
- websocket 实际地址是否与页面地址可自动判定为同机
- HTTP/HTTPS 端口是否与服务配置一致
- 白名单是否误写成完整 URL 或带路径

### 跨域失败

优先检查：

- `service.json` 中是否存在对应跨域配置
- 目标 URL 是否被正确映射

### 静态资源 404

优先检查：

- `jetty.resource.path`
- `service.json` 中 `resourceDirs`
- 部署目录下静态资源路径是否存在

### 后台启动无日志

优先检查：

- 是否使用 `nohupStartup.sh`
- 当前目录下是否生成 `log.txt`
- 是否启用了输出重定向相关配置

### 无法停止服务或停错进程

优先检查：

- 当前服务实际监听的是 HTTP 端口还是 RPC 端口
- 是否先按端口准确找到对应 PID
- 是否误杀了同机其他 Java 进程
- `kill` 后进程是否已真正退出

Linux 下常用检查命令：

```bash
ss -ltnp | grep ':8090'
lsof -i :8090
lsof -t -i :8090
kill PID
kill -9 PID
```

### 集群成员无法加入

优先检查：

- 是否使用 `agent_starter.xml`
- `tinyServiceServant` 或 `tinyServiceMember` 的首领地址和本机地址
- DAO 仆从模式或主节点地址是否正确
