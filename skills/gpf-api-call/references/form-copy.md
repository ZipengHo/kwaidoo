# 表单复制特殊规则

本文档只处理一种高风险场景：

- 把一个表单数据复制到另一个表单

这类需求最容易写出“看起来能跑、实际上会把系统属性和关联关系复制坏掉”的代码，因此必须单独处理。

## 一句话规则

普通标量属性可以按字段逐个复制；嵌套模型、附件、网络附件以及系统属性不能直接按 `getAttrValue` / `setAttrValue` 原样搬运。

尤其不能直接写成：

```java
Object value = sourceForm.getAttrValue(fieldName);
targetForm.setAttrValue(fieldName, value);
```

然后对所有字段一把梭。

## 为什么这是高风险场景

下面这些字段或对象带有强上下文语义：

- `Form.UUID`
- `Form.Code`
- `TableData.MasterKey`
- 本地附件
- 网络附件

它们的问题分别是：

### `Form.UUID`

- 每个表单实例自己的唯一标识
- 复制到目标表单时，绝不能沿用源表单的 `UUID`

### `Form.Code`

- 每个表单实例自己的唯一编码
- 如果目标表单也沿用源表单的 `Code`，通常会造成唯一性冲突或业务语义错乱

### `TableData.MasterKey`

- 嵌套模型数据与父表单之间的关联 key
- 如果把源表单中嵌套行的 `MasterKey` 直接复制到目标表单，嵌套数据仍然可能指向旧父表单

### 附件 / 网络附件

- 通常都带有资源归属、业务关联或上下文绑定
- 不能简单把源表单里取到的附件对象整体塞给目标表单

如果这些不处理，常见结果是：

- 数据漂移
- 嵌套表行仍然关联旧表单
- 附件关系异常
- 保存时报错
- 查询结果混乱

## 应该怎么拆开处理

复制表单时，先把字段分成 4 类：

1. 普通标量字段
2. 系统属性字段
3. 嵌套模型字段
4. 附件 / 网络附件字段

### 1. 普通标量字段

可以逐个字段复制，例如：

```java
targetForm.setAttrValue("标题", sourceForm.getString("标题"));
targetForm.setAttrValue("金额", sourceForm.getDouble("金额"));
targetForm.setAttrValue("状态", sourceForm.getString("状态"));
```

### 2. 系统属性字段

下面这些通常不应直接复制：

- `Form.UUID`
- `Form.Code`

结论：

- 目标表单应让系统生成自己的 `UUID`
- 目标表单如果需要新编号，应走目标场景自己的编号生成逻辑

### 3. 嵌套模型字段

复制嵌套模型时，不要把整个 `TableData` 连同源 `MasterKey` 原样塞到目标表单。

正确思路：

- 读取源表单的嵌套表
- 遍历每一行
- 只复制业务字段
- 不复制 `TableData.MasterKey` 等系统关联字段
- 让目标表单保存时重新建立父子关系

### 4. 附件 / 网络附件字段

附件和网络附件不能默认当普通属性复制。

正确思路：

- 本地附件 `AttachData` 与网络附件 `WebAttachData` 要分开处理
- 先确认目标场景到底是“复制文件内容”还是“复用原文件资源”
- 再按对象真实 API 和当前业务语义处理
- 如果当前工程没有明确的附件复制方案，必须先停下来确认，不能脑补

#### 4.1 本地附件 `AttachData`

根据源码 `AttachData.java` 可确认：

- `AttachData` 包含 `formUuid`、`attrName`、`name`、`content`
- `getContent()` 会在 `content == null` 时，基于 `formUuid`、`attrName`、`name` 去查源表单附件字节
- `copy()` 的真实语义是：

```java
AttachData copyData = new AttachData(getName(), getContent());
```

也就是说：

- 它会复制文件名和文件内容
- 不会沿用源附件的 `formUuid`、`attrName`
- 这正适合跨表单复制时重新挂到目标表单

推荐做法：

- 优先调用 `attach.copy()`
- 或者显式 `new AttachData(源文件名, 源内容字节)`
- 然后把新附件列表设到目标表单

#### 4.2 网络附件 `WebAttachData`

根据源码 `WebAttachData.java` 可确认：

- `WebAttachData` 包含 `uuid`、`fileUuid`、`name`、`size`、`md5`
- `getContent()` 会通过 `IFormMgr.get().downloadWebAttach(fileUuid)` 下载内容
- `copy()` 的真实语义是：

```java
WebAttachData cloneData = new WebAttachData(fileUuid, name);
cloneData.setUuid(ToolUtilities.allockUUIDWithUnderline())
    .setSize(size)
    .setMd5(md5);
```

也就是说：

- 它会保留原 `fileUuid`
- 会生成新的 `uuid`
- 会复制 `name`、`size`、`md5`

这说明网络附件默认更偏向“复用原文件资源、生成新的挂接关系”，而不是重新上传一份字节内容。

推荐做法：

- 如果业务允许复用原网络文件资源，优先调用 `webAttach.copy()`
- 如果业务要求生成新的网络文件资源，就不能只做 `copy()`，而要走当前工程真实的上传 / 建立资源关系流程
- 不要把 `uuid` 原样复制到目标表单

