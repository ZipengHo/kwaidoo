# scan-jars 命令

扫描工程内的 jar 包，管理分析状态，为 AI 提供待分析的 jar 包列表。

## 用途

- 发现项目中所有 jar 包依赖
- 记录每个 jar 包的哈希值，实现增量分析
- 通过黑名单过滤不需要分析的 jar 包
- 为 AI 提供结构化的 jar 包分析输入

## 基本用法

```bash
"${SKILL_DIR}/scripts/scan-jars.sh" <子命令> [选项]
```

## 子命令

### --init

初始化配置文件，创建黑名单模板和输出目录。

```bash
"${SKILL_DIR}/scripts/scan-jars.sh" --init
```

输出：
```json
{
  "success": true,
  "blacklistFile": "/path/to/.bapdev-cli/jars-blacklist.txt",
  "outputDir": "/path/to/.bapdev-cli/jars",
  "stateFile": "/path/to/.bapdev-cli/jars-state.json"
}
```

### --list

列出所有 jar 包及其哈希和分析状态。

```bash
"${SKILL_DIR}/scripts/scan-jars.sh" --list
```

输出示例：
```json
{
  "jars": [
    {
      "path": "/project/lib/h2-2.1.214.jar",
      "relativePath": "lib/h2-2.1.214.jar",
      "hash": "sha256:abc123...",
      "size": 2345678,
      "needsAnalysis": true
    },
    {
      "path": "/project/lib/slf4j-api-1.7.36.jar",
      "relativePath": "lib/slf4j-api-1.7.36.jar",
      "hash": "sha256:def456...",
      "size": 41234,
      "needsAnalysis": false
    }
  ]
}
```

### --pending

只输出需要分析的 jar 包（新增或哈希变更的）。

```bash
"${SKILL_DIR}/scripts/scan-jars.sh" --pending
```

输出示例：
```json
{
  "pendingCount": 3,
  "jars": [
    {
      "path": "/project/lib/h2-2.1.214.jar",
      "relativePath": "lib/h2-2.1.214.jar",
      "hash": "sha256:abc123...",
      "size": 2345678,
      "needsAnalysis": true
    }
  ],
  "outputDir": "/path/to/.bapdev-cli/jars"
}
```

### --analyze <jar>

输出单个 jar 包的分析元数据，供 AI 使用。

```bash
"${SKILL_DIR}/scripts/scan-jars.sh" --analyze lib/h2-2.1.214.jar
```

输出示例：
```json
{
  "jarPath": "/project/lib/h2-2.1.214.jar",
  "relativePath": "lib/h2-2.1.214.jar",
  "jarName": "h2-2.1.214.jar",
  "hash": "sha256:abc123...",
  "size": 2345678,
  "outputDir": "/path/to/.bapdev-cli/jars",
  "overviewFile": "/path/to/.bapdev-cli/jars/h2_2_1_214-overview.md",
  "indexFile": "/path/to/.bapdev-cli/jars/h2_2_1_214-index.md",
  "analyzeCommand": "请分析该 jar 包并生成以下文件:",
  "instructions": [
    "1. 解压 jar 包，获取其中的 .class 文件列表",
    "2. 分析 jar 包的用途、主要功能和包结构",
    "3. 生成 overviewFile 内容：jar 包总览（用途、包结构、核心类概述）",
    "4. 生成 indexFile 内容：详细类索引（包名、类名、主要方法签名）",
    "5. 更新 jars-state.json 记录分析完成状态"
  ]
}
```

### --mark-done <jar>

标记指定 jar 包已完成分析。

```bash
"${SKILL_DIR}/scripts/scan-jars.sh" --mark-done lib/h2-2.1.214.jar
```

AI 在完成 jar 包分析后调用此命令，更新状态文件。

### --status

显示扫描状态摘要。

```bash
"${SKILL_DIR}/scripts/scan-jars.sh" --status
```

输出示例：
```json
{
  "totalJars": 15,
  "analyzedJars": 3,
  "pendingJars": 12,
  "lastScan": "2024-01-15T10:30:00Z",
  "stateFile": "/path/to/.bapdev-cli/jars-state.json",
  "blacklistFile": "/path/to/.bapdev-cli/jars-blacklist.txt",
  "outputDir": "/path/to/.bapdev-cli/jars"
}
```

### --refresh

刷新扫描状态，应用黑名单变更。

```bash
"${SKILL_DIR}/scripts/scan-jars.sh" --refresh
```

当修改黑名单配置后，调用此命令移除已黑名单 jar 的状态记录。

### --rescan

全量重新扫描，清除所有分析状态。

```bash
"${SKILL_DIR}/scripts/scan-jars.sh" --rescan
```

### --blacklist add <pattern>

添加黑名单模式。

```bash
"${SKILL_DIR}/scripts/scan-jars.sh" --blacklist add "test-*.jar"
```

### --blacklist remove <pattern>

移除黑名单模式。

```bash
"${SKILL_DIR}/scripts/scan-jars.sh" --blacklist remove "test-*.jar"
```

## 全局选项

