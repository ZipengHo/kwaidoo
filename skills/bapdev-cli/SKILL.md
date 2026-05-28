---
name: bapdev-cli
description: 使用 BapDev CLI 对 BAP 云工程执行提交、发布、下载、更新、历史查询、状态刷新、获取云端当前代码、重定位和本地编译。适用于用户明确要求通过命令行操作云工程、通过显式配置完成连接、或执行项目级云工程同步与构建的场景。不适用于 HTTP 接口开发、规则函数开发、平台部署或 IDE 插件操作说明。
---

# BapDev CLI 技能

## 适用范围

适用：

- 用户要求通过命令行提交、发布、下载、更新、编译或查询 BAP 云工程。
- 用户要求通过 HTTP + JWT 为当前工程完成首次鉴权，并建立可复用的连接信息。
- 用户要求生成或执行 `bapdev-cli` 的 shell 包装脚本。

不适用：

- 用户要求通过 IDE 插件执行云工程操作，此时优先使用相邻的云工程 IDE 技能。
- 用户要求开发 HTTP 接口、规则函数、数据库结构或部署脚本。
- 用户只是在讨论 BAP 平台概念，没有明确 CLI 操作目标。

## 输入契约

执行本技能时，至少要明确以下信息中的一项：

- 目标子命令：`commit`、`publish`、`download`、`update`、`history`、`refresh`、`fetch-current`、`relocate`、`compile`、`sync-libs`、`unit-test`、`scan-jars`
- 工作目录，或允许从当前目录向上查找 `.bapdev-cli`
- 已生成的项目级连接配置，或允许先执行 `scripts/auth-http.sh`

常见补充参数：

- `commit`：`--file` / `--files`、`--message`、`--project-root`、`--init-baseline`、`--confirm`
- `publish`：`--no-compile`、`--ignore-error`
- `download`：`--output`
- `sync-libs`：`--project-root`
- `update`：`--file` / `--files`、`--force`
- `history`：`--file` / `--files`、`--limit`
- `fetch-current`：`--file` / `--files`、`--project-root`
- `relocate`：`--dir`
- `compile`：`--file` / `--files`、`--project-root`、`--clean`、`--source-dir`、`--classpath`
- `unit-test`：`--class`、`--package`、`--method`、`--parallel`、`--project-root`、`--execution-mode`、`--show-classpath-details`
- `scan-jars`：`--init`、`--list`、`--pending`、`--analyze`、`--mark-done`、`--refresh`、`--rescan`、`--status`、`--blacklist`

## 执行原则

1. 先确认是不是命令行场景。如果用户要的是 IDE 菜单操作，不要误用本技能。
2. 读取 `scripts/init-env.sh` 或 `scripts/lib/common.sh`，按以下优先级确定连接信息：
   - 当前工程下 `.bapdev-cli` 中已有的项目级元数据，尤其是 `cloud.properties` 中的 `uri`
   - 用户主目录下 `~/.bapdev-cli/<ws目录>/` 中由 `scripts/auth-http.sh` 写入的全局会话信息
   - 用户在当前请求里显式给出的命令参数
3. 需要直接执行命令时，优先使用 `scripts/*.sh` 包装脚本，而不是重复手写 `java -jar ...`。
4. 具体参数、工作流、返回 JSON 字段和示例统一下沉到 `references/commands/` 目录，不在主技能文档重复展开。
5. 输出时明确说明：
   - 实际执行的子命令
   - 生效的项目目录或 `.bapdev-cli` 路径
   - 生效的连接信息来源
   - CLI 返回的 JSON 结果或失败原因

## 脚本入口

- 环境变量初始化：`scripts/init-env.sh`
- 首次 HTTP + JWT 鉴权：`scripts/auth-http.sh`
- 通用 CLI 入口：`scripts/run-cli.sh`
- 子命令包装脚本：
  - `scripts/commit.sh`
  - `scripts/publish.sh`
  - `scripts/download.sh`
  - `scripts/sync-libs.sh`
  - `scripts/update.sh`
  - `scripts/history.sh`
  - `scripts/refresh.sh`
  - `scripts/fetch-current.sh`
  - `scripts/relocate.sh`
  - `scripts/compile.sh`
  - `scripts/unit-test.sh`
  - `scripts/scan-jars.sh`