## 推荐复制示例

下面示例演示“普通字段 + 嵌套模型业务字段 + 本地附件 + 网络附件”的安全复制方式，重点说明不要复制系统属性和旧关联关系。

```java
import gpf.adur.data.AttachData;
import gpf.adur.data.Form;
import gpf.adur.data.TableData;
import gpf.adur.data.WebAttachData;

import java.util.ArrayList;
import java.util.List;

public class FormCopyExample {

    public void copyFormData(Form sourceForm, Form targetForm) {
        // 1. 普通标量字段：逐个复制业务字段
        targetForm.setAttrValue("标题", sourceForm.getString("标题"));
        targetForm.setAttrValue("金额", sourceForm.getDouble("金额"));
        targetForm.setAttrValue("申请事由", sourceForm.getString("申请事由"));

        // 2. 系统属性：不要复制 Form.UUID / Form.Code
        // targetForm.setAttrValueByCode(Form.UUID, sourceForm.getStringByCode(Form.UUID)); // 禁止
        // targetForm.setAttrValueByCode(Form.Code, sourceForm.getStringByCode(Form.Code)); // 禁止

        // 3. 嵌套模型：只复制业务字段，不复制 MasterKey 等系统字段
        TableData sourceItems = sourceForm.getTable("明细");
        if (sourceItems != null) {
            TableData targetItems = new TableData();
            List<Form> targetRows = new ArrayList<Form>();

            for (Form sourceRow : sourceItems.getRows()) {
                Form targetRow = new Form();

                // 只复制业务字段
                targetRow.setAttrValue("物料名称", sourceRow.getString("物料名称"));
                targetRow.setAttrValue("规格", sourceRow.getString("规格"));
                targetRow.setAttrValue("数量", sourceRow.getLong("数量"));
                targetRow.setAttrValue("单价", sourceRow.getDouble("单价"));

                // 禁止复制嵌套系统属性
                // targetRow.setAttrValueByCode(TableData.MasterKey, sourceRow.getStringByCode(TableData.MasterKey)); // 禁止

                targetRows.add(targetRow);
            }

            targetItems.setRows(targetRows);
            targetForm.setAttrValue("明细", targetItems);
        }

        // 4. 本地附件：复制文件名和内容，不沿用源 formUuid / attrName
        List<AttachData> sourceAttaches = sourceForm.getAttachments("附件");
        if (sourceAttaches != null && !sourceAttaches.isEmpty()) {
            List<AttachData> targetAttaches = new ArrayList<AttachData>();
            for (AttachData sourceAttach : sourceAttaches) {
                targetAttaches.add(sourceAttach.copy());
            }
            targetForm.setAttrValue("附件", targetAttaches);
        }

        // 5. 网络附件：如果业务允许复用原文件资源，可保留 fileUuid、生成新的 uuid
        List<WebAttachData> sourceWebAttaches = sourceForm.getWebAttachs("网络附件");
        if (sourceWebAttaches != null && !sourceWebAttaches.isEmpty()) {
            List<WebAttachData> targetWebAttaches = new ArrayList<WebAttachData>();
            for (WebAttachData sourceWebAttach : sourceWebAttaches) {
                targetWebAttaches.add(sourceWebAttach.copy());
            }
            targetForm.setAttrValue("网络附件", targetWebAttaches);
        }
    }
}
```

## 必须重点提醒

### 错误示例 1：整表单属性直接搬运

```java
for (String fieldName : fieldNames) {
    targetForm.setAttrValue(fieldName, sourceForm.getAttrValue(fieldName));
}
```

问题：

- 会把不该复制的复杂字段一起搬过去
- 极易把嵌套模型、附件、网络附件和系统属性复制坏

### 错误示例 2：系统属性继续走字段编码转换

```java
String codeField = IFormMgr.get().getFieldCode("编号");
targetForm.setAttrValueByCode(codeField, sourceForm.getStringByCode(codeField));
```

问题：

- 系统属性不是普通业务字段
- `编号` 这类系统属性应优先使用 `Form.Code`

### 错误示例 3：复制嵌套模型时保留旧 `MasterKey`

```java
targetRow.setAttrValueByCode(TableData.MasterKey, sourceRow.getStringByCode(TableData.MasterKey));
```

问题：

- 会让嵌套数据继续指向旧父表单

### 错误示例 4：附件 / 网络附件整体对象直接搬运

```java
targetForm.setAttrValue("附件", sourceForm.getAttrValue("附件"));
targetForm.setAttrValue("网络附件", sourceForm.getAttrValue("网络附件"));
```

问题：

- 本地附件可能仍然绑定源表单上下文
- 网络附件可能把旧挂接关系整体带到目标表单
- 这类写法绕过了 `copy()` 的安全复制语义

## 写这类代码前必须确认的事

1. 目标表单是否应该保留原业务字段值
2. 目标表单是否需要新编号
3. 嵌套模型是否允许整行复制，还是要做字段裁剪
4. 附件是复用原资源，还是生成新的资源关系
5. 当前工程里是否已有成熟的表单复制实现可复用

只要以上任一项不清楚，就不要直接生成最终复制代码。
