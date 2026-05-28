---
name: gpf-api-call
description: GPF 基础 API 调用技能。用于围绕 IUserMgr、IRoleMgr、IFormMgr、IPDFRuntimeMgr、IBackupService、数据模型、查询条件和分页结果编写或修改代码；适用于最终交付物是直接调用 GPF 管理器 API 或流程运行/备份接口的数据操作代码，不适用于 HTTP 接口、规则函数或权限矩阵任务。
---

# GPF 基础 API 调用

## 适用范围

适用：

- 用户、角色、组织、表单数据的增删改查
- 流程运行表单、流程实例、流程列表查询
- 数据导出导入、Excel/Json 备份编排
- `Cnd` 条件拼装和 `ResultSet` 分页处理
- 数据模型、字段取值、关联数据操作
- `PDCForm`、`PDFForm` 等流程表单对象读写
- 作为子技能，被 `gpf-rule-function`、`gpf-cloud-cell`、`gpf-http-interface` 在需要真实 GPF 管理器 API 时显式加载

不适用：

- 对外暴露 URL
- 在规则上下文中执行的逻辑
- 身份匹配和权限授予规则

## 输入契约

至少确认：

- 使用哪个管理器接口
- 目标模型或字段
- 是查询、创建、更新还是删除
- 是否需要事务、分页或关联数据处理

## 技能依赖关系

- `gpf-api-call` 是 GPF 管理器 API 与数据对象操作的专项技能
- 当 `gpf-rule-function`、`gpf-cloud-cell`、`gpf-http-interface` 任务里出现 `IUserMgr`、`IRoleMgr`、`IFormMgr`、`IPDFRuntimeMgr`、`IBackupService`、`Form`、`PDCForm`、`PDFForm`、`TableData`、`AssociationData`、`Cnd`、`ResultSet` 等真实 API 调用或数据对象操作时，必须同时加载本技能
- 其他技能可以负责宿主形态，但不能绕过本技能自行定义 GPF API 调用规则

## 执行流程

1. 先读 `references/overview.md`，确认当前需求属于哪个管理器和数据对象。
2. 再读 `references/core-concepts.md`，确认模型、字段、事务和分页约束。
3. 先读 `references/源码类索引.md`，确认当前要引用的对象是否已有源码快照可核实。
4. 只要任务涉及 `Form`、`TableData`、`AssociationData`、`ResultSet` 等对象方法，先读 `references/form-capability-matrix.md`，确认真实可用的方法名。
5. 如果需要确认真实可用管理器入口，读 `references/manager-api.md`。
6. 如果需要确认 `User`、`Role`、`Org`、`Form`、`PDCForm`、`PDFForm`、`AssociationData`、`ResultSet` 等对象的使用方式，读 `references/entity-models.md`。
7. 如果需要区分 `Form` / `TableData` 的系统属性与业务属性，读 `references/system-fields.md`。
8. 如果需要快速判断“当前场景到底该用字段名、字段编码还是系统常量”，读 `references/field-decision-table.md`。
9. 如果需要知道某个实体“什么时候看示例、该看哪个示例”，读 `references/entity-examples.md`。
10. 如果任务涉及“把一个表单数据复制到另一个表单”或跨表单复制嵌套模型、附件、网络附件，必须先读 `references/form-copy.md`。
11. 生成代码前读 `references/source-patterns.md`、`references/workflow.md` 和 `references/patterns.md`。
12. 如果想避免典型误写，读 `references/anti-patterns.md`。
13. 如果是排错，优先读 `references/errors.md`。
14. 如果是流程运行场景，优先参考 `assets/examples/pdf_runtime_create_and_submit.java`。
15. 如果是备份导出导入场景，优先参考 `assets/examples/backup_service_form_excel.java`。
16. 输出前按 `references/checklist.md` 自检，并参考 `assets/examples/` 的样例。

## 对象真实能力面规则

当任务涉及现有对象方法调用时，必须先按下面顺序收敛事实，再生成代码：

