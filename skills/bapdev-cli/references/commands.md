# BapDev CLI 命令索引

本文档只提供导航。具体命令参数、工作流和输出示例已拆分到 `references/commands/` 目录下的独立文件。

当前包装脚本不再依赖 `.bapdev-cli/cloud.token` 或令牌口令。需要云工程连接信息时，请改为从工程 `.bapdev-cli/cloud.properties`、全局会话信息或显式参数中提供。

## 公共说明

- [全局选项](./commands/global-options.md)
- [常见错误码](./commands/errors.md)

## 命令说明

- [init](./commands/init-prompt.md)
- [commit](./commands/commit.md)
- [publish](./commands/publish.md)
- [download](./commands/download.md)
- [sync-libs](./commands/sync-libs.md)
- [compile](./commands/compile.md)
- [unit-test](./commands/unit-test.md)
- [fetch-current](./commands/fetch-current.md)
- [update](./commands/update.md)
- [history](./commands/history.md)
- [refresh](./commands/refresh.md)
- [relocate](./commands/relocate.md)
- [scan-jars](./commands/scan-jars.md)
