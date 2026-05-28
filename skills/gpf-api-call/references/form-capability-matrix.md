# GPF 表单与结果对象真实能力面

本文件是 `Form`、`TableData`、`AssociationData`、`ResultSet` 等对象的真实调用面速查表。

使用规则：

- 这里只记录已经从 `assets/source/` 源码快照核实过的方法。
- 生成代码前，先在这里确认对象真实可用的方法，再结合字段元数据决定该用哪个方法。
- 如果某个 `getXxx` / `setXxx` / `deleteXxx` 不在本表中，就不能直接输出，必须回到源码继续核实。
- 不要根据英文语义、其他框架习惯或字段名去脑补方法名。

## `Form`

### 基础字段读取

| 场景 | 真实方法 | 返回类型 | 说明 | 常见伪造名 |
| --- | --- | --- | --- | --- |
| 文本字段 | `getString(attrName)` | `String` | 业务属性默认按字段名称读取 | `getText(...)` |
| 布尔字段 | `getBoolean(attrName)` | `Boolean` | 布尔值读取 | `getBool(...)` |
| 整数字段 | `getLong(attrName)` | `Long` | GPF 整数字段统一按 `Long` 读取 | `getInteger(...)` |
| 小数字段 | `getDouble(attrName)` | `Double` | 小数读取 | `getDecimal(...)` |
| 时间字段时间戳 | `getLong(attrName)` | `Long` | `Date` 属性底层按 `Long` 存储 | `getDate(...)` |
| 时间字段日期对象 | `getTime(attrName)` | `Date` | 只有明确需要 `Date` 对象时再使用 | `getDate(...)` |
| 密码字段 | `getPassword(attrName)` | `Password` | 密码不是普通字符串 | `getString(...)` |

### 复杂字段读取

| 场景 | 真实方法 | 返回类型 | 说明 | 常见伪造名 |
| --- | --- | --- | --- | --- |
| 属性表原始结构 | `getPropTable(attrName)` | `List<Map<String, String>>` | 保留原始键值行结构 | `getKeyValue(...)` |
| 属性表键值映射 | `getPropKeyValueMap(fieldName)` | `Map<String, String>` | 需要直接按 key 取值时使用 | `getKeyValueMap(...)` |
| 单选关联 | `getAssociation(attrName)` | `AssociationData` | 单选关联读取 | `getRelate(...)` |
| 多选关联 | `getAssociations(attrName)` | `List<AssociationData>` | 多选关联读取 | `getRelates(...)` |
| 本地附件 | `getAttachments(attrName)` | `List<AttachData>` | 本地附件字段 | `getAttach(...)` |
| 网络附件 | `getWebAttachs(attrName)` | `List<WebAttachData>` | 方法名以源码为准，保留 `Attachs` 拼写 | `getWebAttachments(...)` |
| 嵌套表 | `getTable(attrName)` | `TableData` | `NestingModel` 字段读取 | `getRows(...)` |
| 字节数组 | `getByteArray(attrName)` | `byte[]` | 少数二进制字段场景 | `getBytes(...)` |

### `ByCode` 版本

- `getStringByCode`
- `getBooleanByCode`
- `getLongByCode`
- `getDoubleByCode`
- `getTimeByCoide`
- `getPropTableByCode`
- `getPropKeyValueMapByCode`
- `getAssociationByCode`
- `getAssociationsByCode`
- `getAttachmentsByCode`
- `getWebAttachsByCode`
- `getTableByCode`

说明：

- 这些方法只在系统属性、动态字段或现场代码已明确按字段编码访问时使用。
- `getTimeByCoide` 的拼写以源码快照为准，不能自作主张改成 `getTimeByCode`。

### 设值与通用入口

| 场景 | 真实方法 | 说明 |
| --- | --- | --- |
| 按字段名称设值 | `setAttrValue(fieldName, value)` | 业务属性默认入口 |
| 按字段编码设值 | `setAttrValueByCode(fieldCode, value)` | 系统属性或编码驱动场景 |
| 通用按字段名称取底层值 | `getAttrValue(attrName)` | 只在动态逻辑必须兜底时使用 |
| 通用按字段编码取底层值 | `getAttrValueByCode(fieldCode)` | 动态字段场景 |

## `TableData`

| 场景 | 真实方法 | 返回类型 | 说明 | 常见伪造名 |
| --- | --- | --- | --- | --- |
| 判空 | `isEmtpy()` | `boolean` | 源码拼写就是 `isEmtpy` | `isEmpty()` |
| 按索引取行 | `getData(int index)` | `Form` | 读取指定明细行 | `get(index)` |
| 按行 UUID 取行 | `getData(String formID)` | `Form` | 按明细行标识读取 | `get(uuid)` |
| 读取全部行 | `getRows()` | `List<Form>` | 遍历明细行 | 直接 `for (Form x : tableData)` |
| 新增行 | `add(Form row)` | `TableData` | 追加一行 | `append(...)` |
| 整体覆盖 | `setRows(List<Form> rows)` | `TableData` | 用整行列表覆盖 | `replaceAll(...)` |
| 按索引删除 | `delete(int index)` | `TableData` | 删除指定行 | `remove(index)` |
| 按对象删除 | `delete(Form row)` | `TableData` | 删除指定对象 | `remove(row)` |
| 按 UUID 删除 | `deleteByUuids(...)` | `TableData` | 批量删除行 | `removeAll(...)` |

## `AssociationData`

| 场景 | 真实方法 | 返回类型 | 说明 |
| --- | --- | --- | --- |
| 读取关联值 | `getValue()` | `String` | 第二个构造参数对应的业务标识 |
| 读取关联模型 | `getFormModelId()` | `String` | 关联模型 ID |
| 读取关联表单 | `getForm()` | `Form` | 需要进一步读关联对象字段时使用 |

## `ResultSet<T>`

| 场景 | 真实方法 | 返回类型 | 说明 | 常见伪造名 |
| --- | --- | --- | --- | --- |
| 读取当前页数据 | `getDataList()` | `List<T>` | 分页结果数据列表 | 直接遍历 `resultSet` |
| 读取总记录数 | `getTotalCount()` | `int` | 分页总数 | `size()` |
| 读取当前页大小 | `getSize()` | `int` | 当前页返回条数 | 直接 `resultSet.size()` |

## 决策顺序

生成代码时，按下面顺序收敛：

1. 先确认当前操作对象是 `Form`、`TableData`、`AssociationData` 还是 `ResultSet`。
2. 再在本表确认该对象真实存在的方法名。
3. 再根据字段元数据或业务场景，从真实方法里选择匹配的读写方式。
4. 如果本表里没有对应方法，回到源码快照核实，不得直接猜。
