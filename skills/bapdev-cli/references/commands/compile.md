# compile

在本地直接使用 `compile` 编译当前工程源码到 `bin` 目录，不依赖 `pom.xml`。

## 语法

```bash
bapdev compile [选项]
```

## 选项

| 选项 | 说明 | 必需 |
|------|------|------|
| `-f, --file <file>` | 指定单个源码或资源文件，仅编译该文件 | 否，与 `--files` 二选一 |
| `--files <files>` | 指定多个源码或资源文件，逗号分隔，仅编译这些文件 | 否，与 `--file` 二选一 |
| `--project-root <dir>` | 项目根目录，默认当前目录 | 否 |
| `--clean` | 编译前清空 `bin` 目录 | 否 |
| `--source-dir <dirs>` | 额外指定源码目录，逗号分隔 | 否 |
| `--classpath <entries>` | 额外补充 classpath 条目，逗号分隔 | 否 |
| `--show-classpath-details` | 输出完整 classpath、依赖库和源码目录明细 | 否 |

## 规则

- 如果工程根目录存在 `.classpath`，编译会优先按其中的 `classpathentry kind="src"`、`kind="lib"`、`kind="output"` 执行。
- 资源文件会按其所属源码目录的相对路径复制到输出目录。例如 `src/res/conf/app.properties` 会复制为 `bin/conf/app.properties`。
- 只有在 `.classpath` 不存在时，才会回退为扫描 `src` 下各个子目录和 `lib` 下各个 `jar` / `zip` 的约定模式。
- 默认把 `src` 目录下的一级子目录都视为源码目录，例如 `src/cell`、`src/common`、`src/router`。
- 如果 `src` 下没有子目录，才回退为把整个 `src` 视为源码目录。
- 显式传入 `--source-dir` 时，会在现有源码目录基础上补充指定目录。
- 默认扫描 `lib` 目录下的所有 `jar` 和 `zip` 作为依赖。
- 所有源码目录中的非 `.java` 资源文件会同步复制到输出目录对应位置。
- 传入 `--file` 或 `--files` 时，只编译指定的 `.java` 文件，并只复制指定的非 `.java` 资源文件。
- 默认只输出 classpath、源码目录、依赖库的数量摘要，不返回超长明细列表。
- 如需排查依赖问题，可显式追加 `--show-classpath-details` 输出完整明细。

## 示例

```bash
bapdev compile
bapdev compile --file src/cell/IMyCell.java
bapdev compile --files src/cell/IMyCell.java,src/res/conf/app.properties
bapdev compile --project-root /path/to/project
bapdev compile --clean
bapdev compile --source-dir src,src-gen --classpath /path/to/ext.jar,/path/to/ext2.jar
bapdev compile --show-classpath-details
```

## 输出示例

```json
{
  "success": true,
  "data": {
    "projectRoot": "/path/to/project",
    "outputDir": "/path/to/project/bin",
    "sourceFiles": 12,
    "resourceFiles": 3,
    "classpathEntryCount": 128,
    "sourceDirectoryCount": 3,
    "libraryFileCount": 124
  }
}
```
