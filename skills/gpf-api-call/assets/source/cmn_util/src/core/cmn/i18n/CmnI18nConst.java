package cmn.i18n;

public class CmnI18nConst extends AbsI18n{
//	private BasicRes res = new BasicRes("LanguageRes/cmnutil_zh_CN.properties", "LanguageRes/cmnutil_en_US.properties");
	private static CmnI18nConst inst = new CmnI18nConst();
	private CmnI18nConst() {
		super();
	}
	public static CmnI18nConst get() {
		return inst;
	}
	
	@Override
	public String getResourceFileName() {
		return "cmn_util_i18n.setting";
	}
	public static final String FIELD_NAME_LENGTH_EXCEEDS_THE_LIMIT = inst.format("FIELD_NAME_LENGTH_EXCEEDS_THE_LIMIT");
	public static final String CANNOT_BE_NULL = inst.format("CANNOT_BE_NULL");
	public static final String CANNOT_BE_EMPTY = inst.format("CANNOT_BE_EMPTY");
	public static final String IS_NOT_IN_RANGE = inst.format("IS_NOT_IN_RANGE");
	public static final String VALUE_IS_DUPLICATE = inst.format("VALUE_IS_DUPLICATE");
	public static final String INVALID_NUMBER = inst.format("INVALID_NUMBER");
	
	public static final String LENGTH_EXCEEDS_THE_LIMIT = inst.format("LENGTH_EXCEEDS_THE_LIMIT");//"%s Length Exceeds The Limit:%s(%s)";
	public static final String PRECISION_EXCEEDS_THE_LIMIT = inst.format("PRECISION_EXCEEDS_THE_LIMIT");//"%s Precision Exceeds The Limit:%s(%s)";
	public static final String DUPLICATE_FIELD_NAME = inst.format("DUPLICATE_FIELD_NAME");//"Duplicate Field Name %s";
	
	public static final String TREE_NODE_IS_NULL = inst.format("TREE_NODE_IS_NULL");//"TreeNode %s is not exist!";
	public static final String PARENT_TREE_NODE_IS_NULL = inst.format("PARENT_TREE_NODE_IS_NULL");//"Parent TreeNode %s is not exist!";
	public static final String IS_NULL = inst.format("IS_NULL");//"%s is not exist!";
	public static final String SEQ_INCREMENT_IS_UNDEFINE = inst.format("SEQ_INCREMENT_IS_UNDEFINE");//"sequence increment code %s is undefine!";
	
	public static final String TIPS_FIELD_DEFINE_ILLEGAL_MODEL_IS_NOT_RELATE_MODEL = inst.format("TIPS_FIELD_DEFINE_ILLEGAL_MODEL_IS_NOT_RELATE_MODEL");
	public static final String TIPS_FIELD_DEFINE_ILLEGAL_MODEL_IS_NOT_NESTING_MODEL = inst.format("TIPS_FIELD_DEFINE_ILLEGAL_MODEL_IS_NOT_NESTING_MODEL");
	public static final String TIPS_PACKAGEPATH_IS_USED_AS_MODEL_CLASS = inst.format("TIPS_PACKAGEPATH_IS_USED_AS_MODEL_CLASS");
	public static final String TIPS_MODEL_NAME_IS_RESERVED_WORD = inst.format("TIPS_MODEL_NAME_IS_RESERVED_WORD");
	
	public final static String TIPS_PROJECT_IS_NOT_EXIST = getString("TIPS_PROJECT_IS_NOT_EXIST");// {1} java工程uuid
	public final static String TIPS_RESOURCE_FILE_IS_EXIST = getString("TIPS_RESOURCE_FILE_IS_EXIST");// {1} 资源文件名称
	public final static String TIPS_RESOURCE_FILE_IS_NOT_EXIST = getString("TIPS_RESOURCE_FILE_IS_NOT_EXIST");// {1} 资源文件名称
	public final static String TIPS_JAVA_FOLDER_IS_NOT_EXIST = getString("TIPS_JAVA_FOLDER_IS_NOT_EXIST");// {1} 源文件目录名称
	public final static String TIPS_JAVA_FOLDER_IS_EXIST = getString("TIPS_JAVA_FOLDER_IS_EXIST");// {1} 源文件目录名称
	
	
	public static final String DataType = inst.format("dataType");
	public static String getString(String s, Object... params) {
		return get().format(s, params);
	}
	
	
	public static void main(String[] args) {
		System.out.println(get().formatInGroup("IS_NULL", "LicenseInfo",null,1));
	}
	
}
