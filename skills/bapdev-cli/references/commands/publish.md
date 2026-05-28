# publish

发布项目到 BAP 服务器。

## 语法

```bash
bapdev [全局选项] publish [选项]
```

## 选项

| 选项 | 说明 | 必需 |
|------|------|------|
| `--no-compile` | 发布时跳过编译 | 否 |
| `--ignore-error` | 忽略编译错误继续发布 | 否 |

## 前提条件

需要能从 `.bapdev-cli` 或显式命令参数解析到项目标识。

## 示例

```bash
bapdev publish
bapdev --project my-project publish
bapdev --project my-project publish --no-compile
bapdev --project my-project publish --ignore-error
```

## 输出示例

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "发布成功",
  "data": {
    "projectId": "my-project",
    "compiled": true
  }
}
```
