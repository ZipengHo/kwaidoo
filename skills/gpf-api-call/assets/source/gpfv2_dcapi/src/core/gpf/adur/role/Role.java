package gpf.adur.role;

import java.io.Serializable;
import java.util.Map;

import com.cdao.model.CDoRole;

import gpf.adur.data.Form;

/**
 * 角色
 * @author chenxb
 *
 */
public class Role implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7476613747519709141L;
	
	public final static String ModelId = CDoRole.class.getName();
	public final static String Uuid = "uuid";
	public final static String Code = "code";
	public final static String Label = "label";
	public final static String Description = "description";
	public final static String Owner = "owner";
	/**
	 * 唯一标识
	 */
	String uuid;
	/**
	 * 唯一编号
	 */
	String code;
	/**
	 * 标签
	 */
	String label;
	/**
	 * 描述
	 */
	String description;
	/**
	 * 所属组织
	 */
	String owner;
	
	Map<String,Object> extFields;

	public Role() {
	}

	public Role(String label) {
		this.label = label;
	}

	public Role(String label, String description) {
		this.label = label;
		this.description = description;
	}
	
	public String getUuid() {
		return uuid;
	}
	public Role setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}
	public String getCode() {
		return code;
	}
	public Role setCode(String code) {
		this.code = code;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public Role setLabel(String label) {
		this.label = label;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Role setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public String getOwner() {
		return owner;
	}
	public Role setOwner(String owner) {
		this.owner = owner;
		return this;
	}
	public Map<String, Object> getExtFields() {
		return extFields;
	}
	public Role setExtFields(Map<String, Object> extFields) {
		this.extFields = extFields;
		return this;
	}
	/**
	 * 转换为form对象
	 * @return
	 */
	public Form convertToForm() {
		Form form = new Form(ModelId);
		form.setUuid(getUuid());
		form.setAttrValueByCode(Code, getCode());
		form.setAttrValueByCode(Label, getLabel());
		form.setAttrValueByCode(Description, getDescription());
		form.setAttrValueByCode(Owner, getOwner());
		return form;
	}
	
	public static Role convertToRole(Form form) throws Exception {
		Role role = new Role();
		role.setUuid(form.getUuid()).setCode(form.getStringByCode(Code)).setLabel(form.getStringByCode(Label)).setDescription(form.getStringByCode(Description))
		.setOwner(form.getStringByCode(Owner));
		return role;
	}
}
