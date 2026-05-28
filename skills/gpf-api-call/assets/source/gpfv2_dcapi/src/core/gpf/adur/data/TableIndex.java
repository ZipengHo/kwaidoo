package gpf.adur.data;

import java.io.Serializable;
import java.util.List;

import com.leavay.ms.tool.CmnUtil;

import cmn.anotation.ClassDeclare;
import cmn.anotation.FieldDeclare;
@ClassDeclare(
	    label = "表索引",
	    what = "用于表示数据库表的索引信息",
	    why = "提供数据结构以存储和处理表索引相关的元数据",
	    how = "",
	    developer = "陈晓斌",
	    version = "1.0",
	    createTime = "2024-12-05",
	    updateTime = "2024-12-05"
	)
public class TableIndex implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2151457158607020146L;
	// 决定了唯一性
	@FieldDeclare(label = "索引名称",desc = "")
    public String name;
	@FieldDeclare(label = "是否唯一索引",desc = "")
    public boolean unique;
	@FieldDeclare(label = "构成索引的属性列表",desc = "")
    public List<String> lstFields;
    
    public String getName()
    {
        return name;
    }
    public TableIndex setName(String name)
    {
        this.name = name;
        return this;
    }
    public boolean isUnique()
    {
        return unique;
    }
    public TableIndex setUnique(boolean unique)
    {
        this.unique = unique;
        return this;
    }
    public List<String> getLstFields()
    {
        return lstFields;
    }
    public TableIndex setLstFields(List<String> lstFields)
    {
        this.lstFields = lstFields;
        return this;
    }
    
    public TableIndex addField(String f)
    {
        lstFields = CmnUtil.addToList(lstFields, f);
        return this;
    }
    
    public String toString()
    {
        return getName()+" "+getLstFields();
    }
    
    /**
     * 简单校验一下名字等
     */
    public void verify()
    {
        CmnUtil.assertNotEmpty(getName(), "Please set name");
        CmnUtil.assertNotEmpty(getLstFields(), "Please select fields");
    }
}
