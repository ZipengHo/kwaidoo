# fetch-current

直接获取云工程当前代码或资源内容，支持多文件。

## 语法

```bash
bapdev [全局选项] fetch-current [选项]
```

## 选项

| 选项 | 说明 | 必需 |
|------|------|------|
| `-f, --file <file>` | 要获取的单个文件路径 | 否，与 `--files` 二选一 |
| `--files <files>` | 要获取的多个文件路径，逗号分隔 | 否，与 `--file` 二选一 |
| `--project-root <dir>` | 项目根目录，默认自动查找 `.bapdev-cli` | 否 |

## 规则

- 只支持 `src` 目录下的文件。
- `src/res/` 下的文件按资源处理。
- 其他 `src/*/*.java` 按 Java 源码处理。
- 资源文件如果可判定为 UTF-8 文本，直接返回文本；否则返回 Base64。
- 此命令用于“查看当前云工程代码”，不能用 `history` 或 `download` 代替。

## 示例

```bash
bapdev fetch-current --file src/cell/IMyCell.java
bapdev fetch-current --files src/cell/IMyCell.java,src/res/conf/app.properties
bapdev --project HelloWorld fetch-current --file src/cell/IMyCell.java
```

## 输出示例

```json
{
  "success": true,
  "data": {
    "projectRoot": "/path/to/project",
    "project": "HelloWorld",
    "files": [
      {
        "path": "src/cell/IMyCell.java",
        "type": "java",
        "fullClassName": "cell.IMyCell",
        "exists": true,
        "encoding": "utf-8",
        "content": "package cell;\npublic class IMyCell {}"
      },
      {
        "path": "src/res/conf/app.properties",
        "type": "resource",
        "cloudPath": "conf/app.properties",
        "exists": true,
        "encoding": "utf-8",
        "content": "server.port=8080"
      }
    ]
  }
}
```
