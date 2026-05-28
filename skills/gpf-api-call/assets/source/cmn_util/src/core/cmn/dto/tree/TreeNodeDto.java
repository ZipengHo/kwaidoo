package cmn.dto.tree;

import com.cdao.dto.DaoDto;

public class TreeNodeDto extends DaoDto {
	public static final String ENTITY_CLASS_PATH = "web.md.TreeNode";
	/**
	 * the constant of field {@link TreeNodeDto#name}
	 */
	public static final String CONST_NAME = "name";
	/**
	 * the constant of field {@link TreeNodeDto#label}
	 */
	public static final String CONST_LABEL = "label";
	/**
	 * the constant of field {@link TreeNodeDto#parentUuid}
	 */
	public static final String CONST_PARENT_UUID = "parentUuid";
	/**
	 * the constant of field {@link TreeNodeDto#nodeType}
	 */
	public static final String CONST_NODE_TYPE = "nodeType";
	/**
	 * the constant of field {@link TreeNodeDto#realDataUuid}
	 */
	public static final String CONST_REAL_DATA_UUID = "realDataUuid";
	/**
	 * the constant of field {@link TreeNodeDto#orderSeq}
	 */
	public static final String CONST_ORDER_SEQ = "orderSeq";
	/**
	 * the constant of field {@link TreeNodeDto#parentPath}
	 */
	public static final String CONST_PARENT_PATH = "parentPath";
	/**
	 * the constant of field {@link TreeNodeDto#createTime}
	 */
	public static final String CONST_CREATE_TIME = "createTime";
	/**
	 * the constant of field {@link TreeNodeDto#updateTime}
	 */
	public static final String CONST_UPDATE_TIME = "updateTime";
	private static final long serialVersionUID = -1394630608703684977L;
	String name;
	String label;
	String parentUuid;
	String nodeType;
	String realDataUuid;
	Integer orderSeq;
	String parentPath;
	Long createTime;
	Long updateTime;

	@Override
	public String getName() {
		return name;
	}

	public TreeNodeDto setName(String name) {
		this.name = name;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public TreeNodeDto setLabel(String label) {
		this.label = label;
		return this;
	}

	public String getParentUuid() {
		return parentUuid;
	}

	public TreeNodeDto setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
		return this;
	}

	public String getNodeType() {
		return nodeType;
	}

	public TreeNodeDto setNodeType(String nodeType) {
		this.nodeType = nodeType;
		return this;
	}

	public String getRealDataUuid() {
		return realDataUuid;
	}

	public TreeNodeDto setRealDataUuid(String realDataUuid) {
		this.realDataUuid = realDataUuid;
		return this;
	}

	public Integer getOrderSeq() {
		return orderSeq;
	}

	public TreeNodeDto setOrderSeq(Integer orderSeq) {
		this.orderSeq = orderSeq;
		return this;
	}

	public String getParentPath() {
		return parentPath;
	}

	public TreeNodeDto setParentPath(String parentPath) {
		this.parentPath = parentPath;
		return this;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public TreeNodeDto setCreateTime(Long createTime) {
		this.createTime = createTime;
		return this;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public TreeNodeDto setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
		return this;
	}

	@Override
	protected String initDaoClass() {
		return ENTITY_CLASS_PATH;
	}
}
