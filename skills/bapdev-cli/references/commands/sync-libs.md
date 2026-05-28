# sync-libs

从云工程增量同步本地工程依赖库目录。

## 语法

```bash
bapdev [全局选项] sync-libs [选项]
```

## 选项

| 选项 | 说明 | 必需 |
|------|------|------|
| `--project-root <dir>` | 项目根目录，默认使用当前目录 | 否 |

## 行为说明

- 该命令会基于旧平台真实 RPC 增量同步以下目录或内容：
  - `lib/platform`
  - `lib/plugin`
  - `lib/project`
  - `lib/model`
  - `src/OpenSource`
- 项目标识优先使用全局 `--project`，未提供时再从 `.bapdev-cli` 解析。
- 该命令是面向当前工程的云端依赖同步入口，不是技能发布包构建脚本。
- 同步完成后，会重建工程根目录下的 `.classpath`，并刷新 `.bapdev-cli` 元数据，让本地编译和 IDE 引用与最新依赖保持一致。

## 示例

```bash
bapdev sync-libs
bapdev sync-libs --project-root .
bapdev --project HelloWorld sync-libs --project-root /path/to/project
```

## 输出示例

```json
{
  "success": true,
  "data": {
    "project": "HelloWorld",
    "projectRoot": "/path/to/project",
    "platformUpdated": 12,
    "platformDeleted": 1,
    "pluginUpdated": 3,
    "pluginDeleted": 0,
    "projectLibUpdated": 5,
    "projectLibDeleted": 0,
    "modelUpdated": true,
    "openSourceUpdated": true,
    "srcFolders": ["cell", "form"]
  }
}
```
