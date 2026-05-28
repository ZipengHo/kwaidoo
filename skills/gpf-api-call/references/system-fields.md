# GPF 基础 API 系统属性速查

本文件只解决一个问题：`Form` 和 `TableData` 什么时候该用字段名，什么时候该用字段编码或系统常量。

## 一句话规则

- 业务属性读写：默认优先字段名，也就是 `setAttrValue("字段中文名", value)` / `getString("字段中文名")`
- `Form` 系统属性读写：使用 `Form` 常量 + `ByCode`
- `TableData` 系统属性读写：使用 `TableData` 常量 + `ByCode`
- `Cnd` 构造：系统属性可直接使用固定字段名；业务属性通常先转字段编码

## `Form` 系统属性

这些属性是所有表单共有的系统属性，读写时应使用 `Form` 常量。

### 常用常量

```java
Form.UUID         // "uuid"         表单唯一标识
Form.Code         // "code"         表单编号
Form.Owner        // "owner"        所属组织或所属对象
Form.ForeignClass // "foreignClass" 外部关联类
Form.ForeignKey   // "foreignKey"   外部关联键
```

### 什么时候用

- 迁移数据时手工指定 UUID 或 Code
- 按系统编号读写表单
- 处理系统归属或外部关联字段

### 示例

```java
form.setAttrValueByCode(Form.Code, "ORDER001");
String code = form.getStringByCode(Form.Code);
String uuid = form.getStringByCode(Form.UUID);
```

### 注意

- 一般业务字段不要因为看到了 `ByCode` 就全部改成 `ByCode`
- `Form.UUID`、`Form.Code` 这类是系统属性特例

## `TableData` 系统属性

这些属性是嵌套表明细行的系统属性，通常由系统维护。

### 常用常量

```java
TableData.MasterClass // "masterClass" 主表模型 ID
TableData.MasterKey   // "masterKey"   主表 UUID
TableData.MasterField // "masterField" 主表中的嵌套字段编码
TableData.OrderSeq    // "orderSeq"    明细排序序号
```

### 什么时候用

- 读取嵌套表与主表的归属关系
- 读取或判断明细排序序号
- 调试嵌套表数据结构

### 示例

```java
TableData details = orderForm.getTable("订单明细");
if (details != null && !details.isEmtpy()) {
    for (Form detail : details.getRows()) {
        String masterClass = detail.getStringByCode(TableData.MasterClass);
        String masterKey = detail.getStringByCode(TableData.MasterKey);
        String masterField = detail.getStringByCode(TableData.MasterField);
        Long orderSeq = detail.getLongByCode(TableData.OrderSeq);
    }
}
```

### 注意

- 这些字段通常不需要业务代码手工写入
- 业务明细字段仍优先使用字段中文名读写，例如 `detail.setAttrValue("数量", 2L)`
- `TableData` 判空优先用 `isEmtpy()`，取指定行优先用 `getData(...)`

## `Cnd` 中的系统属性与业务属性

### 系统属性

在 `Cnd` 中，可直接使用的系统属性应以前文已经定义过的 `Form` 与 `TableData` 系统属性为准，不要再扩展出另一套字段清单。

也就是说，`Cnd` 里的系统属性来源只有两类：

- `Form` 系统属性：`Form.UUID`、`Form.Code`、`Form.Owner`、`Form.ForeignClass`、`Form.ForeignKey`
- `TableData` 系统属性：`TableData.MasterClass`、`TableData.MasterKey`、`TableData.MasterField`、`TableData.OrderSeq`

在构造 `Cnd` 时，应直接使用这些系统属性对应的固定字段名或常量值：

```java
Form.UUID
Form.Code
Form.Owner
Form.ForeignClass
Form.ForeignKey
TableData.MasterClass
TableData.MasterKey
TableData.MasterField
TableData.OrderSeq
```

示例：

```java
Cnd cnd = Cnd.where(Form.Code, "=", "ORDER001")
        .and(Form.Owner, "=", ownerFormUuid);
```

其中：

- `ownerFormUuid` 表示其他 `Form` 的 uuid
- 不要把 `Form.Owner` 误写成组织编号、组织名称或普通业务编码

### 业务属性

业务属性在 `Cnd` 中通常先转字段编码，再参与条件构造。

示例：

```java
String customerFieldCode = IFormMgr.get().getFieldCode("客户名称");
Cnd cnd = Cnd.where(customerFieldCode, "=", "张三");
```

## 选择口诀

- 读写表单业务值：先想字段中文名
- 读写表单系统值：用 `Form` 常量
- 读写嵌套表系统值：用 `TableData` 常量
- 写查询条件：先判断是不是前文定义过的 `Form` / `TableData` 系统属性，不是就转字段编码