## 工程信息初始化

- 如果用户要求“初始化工程信息”或“生成/更新工程根目录下的 `AGENTS.md`”，应直接以模板 `references/templates/AGENTS.template.md` 为起点完成，不再依赖初始化脚本。
- 生成或更新 `AGENTS.md` 时，必须使用技能目录内部模板 `references/templates/AGENTS.template.md`，不能引用技能目录外部文件路径。
- `AGENTS.md` 模板只保留主要章节结构和通用约束，不应预填项目专属信息；项目名称、版本、目录、依赖和团队规范应由 AI 分析当前工程后补充。
- 生成 `AGENTS.md` 时，应先识别模板中的占位信息，再分析当前工程内容完成补全，不能保留“待补全”等占位文本。
- `AGENTS.md` 中应至少补齐以下两类入口：
  - 技能入口：说明哪些技能可用于 BAP 云工程操作和本地编译，哪些技能可用于代码开发。
  - 协作入口：说明环境初始化、刷新差异、获取云端当前代码、提交、发布、依赖同步等工作流约束。
- 在 `AGENTS.md` 中，不要直接罗列 `bapdev-cli` 子命令列表，而应说明 `bapdev-cli` 技能负责哪些云工程操作与编译能力。
- 如果工程根目录已有 `AGENTS.md`，应优先在原文件基础上增量更新 `bapdev-cli` 相关区块，不要覆盖用户已有的项目规范内容。

## 使用规则

- 执行脚本前，应先定位 `bapdev-cli` 技能的实际目录。

```bash
SKILL_DIR="<bapdev-cli 技能完整路径>"
# 例如：/path/to/project/.opencode/skills/bapdev-cli
```

- 需要把项目级配置装载到当前 shell 时，优先使用：

```bash
source "${SKILL_DIR}/scripts/init-env.sh"
```

- 如果不能 `source`，则使用：

```bash
eval "$("${SKILL_DIR}/scripts/init-env.sh")"
```

- 如果当前工程中还没有 `.bapdev-cli/cloud.properties`，应先执行“云工程环境初始化”，不要直接执行 `download`、`sync-libs`、`refresh`、`publish`、`update` 等依赖连接的命令。

- 云工程环境初始化命令说明应明确提供给用户：

```bash
"${SKILL_DIR}/scripts/init-env.sh" \
  --project-root . \
  --uri <ws地址> \
  --project-name <工程名称> \
  --write-uri
```

- 上述初始化命令的作用是：
  1. 为当前工程创建最小 `.bapdev-cli/cloud.properties`
  2. 至少写入当前工程对应的 `uri`
  3. 在已知工程名称时同步写入 `projectName`
  4. 让后续命令能够根据当前工程的 ws 地址去尝试复用 `~/.bapdev-cli/<ws目录>/` 下的全局会话

- 首次在工程里配置连接信息时，优先让用户在终端中自行执行：

```bash
"${SKILL_DIR}/scripts/init-env.sh" --project-root . --write-uri
```

- `scripts/auth-http.sh` 只要求用户提供 HTTP 服务地址，例如 `http://127.0.0.1:8090`；具体 `/gpfdc/app/getUser` 接口路径由程序内部自动补全，用户不需要感知。

- BAP 云工程环境初始化的标准用法应说明清楚：
  1. 如果当前工程还没有 `.bapdev-cli`，或缺少 ws 服务地址，应先执行 `scripts/init-env.sh --project-root <dir> --uri <ws地址> --project-name <工程名称> --write-uri`
  2. 写入当前工程最小 `cloud.properties` 后，直接尝试执行后续命令，由 jar 内部优先复用 `~/.bapdev-cli/<ws目录>/` 下的全局会话
  3. 如果 jar 内部连接失败，且判断为当前工程/全局都没有可用会话，再执行 `scripts/auth-http.sh`
  4. 首次 HTTP + JWT 鉴权成功后，会话信息写入 `~/.bapdev-cli/<ws目录>/`
  5. 后续命令：再执行 `download`、`sync-libs`、`refresh`、`publish` 等包装脚本

