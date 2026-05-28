package gpf.adur.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cdao.model.CDoSlave;
import com.kwaidoo.ms.tool.CmnUtil;

import cmn.anotation.ClassDeclare;
import gpf.md.slave.NestingData;
@ClassDeclare(
	    label = "嵌套表格数据",
	    what = "表单中属性类型为嵌套模型(NestingModel)的值类型\r\n",
	    why = "",
	    how = "构建嵌套数据示例：\r\n" + 
	    		"TableData tableData = new TableData(nestingModel);\r\n" + 
	    		"Form nestingForm = new Form(nestingModel);\r\n" + 
	    		"//嵌套模型的表单设值与父表单设值一样\r\n" + 
	    		"tableData.add(nestingForm);\r\n" + 
	    		"\r\n" + 
	    		"获取嵌套数据示例：\r\n" + 
	    		"TableData tableData = form.getTable(\"嵌套属性\");\r\n" + 
	    		"if(tableData!= null){\r\n" + 
	    		"	List<Form> nestingForms = tableData.getRows();\r\n" + 
	    		"}",
	    developer = "陈晓斌",
	    version = "1.0",
	    createTime = "2025-03-17",
	    updateTime = "2025-03-17"
	)
public class TableData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7569698655350289061L;
	public final static String MasterClass = CDoSlave.MasterClass;
	public final static String MasterKey = CDoSlave.MasterKey;
	public final static String MasterField = CDoSlave.MasterField;
	public final static String OrderSeq = NestingData.OrderSeq;
	
	String formModelId;
	List<Form> rows = new ArrayList<>();
	
	public TableData() {
	}
	public TableData(String formModelId) {
		this.formModelId = formModelId;
	}
	public TableData(List<Form> rows) {
		if(!CmnUtil.isCollectionEmpty(rows)) {
			this.formModelId = rows.get(0).getFormModelId();
		}
		this.rows = rows;
	}
	/**
	 * 表格表单模型ID
	 * @return
	 */
	public String getFormModelId() {
		return formModelId;
	}
	public void setFormModelId(String formModelId) {
		this.formModelId = formModelId;
	}
	/**
	 * 表格数据
	 * @return
	 */
	public List<Form> getRows() {
		return rows;
	}
	public TableData setRows(List<Form> rows) {
		this.rows = rows;
		return this;
	}
	public int size() {
		return rows.size();
	}
	public boolean isEmtpy() {
		return rows.isEmpty();
	}
	public TableData add(Form row) {
		rows.add(row);
		return this;
	}
	public TableData allAll(List<Form> rows) {
		this.rows.addAll(rows);
		return this;
	}
	public TableData delete(int index) {
		this.rows.remove(index);
		return this;
	}
	public TableData delete(Form row) {
		for(Iterator<Form> it = rows.iterator();it.hasNext();) {
			Form itRow = it.next();
			if(CmnUtil.isStringEqual(row.getUuid(), itRow.getUuid())) {
				it.remove();
				break;
			}
		}
		return this;
	}
	
	public TableData deleteByUuids(String... uuids) {
		Map<String,Form> forms = getRowUuidMap();
		for(String uuid : uuids) {
			forms.remove(uuid);
		}
		this.rows = new ArrayList<>(forms.values());
		return this;
	}
	public TableData clear() {
		this.rows.clear();
		return this;
	}
	public Form getData(int index) {
		return this.rows.get(index);
	}
	public Form getData(String formID) {
		for(Iterator<Form> it = rows.iterator();it.hasNext();) {
			Form itRow = it.next();
			if(CmnUtil.isStringEqual(formID, itRow.getUuid())) {
				return itRow;
			}
		}
		return null;
	}
	public TableData update(Form row) {
		int index = -1;
		for(int i =0;i<this.rows.size();i++) {
			Form itRow = this.rows.get(i);
			if(CmnUtil.isStringEqual(row.getUuid(), itRow.getUuid())) {
				index = i;
				break;
			}
		}
		if(index == -1) {
			add(row);
		}else {
			this.rows.set(index, row);
		}
		return this;
	}
	
	public Map<String,Form> getRowUuidMap(){
		if(CmnUtil.isCollectionEmpty(rows))
			return new LinkedHashMap<>();
		return rows.stream().collect(Collectors.toMap(Form::getUuid, v->v, (e, r)-> r,LinkedHashMap::new));
	}
	
	public Map<String,Form> getRowMapByFieldCode(String fieldCode){
		if(CmnUtil.isCollectionEmpty(rows))
			return new LinkedHashMap<>();
		return rows.stream().collect(Collectors.toMap(v->CmnUtil.getString(v.getAttrValueByCode(fieldCode),""), v->v, (e, r)-> r,LinkedHashMap::new));
	}
}
