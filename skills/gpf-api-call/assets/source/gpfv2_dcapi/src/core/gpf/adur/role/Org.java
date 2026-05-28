package gpf.adur.role;

import gpf.adur.data.Form;
import gpf.adur.user.User;

/**
 * 组织
 * @author chenxb
 *
 */
public class Org extends Form{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4804703666683516147L;

	/**
	 * 唯一编码
	 */
	public final static String Code = "code";
	/**
	 * 标签
	 */
	public final static String Name = "name";
	/**
	 * 标签
	 */
	public final static String Label = "label";
	/**
	 * 描述
	 */
	public final static String Description = "description";
	/**
	 * 父组织编码
	 */
	public final static String ParentOrgCode = "parentOrgCode";
	/**
	 * 父组织唯一标识
	 */
	public final static String ParentUuid = "parentUuid";
	
	public Org() {
		super();
//		setCode(getUuid());
	}
	
	public Org(String formModelID) {
		super(formModelID);
		setCode(getUuid());
	}
	
	public Org(String formModelID,String uuid) {
		super(formModelID,uuid);
		setCode(getUuid());
	}
	/**
	 * 编号
	 * @return
	 * @throws Exception
	 */
	public String getCode() throws Exception {
		return getStringByCode(Code);
	}
	
	public Org setCode(String code) {
		setAttrValueByCode(Code, code);
		return this;
	}
	/**
	 * 中文名称
	 * @return
	 * @throws Exception
	 */
	public String getName() throws Exception {
		return getStringByCode(Name);
	}
	
	public Org setName(String name) {
		setAttrValueByCode(Name, name);
		return this;
	}
	/**
	 * 中文名称
	 * @return
	 * @throws Exception
	 */
	public String getLabel() throws Exception {
		return getStringByCode(Label);
	}
	
	public Org setLabel(String label) {
		setAttrValueByCode(Label, label);
		return this;
	}
	/**
	 * 说明
	 * @return
	 * @throws Exception
	 */
	public String getDescription() throws Exception {
		return getStringByCode(Description);
	}
	
	public Org setDescription(String description) {
		setAttrValueByCode(Description, description);
		return this;
	}
	/**
	 * 父组织Uuid
	 * @return
	 * @throws Exception
	 */
	public String getParentUuid() throws Exception {
		return getStringByCode(ParentUuid);
	}
	
	public Org setParentUuid(String parentUuid) {
		setAttrValueByCode(ParentUuid, parentUuid);
		return this;
	}
}