- 如果当前工程下还没有 `.bapdev-cli/cloud.properties`，或其中缺少 `uri`，应优先让用户执行：

```bash
"${SKILL_DIR}/scripts/init-env.sh" --project-root . --uri <ws地址> --project-name <工程名称> --write-uri
```

- 如果用户只说“初始化云工程环境”或“让当前工程能连接 BAP”，默认应理解为先执行上面的初始化命令，而不是直接要求用户先做 `auth-http.sh`。

- 如果命令执行结果里出现 `缺少 ws 服务地址`，应优先判断为“当前工程尚未初始化最小 cloud.properties”，并直接引导用户先手动执行：

```bash
"${SKILL_DIR}/scripts/init-env.sh" --project-root . --uri <ws地址> --project-name <工程名称> --write-uri
```

- 如果命令执行结果里出现 `未连接到BAP服务器` 或 `NOT_CONNECTED`，应优先判断为“jar 内部未找到可用会话或当前工程/全局配置不足”，并引导用户按以下顺序处理：
  1. 先确认当前工程 `.bapdev-cli/cloud.properties` 中是否有 `uri`
  2. 再确认当前工程 `.bapdev-cli/cloud.properties` 中是否已有 `projectName`
  3. 然后让 jar 内部尝试复用 `~/.bapdev-cli/<ws目录>/session.properties`
  4. 如果全局会话仍不存在或连接仍失败，再执行 `scripts/auth-http.sh`

- 默认不要读取、解析或依赖 `.develop`。技能应只使用 `.bapdev-cli` 与用户显式输入。
- 技能对外说明中，不再把 `BAP_URI`、`BAP_PROJECT`、`BAP_USER`、`BAP_PASSWORD` 作为配置方式提供给用户。

- 执行具体子命令时，优先走包装脚本。例如：

```bash
"${SKILL_DIR}/scripts/commit.sh" --file src/demo/Test.java --message "修复问题"
```

- 首次在某个工程上使用 `commit` 时，应先初始化基线：

```bash
"${SKILL_DIR}/scripts/commit.sh" --project-root . --init-baseline
```

- 如果首次提交时，目标文件与云工程内容完全一致，CLI 会自动把当前状态记为基线，不再强制要求手工执行 `--init-baseline`。
- 普通 `commit` 默认只输出摘要；真正执行提交时再追加 `--confirm`。
- 在代码比较场景中，不应使用 `history` 或 `download` 作为“云工程当前代码”的来源。
- `history` 只用于历史版本查询，`download` 只用于整包下载。
- 云工程当前代码与 Git 基线版本的冲突判断，应在上层工作流中完成；`commit --confirm` 真实执行时不再重复做这一步校验。
- 需要直接查看当前云工程代码时，优先使用：

```bash
"${SKILL_DIR}/scripts/fetch-current.sh" --files src/cell/Test.java,src/res/conf/app.properties
```

- 包装脚本会自动定位 jar，并优先从当前工程 `.bapdev-cli` 以及 `~/.bapdev-cli/<ws目录>/` 中装载连接信息；脚本层只要求当前工程至少能提供 `uri`，实际连接校验应交由 jar 内部处理。
- 技能发布包应包含运行所需依赖，技能使用时不应要求额外配置额外的旧插件打包脚本。
- `update` 不应跳过差异检查；应先通过 `refresh` 判断本地代码与云工程代码是否存在差异，再决定是否执行更新。
- `download` 和 `sync-libs` 完成后，应补齐或重建工程根目录下的 `.classpath`，并刷新 `.bapdev-cli` 元数据。
- `download` 完成后，不应默认直接提交 Git；应先向用户明确“是否要提交 Git 仓库生成基线”。
- `compile` 应优先按 `.classpath` 中声明的 `src`、`lib`、`output` 条目编译；只有在 `.classpath` 不存在时，才回退到约定目录扫描。