1. 先确认当前操作对象是 `Form`、`TableData`、`AssociationData`、`ResultSet` 还是其他已有类。
2. 先到 `references/form-capability-matrix.md` 或对应源码快照里确认真实存在的方法名。
3. 只有方法名已经被源码快照或能力面核实过，才允许写进输出。
4. 如果方法不在能力面里，就视为“待从源码核实”，不能凭字段名、英文语义或其他框架习惯脑补。
5. 字段类型映射只负责帮助选择“在真实方法里该用哪一个”，不能用来证明某个 `getXXX` / `setXXX` 真的存在。

## 表单元数据处理规则

当用户提供了表单元数据，且任务涉及 `Form` 属性读写时，必须先按下面顺序收敛事实，再生成代码：

1. 从用户提供的元数据中拿到每个属性的字段名、字段编码、属性类型、是否多值。
2. 先到 `references/form-capability-matrix.md` 确认 `Form` 的真实方法名，再根据属性类型确定 Java 值类型，并在真实方法中选择匹配的取值/设值方式。
3. 用户自定义属性默认使用字段名调用 `setAttrValue` / `getXXX`；只有系统属性才使用 `setAttrValueByCode` / `getXXXByCode`。
4. 系统属性必须使用 `Form` 或 `TableData` 常量配合 `ByCode` 方法访问，禁止把系统属性当成普通业务字段处理。
5. 如果现场已有代码已经明确统一按字段编码访问，且字段编码来自现有代码事实或已核实来源，可以跟随现场风格；但不能把“默认使用字段编码”当成通用规则。
6. 如果元数据缺了属性类型，就无法确定 Java 类型和 `get/set` 方法，必须先停下来确认，不能继续生成。

## 表单复制特殊规则

当任务属于“把一个表单复制到另一个表单”时，必须优先判断字段是不是普通标量属性，不能默认把所有属性都按：

```java
Object value = sourceForm.getAttrValue(fieldName);
targetForm.setAttrValue(fieldName, value);
```

直接复制。

尤其是下面这些类型必须重点处理：

- 嵌套模型
- 本地附件
- 网络附件

原因：

- `Form.UUID`、`Form.Code` 是每个表单自己的唯一标识，不能原样复制到目标表单
- `TableData.MasterKey` 是嵌套数据和父表单之间的关联键，不能把源表单上的值直接搬到目标表单
- 附件、网络附件通常也绑定源表单上下文或资源标识，不能简单按普通属性值直接灌到目标表单

如果不处理这些系统属性和关联关系，就直接把源表单值写到目标表单，常见后果是：

- 数据漂移
- 嵌套数据仍然指向旧父表单
- 附件关系异常
- 保存时报错或后续查询错乱

这类场景必须先读 `references/form-copy.md`，按“普通字段 / 系统属性 / 嵌套模型 / 附件 / 网络附件”分开处理。

### 表单属性类型与 Java 类型对照表

| 属性类型 | 常见含义 | Java值类型 | 设值方法 | 取值方法 |
| --- | --- | --- | --- | --- |
| `Text` | 文本 | `String` | `form.setAttrValue(fieldName, textValue)` | `form.getString(fieldName)` |
| `Boolean` | 布尔值 | `Boolean` | `form.setAttrValue(fieldName, boolValue)` | `form.getBoolean(fieldName)` |
| `Long` | 整数 | `Long` | `form.setAttrValue(fieldName, longValue)` | `form.getLong(fieldName)` |
| `Decimal` | 小数 | `Double` | `form.setAttrValue(fieldName, doubleValue)` | `form.getDouble(fieldName)` |
| `Date` | 时间 | `Long` | `form.setAttrValue(fieldName, timeValue)` | `form.getLong(fieldName)`         |
| `Password`     | 密码 | `Password` | `form.setAttrValue(fieldName, password)` | `form.getPassword(fieldName)` |
| `Attach` | 本地附件 | `List<AttachData>` | `form.setAttrValue(fieldName, attaches)` | `form.getAttachments(fieldName)` |
| `WebAttach` | 网络附件 | `List<WebAttachData>` | `form.setAttrValue(fieldName, webAttachs)` | `form.getWebAttachs(fieldName)` |
| `KeyValue` | 属性表 | `List<Map<String, String>>` | `form.setAttrValue(fieldName, keyValues)` | `form.getPropTable(fieldName)` |
| `Relate` 单选 | 关联 | `AssociationData` | `form.setAttrValue(fieldName, assocData)` | `form.getAssociation(fieldName)` |
| `Relate` 多选 | 关联 | `List<AssociationData>` | `form.setAttrValue(fieldName, assocDatas)` | `form.getAssociations(fieldName)` |
| `Depend` | 强依赖 | `String`，值只能是依赖 `Form` 的 `Uuid` | `form.setAttrValue(fieldName, dependFormUuid)` | `form.getString(fieldName)` |
| `NestingModel` | 嵌套模型 | `TableData` | `form.setAttrValue(fieldName, tableData)` | `form.getTable(fieldName)` |

