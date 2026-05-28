package cmn.dto.schema;

import java.io.Serializable;
import java.util.Map;

/**
 * DTO 结构描述。
 * 用于表达一个 DTO 类型的整体结构定义。
 * 可用于 HTTP、RPC、前端表单、参数校验等场景的结构元数据输出。
 */
public class DtoSchema implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Schema 名称。
	 * 通常取 DTO 类的简单类名。
	 */
	private String schemaName;
	/**
	 * 根 DTO 的 Java 全限定类名。
	 */
	private String rootJavaType;
	/**
	 * 根节点结构。
	 * 当根节点为复杂对象且启用了 definitions 复用时，通常这里是一个 ref 节点。
	 */
	private DtoSchemaNode root;
	/**
	 * 结构定义集合。
	 * key 为定义名，value 为对应的结构节点定义。
	 * 主要用于复用复杂对象定义以及处理循环引用。
	 */
	private Map<String, DtoSchemaNode> definitions;

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getRootJavaType() {
		return rootJavaType;
	}

	public void setRootJavaType(String rootJavaType) {
		this.rootJavaType = rootJavaType;
	}

	public DtoSchemaNode getRoot() {
		return root;
	}

	public void setRoot(DtoSchemaNode root) {
		this.root = root;
	}

	public Map<String, DtoSchemaNode> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(Map<String, DtoSchemaNode> definitions) {
		this.definitions = definitions;
	}
}
