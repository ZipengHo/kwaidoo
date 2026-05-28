# GPF 规则函数常见错误

## 注解不完整

- 类级或方法级声明缺失
- 输入说明不完整

## 体系不统一

- 规则函数没有继承 `CellIntf`
- 包名不是 `cell.` 开头
- 同一技能里同时输出 Cell 规则函数和普通接口两套风格

## 环境变量错误

- 本该自动注入的参数被要求手工传入
- 声明了当前场景没有的环境变量
- 前端规则误用流程变量，或流程规则误用前端变量
- 查询规则漏掉 `$sysvar_cnd$`、`$sysvar_pageNo$`、`$sysvar_pageSize$`

## 业务参数与技术参数混用

- 把字段编码直接暴露给业务人员
- 使用者无法正确配置

## 字段使用错误

- 字段名称和字段编码混淆
- 在 `Cnd`、SQL、界面交互 API 中直接拿字段名称去查
- 系统属性“编号”误写成 `IFormMgr.get().getFieldCode("编号")` 这类普通字段编码转换
- 如果报 `column "bian1hao4" does not exist`，应优先检查是否把系统属性“编号”错误地走了字段编码转换；这类场景通常应直接使用 `Form.Code`

## 配置型回调误写

- 自动提交或待办通知只返回说明字符串，没有真正设置回调参数
- 把配置型回调写成普通校验函数，不说明保存规则或进入规则的配置位置

## 身份匹配规则错误

- 返回 `null`
- 按旧版接口声明 `IContext context`，没有使用 `$env$` 注入的 `Map<String,Object> env`
- 只实现 `matchUser` 模式，遗漏 `queryUser` 模式
- 在 `matchUser` 模式下没有设置 `matchExpression`
- 在 `queryUser` 模式下仍返回 `IdentifyMatchParam`，没有返回 `List<User>`
- 方法返回类型写死为 `IdentifyMatchParam`，导致无法承接 `queryUser` 模式的 `List<User>`
- 方法命名不清晰，无法让使用者判断这是身份匹配函数

## 动态权限职责越界

- 在规则函数技能里展开完整权限矩阵设计
- 没有区分字段权限对象和动作权限对象

## 路由判断错误

- 明明最终产物是规则函数接口或实现，却被错误路由成独立权限矩阵开发
- 把权限领域规则函数和完整权限矩阵引擎设计混为一谈
- 把 `RouterOption` 误写成 `Pair<Boolean, String>` 的替代品；两者都是可接收返回，但语义不同
- 返回 `RouterOption` 后还假设当前节点离开路由会继续逐条识别 `Pair<Boolean, String>` 规则