| 选项 | 说明 | 默认值 |
|------|------|--------|
| `--project-root <dir>` | 项目根目录 | 当前目录或 `.bapdev-cli` 所在目录 |
| `--output <dir>` | 输出目录 | `.bapdev-cli/jars` |
| `--exclude <dirs>` | 排除目录（逗号分隔） | `.git,.bapdev-cli,.opencode,node_modules,target,build` |

## 配置文件

### jars-blacklist.txt

黑名单配置文件，格式如下：

```
# 注释以 # 开头
# 支持以下格式：
#   - 完整文件名：example.jar
#   - glob 模式：*-sources.jar, test-*.jar
#   - 相对路径：lib/test/helper.jar
#   - 路径 glob：lib/test/*.jar

# 常见排除项
*-sources.jar
*-javadoc.jar
```

### jars-state.json

状态存储文件，记录每个 jar 包的分析状态：

```json
{
  "version": "1.0",
  "lastScan": "2024-01-15T10:30:00Z",
  "projectRoot": "/path/to/project",
  "jars": {
    "lib/h2-2.1.214.jar": {
      "hash": "sha256:abc123...",
      "size": 2345678,
      "lastAnalyzed": "2024-01-15T10:30:00Z",
      "overviewFile": "jars/h2_2_1_214-overview.md",
      "indexFile": "jars/h2_2_1_214-index.md"
    }
  }
}
```

## AI 工作流

技能加载后，AI 执行 jar 包分析的推荐流程：

1. **初始化配置**
   ```bash
   "${SKILL_DIR}/scripts/scan-jars.sh" --init
   ```

2. **获取待分析列表**
   ```bash
   "${SKILL_DIR}/scripts/scan-jars.sh" --pending
   ```

3. **逐个分析 jar 包**
   - 调用 `--analyze` 获取元数据
   - 解压 jar 包获取 class 文件列表
   - 分析用途、包结构、核心类
   - 生成 `{name}-overview.md` 和 `{name}-index.md`

4. **标记分析完成**
   ```bash
   "${SKILL_DIR}/scripts/scan-jars.sh" --mark-done lib/example.jar
   ```

5. **生成汇总总览**
   - 生成 `.bapdev-cli/jars/jars-overview.md`
   - 包含所有已分析 jar 包的汇总信息

## 输出文件

### jars-overview.md

汇总总览文件，包含所有已分析 jar 包的概览：

```markdown
# 项目 jar 包依赖总览

## 统计信息
- 总 jar 包数量: 15
- 已分析数量: 3
- 待分析数量: 12

## 已分析 jar 包

### h2-2.1.214.jar
- 用途: H2 嵌入式数据库引擎
- 状态文件: jars/h2_2_1_214-overview.md
- 详细索引: jars/h2_2_1_214-index.md

### slf4j-api-1.7.36.jar
- 用途: SLF4J 日志门面 API
- 状态文件: jars/slf4j_api-overview.md
- 详细索引: jars/slf4j_api-index.md
```

### {jarname}-overview.md

单个 jar 包总览：

```markdown
# h2-2.1.214.jar 总览

## 基本信息
- 大小: 2.3 MB
- 哈希: sha256:abc123...
- 最后分析: 2024-01-15

## 用途
H2 是一个轻量级的嵌入式 Java 数据库引擎，支持内存模式和文件模式。

## 主要包结构
- `org.h2.driver` - JDBC 驱动入口
- `org.h2.engine` - 数据库引擎核心
- `org.h2.command` - SQL 解析与执行
- `org.h2.table` - 表定义与管理
- `org.h2.index` - 索引实现
- `org.h2.store` - 数据存储

## 核心类
- `org.h2.Driver` - JDBC 驱动入口类
- `org.h2.engine.Engine` - 数据库引擎主类
- `org.h2.engine.Session` - 会话管理
- `org.h2.command.Parser` - SQL 解析器

## 使用场景
- 单元测试中替代 MySQL/Oracle
- 嵌入式应用的数据存储
- 开发环境快速验证
```

### {jarname}-index.md

详细类索引：

```markdown
# h2-2.1.214.jar 类索引

## org.h2.driver

### Driver
```java
public class Driver implements java.sql.Driver {
  public static final String URL_PREFIX = "jdbc:h2:";
  
  public Connection connect(String url, Properties info)
  public boolean acceptsURL(String url)
  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
  public int getMajorVersion()
  public int getMinorVersion()
  public boolean jdbcCompliant()
}
```

## org.h2.engine

### Engine
```java
public class Engine {
  public static Engine getInstance()
  public Session getSession(ConnectionInfo ci)
  public void close(String name)
  public ArrayList<String> getDatabaseNames()
}
```

### Session
```java
public class Session extends SessionBase {
  public void commit()
  public void rollback()
  public Table createTable(CreateTableData data)
  public Index createIndex(CreateIndexData data)
}
```
```

## 错误处理

| 场景 | 处理方式 |
|------|----------|
| 未安装 sha256sum/shasum/openssl | 提示用户安装相关工具 |
| jar 文件不存在 | 返回错误，提示路径检查 |
| 未安装 jq/python3 | 使用内置 JSON 处理回退方案 |
| 黑名单文件不存在 | 自动创建模板文件 |

## 依赖

- sha256sum / shasum / openssl（哈希计算）
- jq 或 python3（JSON 处理）
- find（文件扫描）