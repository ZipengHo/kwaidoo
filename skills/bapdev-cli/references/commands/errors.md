# 常见错误码

| 错误码 | 说明 |
|--------|------|
| `SUCCESS` | 操作成功 |
| `NO_FILES` | 未指定文件 |
| `INVALID_PROJECT` | 项目标识无效或缺失 |
| `CONNECTION_FAILED` | 连接 BAP 服务器失败；应优先检查当前 BAP 服务是否可达、服务端是否已启动，以及当前工程连接配置是否仍有效 |
| `NOT_CONNECTED` | 未连接到 BAP 服务器；优先检查当前工程配置中的 `uri`、项目标识和登录信息是否完整可用 |
| `NO_CONFIG` | 未找到完整连接配置；通常表示当前工程缺少可用的 `uri`、项目标识或登录信息 |
| `TEST_EXECUTION_FAILED` | 单元测试执行失败；需要结合 HTML 报告、`report.json` 以及控制台堆栈继续排查 |
| `EXECUTION_ERROR` | 执行过程中发生错误 |
