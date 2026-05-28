# GPF 基础 API 常见反例

本文件不是为了展示语法错误，而是为了防止模型学到错误习惯。

## 反例 1：业务属性读写误用 `ByCode`

错误：

```java
String customerFieldCode = IFormMgr.get().getFieldCode("客户名称");
form.setAttrValueByCode(customerFieldCode, "张三");
String customerName = form.getStringByCode(customerFieldCode);
```

为什么错：

- 这是普通业务属性读写，不是系统属性也不是动态字段场景
- 会把模型误导成“所有表单读写都该先转编码”

正确方向：

- 直接看 `assets/examples/form_basic_usage.java`

## 反例 2：把 `Cnd` 的编码规则套到普通表单读写

错误：

```java
String amountFieldCode = IFormMgr.get().getFieldCode("订单金额");
form.setAttrValueByCode(amountFieldCode, 1999.99);
```

为什么错：

- `Cnd` 构造和普通表单读写不是同一规则

正确方向：

- 查询条件看 `assets/examples/cnd_basic_usage.java`
- 普通表单读写看 `assets/examples/form_basic_usage.java`

## 反例 3：把业务属性当系统属性写进 `Cnd`

错误：

```java
Cnd cnd = Cnd.where("客户名称", "=", "张三");
```

为什么错：

- `客户名称` 是业务属性，不是系统属性
- 在 `Cnd` 中通常应先转字段编码

正确方向：

- 看 `assets/examples/cnd_basic_usage.java`

## 反例 4：把 `TableData` 系统列当业务字段读取

错误：

```java
Long orderSeq = detail.getLong("排序序号");
```

为什么错：

- `orderSeq` 是嵌套表系统属性，应使用 `TableData.OrderSeq`

正确方向：

- 看 `assets/examples/system_field_usage.java`
- 看 `assets/examples/table_data_usage.java`

## 反例 5：把 `TableData` 当普通集合调用虚构方法

错误：

```java
TableData details = form.getTable("订单明细");
if (details != null && !details.isEmpty()) {
    Form firstLine = details.get(0);
    details.remove(firstLine);
}
```

为什么错：

- `TableData` 的真实接口不是 `isEmpty()`、`get(index)`、`remove(...)`
- 这样会把模型带偏成“它就是 `List<Form>`”

正确方向：

- 判空看 `details.isEmtpy()`
- 取值看 `details.getData(index)` 或 `details.getData(uuid)`
- 删除看 `details.delete(...)`
- 参考 `assets/examples/table_data_usage.java`

## 反例 6：把 `ResultSet` 当普通 `List`

错误：

```java
ResultSet<Form> resultSet = IFormMgr.get().queryFormPage(...);
for (Form form : resultSet) {
    // ...
}
```

为什么错：

- `ResultSet` 包含数据列表和总数，不是列表本身

正确方向：

- 看 `assets/examples/result_set_usage.java`

## 反例 7：把关联字段当普通字符串

错误：

```java
form.setAttrValue("负责人", "USER001");
```

为什么错：

- 关联字段通常应写成 `AssociationData` 或 `List<AssociationData>`

正确方向：

- 看 `assets/examples/association_data_usage.java`

## 反例 8：为现有对象脑补不存在的方法名

错误：

```java
Date purchaseDate = form.getDate(FIELD_PURCHASE_DATE);
Map<String, String> classTime = courseForm.getKeyValue(FIELD_CLASS_TIME);
Long level = form.getInteger(FIELD_LEVEL);
```

为什么错：

- 当前源码快照中的 `Form` 没有 `getDate(...)`、`getKeyValue(...)`、`getInteger(...)`
- “字段类型是什么” 只能帮助选择真实方法，不能证明某个 `getXXX` 一定存在
- 如果不先核实对象真实能力面，模型就会按英语语义或其他框架习惯脑补方法名

正确方向：

- 默认读取时间戳：`Long purchaseTime = form.getLong(FIELD_PURCHASE_DATE);`
- 需要 `Date` 对象时，使用已核实存在的 `form.getTime(...)`
- 读取属性表原始结构时，使用 `form.getPropTable(FIELD_CLASS_TIME)`
- 需要键值映射时，使用 `form.getPropKeyValueMap(FIELD_CLASS_TIME)`
- 所有对象方法先看 `references/form-capability-matrix.md`