## jar 包扫描与分析

- `scan-jars` 命令用于扫描工程内的 jar 包依赖，管理分析状态，为 AI 提供结构化的 jar 包分析输入。
- 执行 jar 包分析前，应先调用 `--init` 初始化配置文件（黑名单模板和输出目录）。
- 获取待分析 jar 包列表时，使用 `--pending` 命令，只返回新增或哈希变更的 jar 包。
- 分析单个 jar 包时，调用 `--analyze` 获取元数据（路径、哈希、输出文件位置等），由 AI 完成实际分析并生成文档。
- 完成单个 jar 包分析后，调用 `--mark-done` 更新状态文件。
- 黑名单配置文件为 `.bapdev-cli/jars-blacklist.txt`，支持 glob 模式和相对路径匹配。
- 输出目录为 `.bapdev-cli/jars/`，包含：
  - `jars-state.json`：状态存储文件
  - `jars-overview.md`：汇总总览文件
  - `{jarname}-overview.md`：单个 jar 包总览
  - `{jarname}-index.md`：单个 jar 包详细类索引
- jar 包哈希采用 SHA256 算法，增量分析时比对哈希值跳过未变更的 jar 包。
- 用户可以通过 `--blacklist add/remove` 命令管理黑名单，或直接编辑黑名单配置文件。
- 修改黑名单后，应调用 `--refresh` 命令更新状态文件中已黑名单 jar 的记录。

## 自检要求

- `scripts/auth-http.sh` 必须能通过 HTTP + JWT 完成首次鉴权，并将会话写入 `~/.bapdev-cli/<ws目录>/`。
- `scripts/init-env.sh` 默认只能从 `.bapdev-cli`、`~/.bapdev-cli` 和显式参数加载连接信息，不能默认读取 `.develop`。
- `scripts/init-env.sh --write-uri` 必须能在当前工程缺少 `cloud.properties` 或缺少 `uri` 时，补齐最小连接配置。
- `scripts/run-cli.sh --version` 等价能力必须可用，至少要能成功输出版本 JSON。
- 每个子命令脚本都必须把参数透传给 jar，不得吞掉用户传入的选项。
- `commit.sh` 在未显式提供 `--project-root` 时，应尽量自动补齐项目根目录。
- `commit.sh` 必须等待云提交、Git 提交和 `.bapdev-cli` 状态写入完成后才返回，不得在下载、解压、提交尚未完成时提前结束。
- 主技能文档只保留入口、规则和导航；参数细节与命令工作流放到 `references/commands/`。

## 文件导航

- 命令索引：`references/commands.md`
- 工程信息模板：`references/templates/AGENTS.template.md`
- 全局选项：`references/commands/global-options.md`
- `AGENTS 初始化提示词`：`references/commands/init-prompt.md`
- `commit`：`references/commands/commit.md`
- `publish`：`references/commands/publish.md`
- `download`：`references/commands/download.md`
- `sync-libs`：`references/commands/sync-libs.md`
- `compile`：`references/commands/compile.md`
- `unit-test`：`references/commands/unit-test.md`
- `fetch-current`：`references/commands/fetch-current.md`
- `update`：`references/commands/update.md`
- `history`：`references/commands/history.md`
- `refresh`：`references/commands/refresh.md`
- `relocate`：`references/commands/relocate.md`
- `scan-jars`：`references/commands/scan-jars.md`
- 常见错误码：`references/commands/errors.md`
- jar 包黑名单模板：`references/jars-blacklist.template.txt`
- 环境变量与路径解析：`scripts/init-env.sh`
- 公共函数：`scripts/lib/common.sh`
- jar 包扫描函数：`scripts/lib/jar-utils.sh`
- 实际命令执行入口：`scripts/run-cli.sh`
