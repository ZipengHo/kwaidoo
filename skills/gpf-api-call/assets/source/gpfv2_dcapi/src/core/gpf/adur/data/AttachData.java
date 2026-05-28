package gpf.adur.data;

import java.io.Serializable;

import com.kwaidoo.ms.tool.CmnUtil;

import bap.cells.Cells;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.data.IFormMgr;
import cmn.anotation.ClassDeclare;
/**
 * 附件数据
 * @author chenxb
 *
 */
@ClassDeclare(
	    label = "附件数据",
	    what = "表单中属性类型为附件(Attach)的值类型，表单内附件值类型为：List<AttachData>\r\n",
	    why = "",
	    how = "构建附件数据示例：\r\n" + 
	    		"List<AttachData> attaches = new ArrayList<>();\r\n" + 
	    		"attaches.add(new AttachData(\"test.txt\", \"测试附件内容\".getBytes()));\r\n" + 
	    		"获取附件数据示例：\r\n" + 
	    		"List<AttachData> lstAttach = form.getAttachments(\"附件属性\");",
	    developer = "陈晓斌",
	    version = "1.0",
	    createTime = "2025-03-17",
	    updateTime = "2025-03-17"
	)
public class AttachData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9202553398234389010L;
	String formUuid;
	String attrName;
	String name;
	byte[] content;
	boolean dirty = false;
	
	public AttachData() {
	}
	
	public AttachData(String name,byte[] content) {
		this.name = name;
		this.content = content;
		this.dirty = true;
	}
	/**
	 * 
	 * 附件所在表单的uuid
	 * @return
	 */
	public String getFormUuid() {
		return formUuid;
	}
	public AttachData setFormUuid(String formUuid) {
		this.formUuid = formUuid;
		return this;
	}
	/**
	 * 附件所在表单属性Code
	 * @return
	 */
	public String getAttrName() {
		return attrName;
	}
	public AttachData setAttrName(String attrName) {
		this.attrName = attrName;
		return this;
	}
	/**
	 * 文件名称
	 * @return
	 */
	public String getName() {
		return name;
	}
	public AttachData setName(String name) {
		this.name = name;
		return this;
	}
	protected IDaoService getDaoService() {
		return Cells.get(IDaoService.class);
	}
	/**
	 * 文件内容
	 * @return
	 * @throws Exception
	 */
	public byte[] getContent() throws Exception {
		if(content == null) {
			if(!CmnUtil.isStringEmpty(formUuid) && !CmnUtil.isStringEmpty(attrName) && !CmnUtil.isStringEmpty(name)) {
				try(IDao dao = getDaoService().newDao()){
					this.content = IFormMgr.get().queryAttachBytes(dao,formUuid, attrName, name);
				}
			}
		}
		return content;
	}
	public AttachData setContent(byte[] content) {
		this.content = content;
		dirty = true;
		return this;
	}
	/**
	 * 文件大小(字节)
	 * @return
	 * @throws Exception
	 */
	public int getLength() throws Exception {
		byte[] content = getContent();
		if(content != null) {
			return content.length;
		}
		return -1;
	}
	/**
	 * 是否修改过
	 * @return
	 */
	public boolean isDirty() {
		return dirty;
	}
	
	@Override
	public String toString() {
		if(content != null) {
			return name + "("+"(" + CmnUtil.fileSize2String(content.length)+")";
		}
		return name+"";
	}
	
	public AttachData copy() throws Exception {
		AttachData copyData = new AttachData(getName(),getContent());
		return copyData;
	}
	
}
