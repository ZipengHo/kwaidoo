package gpf.adur.data;

import java.io.Serializable;

import cmn.anotation.ClassDeclare;
import cmn.anotation.FieldDeclare;

@ClassDeclare(
    label = "外键模型",
    what = "用于表示数据库表的外键关系",
    why = "提供数据结构以存储和处理外键相关的元数据",
    how = "",
    developer = "陈晓斌",
    version = "1.0",
    createTime = "2024-12-05",
    updateTime = "2024-12-05"
)
public class ForeignModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2988644349863929992L;

    @FieldDeclare(label = "外键模型ID", desc = "唯一标识一个外键模型")
    private String foreignModelId;

    @FieldDeclare(label = "是否可继承", desc = "表示外键模型是否支持继承，目前暂不支持")
    private boolean inheritable = false; // 暂时不支持继承的，预留

    public String getForeignModelId() {
        return foreignModelId;
    }

    public ForeignModel setForeignModelId(String foreignModelId) {
        this.foreignModelId = foreignModelId;
        return this;
    }

    public boolean isInheritable() {
        return inheritable;
    }

    public ForeignModel setInheritable(boolean inheritable) {
        this.inheritable = inheritable;
        return this;
    }
}