### 系统属性处理规则

- 表单系统属性 `UUID`、`Code`、`Owner`、`ForeignClass`、`ForeignKey` 必须使用 `Form.UUID`、`Form.Code` 等常量配合 `setAttrValueByCode` / `getStringByCode` 访问。
- 嵌套模型系统属性 `MasterClass`、`MasterKey`、`MasterField`、`OrderSeq` 必须使用 `TableData` 常量配合 `ByCode` 方法访问。
- 不要对系统属性再调用 `IFormMgr.get().getFieldCode(...)`，系统属性编码是固定常量。
- 用户自定义属性即使手头有字段编码，默认也应优先使用字段名方法；只有在系统属性或现有代码明确要求按编码访问时才使用 `ByCode`。

## 输出契约

输出必须包含：

- 选用的管理器接口
- 关键代码或修改点
- 事务、模型、字段和分页说明
- 不确定前提和风险

## 强制规范

- 严禁编造工程中已存在类的包路径、接口定义、方法签名、继承关系或返回类型
- 只要输出里引用现有类、接口或管理器，就必须先以当前工程源码或技能内源码快照为准完成核实
- 如果当前技能目录内还没有对应源码快照，应先根据 `references/源码类索引.md` 定位待核实类，再决定是否把最小必要源码收敛进技能目录
- 如果类名能确认但包路径、接口方法或实现细节不能确认，必须明确标注“待从源码核实”，不得补全猜测值
- 只要输出里出现 `Form`、`TableData`、`AssociationData`、`ResultSet` 的方法调用，就必须先以 `references/form-capability-matrix.md` 或对应源码快照核实方法名
- 不在能力面和源码快照中的对象方法，一律视为待核实，禁止按语义脑补出 `getDate(...)`、`getKeyValue(...)`、`getInteger(...)` 这类名字
- 只要用户提供了表单元数据，就必须先按元数据中的属性类型决定 Java 类型和 `Form` 的 `get/set` 方法，禁止凭经验猜字段值类型
- 如果属性类型是 `Relate`，必须先判断是单选还是多选，再决定使用 `AssociationData` 还是 `List<AssociationData>`
- 如果属性类型是 `Depend`，只能按单选强依赖处理，值类型只能是依赖 `Form` 的 `Uuid`，不能生成 `AssociationData`、`List<AssociationData>` 或多选强依赖写法
- 如果属性类型、字段编码、是否多值三者任一缺失且会影响代码正确性，必须先停下来确认
- 如果任务是跨表单复制数据，必须显式处理 `Form.UUID`、`Form.Code`、`TableData.MasterKey` 以及附件/网络附件关系，禁止直接整表单按 `getAttrValue` / `setAttrValue` 搬运

## 引用导航

- 总览：`references/overview.md`
- 核心概念：`references/core-concepts.md`
- 管理器操作面：`references/manager-api.md`
- 实体与结果对象：`references/entity-models.md`
- 对象真实能力面：`references/form-capability-matrix.md`
- 系统属性速查：`references/system-fields.md`
- 字段选择决策表：`references/field-decision-table.md`
- 表单复制专题：`references/form-copy.md`
- 实体示例与查阅时机：`references/entity-examples.md`
- 源码调用模式：`references/source-patterns.md`
- 常见反例：`references/anti-patterns.md`
- 执行流程：`references/workflow.md`
- 代码模式：`references/patterns.md`
- 常见错误：`references/errors.md`
- 最终检查：`references/checklist.md`
- 资料索引：`references/资料索引.md`
- 源码类索引：`references/源码类索引.md`
- 源码快照：`assets/source/`
- 示例模板：`assets/examples/`
