package gpf.adur.data;

import java.io.Serializable;

import com.kwaidoo.ms.tool.CmnUtil;

import bap.cells.Cells;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.data.IFormMgr;
import cmn.anotation.ClassDeclare;
/**
 * 关联数据
 * @author chenxb
 *
 */
@ClassDeclare(
	    label = "关联数据",
	    what = "表单中属性类型为关联(Relate)的值类型，多选关联为List<AssociationData>\r\n",
	    why = "",
	    how = "构建关联数据示例：\r\n" + 
	    		"String assocModelId = \"gpf.md.test.AssocData\";//关联模型ID\r\n" + 
	    		"String assocFormCode = \"001\";//关联数据编号\r\n" + 
	    		"AssociationData assocData = new AssociationData(assocModelId, assocFormCode)；\r\n" + 
	    		"获取关联数据示例：\r\n" + 
	    		"//单选关联\r\n" + 
	    		"AssociationData assocData = form.getAssociation(\"关联属性\");\r\n" + 
	    		"//关联数据的编号\r\n" + 
	    		"String assocCode = assocData.getValue();\r\n" + 
	    		"//多选关联\r\n" + 
	    		"List<AssociationData> assocDatas = form.getAssociations(\"多选关联属性\");",
	    developer = "陈晓斌",
	    version = "1.0",
	    createTime = "2025-03-17",
	    updateTime = "2025-03-17"
	)
public class AssociationData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1136431591676330500L;
	String formModelId;
	String value;
	Form form;
	public AssociationData() {
	}
	public AssociationData(String formModelId,String value) {
		this.formModelId = formModelId;
		this.value = value;
	}
	public String getFormModelId() {
		return formModelId;
	}
	/**
	 * 关联表单的编号
	 * @return
	 */
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	protected IDaoService getDaoService() {
		return Cells.get(IDaoService.class);
	}
	/**
	 * 关联的表单
	 * @return
	 * @throws Exception
	 */
	public Form getForm() throws Exception {
		if(form == null) {
			if(!CmnUtil.isStringEmpty(value) && !CmnUtil.isStringEmpty(formModelId)) {
				try(IDao dao = getDaoService().newDao()){
					form = IFormMgr.get().queryFormByCode(dao,formModelId, value);
				}
			}
		}
		return form;
	}
	
	@Override
	public String toString() {
		return value+"";
	}
}
