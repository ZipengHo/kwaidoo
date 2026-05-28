package cell.gpf.adur.data;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import com.cdao.dto.CPager;
import com.cdao.dto.DataRow;
import com.cdao.model.CDoLink;

import bap.cells.Cells;
import cell.ServiceCellIntf;
import cell.cdao.IDao;
import cmn.dto.Progress;
import cmn.dto.verify.ValidationResult;
import cmn.enums.NestingTableUpdateMode;
import gpf.adur.data.AssociationData;
import gpf.adur.data.AttachData;
import gpf.adur.data.Form;
import gpf.adur.data.FormField;
import gpf.adur.data.FormModel;
import gpf.adur.data.ResultSet;
import gpf.adur.data.WebAttachData;
import gpf.dc.intf.ExportImportIntf;
import gpf.dc.intf.FormOpObserver;
import web.dto.Pair;
/**
 * 数据模型管理接口
 * 管理数据模型(FormModel)和数据(Form)的CRUD操作
 */
public interface IFormMgr extends ServiceCellIntf{

	static IFormMgr get() {
		return Cells.get(IFormMgr.class);
	}
	
	/**
	 * 根据属性中文名获取属性英文编号
	 * @param fieldName	属性中文名
	 * @return	属性英文名
	 * @throws Exception
	 */
	public String getFieldCode(String fieldName)throws Exception;
	
	/**
	 * 获取业务实体根模型ID
	 * @return	业务实体根模型ID
	 * @throws Exception
	 */
	public String getRootBusinessEntityModelId() throws Exception;

	/**
	 * 获取业务实体根模型
	 * @return	业务实体根模型
	 * @throws Exception
	 */
	public FormModel getBusinessEntityModel()throws Exception;
	
	/**
	 * 判断是否业务实体模型
	 * @param modelId	模型ID
	 * @return
	 * @throws Exception
	 */
	public boolean isBusinessEntityModel(String modelId)throws Exception;
	/**
	 * 获取流程实体根模型ID
	 * @return	流程实体根模型ID
	 * @throws Exception
	 */
	public String getRootProcessEntityModelId() throws Exception;
	/**
	 * 获取流程实体根模型
	 * @return	Exception
	 * @throws Exception
	 */
	public FormModel getProcessEntityModel()throws Exception;
	/**
	 * 判断是否流程实体模型
	 * @param modelId	模型ID
	 * @return
	 * @throws Exception
	 */
	public boolean isProcessEntityModel(String modelId)throws Exception;
	/**
	 * 获取嵌套实体根模型ID
	 * @return	嵌套实体根模型ID
	 * @throws Exception
	 */
	public String getRootNestingEntityModelId() throws Exception;
	/**
	 * 获取嵌套实体根模型
	 * @return	嵌套实体根模型
	 * @throws Exception
	 */
	public FormModel getNestingEntityModel()throws Exception;
	/**
	 * 判断是否嵌套实体模型
	 * @param modelId
	 * @return
	 * @throws Exception
	 */
	public boolean isNestingEntityModel(String modelId)throws Exception;
	
	/**
	 * 返回修改模型的校验结果
	 * @param model	校验的模型
	 * @return	校验结果
	 * @throws Exception
	 */
	public List<ValidationResult> verifyUpdateModel(FormModel model)throws Exception;
	/**
	 * 返回修改属性的校验结果
	 * @param orgField	修改前的属性
	 * @param modifyField	修改后的属性
	 * @return	校验结果
	 * @throws Exception
	 */
	public ValidationResult verifyModifyFormField(FormField orgField,FormField modifyField) throws Exception ;
	/**
	 * 根据模型uuid获取模型ID
	 * @param formModelUuid	模型uuid
	 * @return	模型ID
	 * @throws Exception
	 */
	public String getFormModelIdByUuid(String formModelUuid)throws Exception;
	/**
	 * 创建表单模型
	 * @param formModel	模型
	 * @return	创建后的模型
	 * @throws Exception
	 */
	public FormModel createFormModel(FormModel formModel) throws Exception;
	/**
	 * 更新表单模型
	 * @param prog	进度通知对象
	 * @param formModel	模型
	 * @return	更新后的模型
	 * @throws Exception
	 */
	public FormModel updateFormModel(Progress prog,FormModel formModel) throws Exception;
	/**
	 * 重命名表单模型
	 * @param prog	进度通知对象
	 * @param renameModels	key:模型ID, value:(新模型ID,模型中文名)
	 * @throws Exception
	 */
	public void renameFormModel(Progress prog,Map<String,Pair<String, String>> renameModels)throws Exception;
	/**
	 * 重命名表单模型ID
	 * @param prog	进度通知对象
	 * @param renameModels	key:模型ID, value:新模型ID
	 * @throws Exception
	 */
	public void renameFormModelId(Progress prog,Map<String,String> renameModels)throws Exception;
	/**
	 * 变更模型的父模型，此操作不会同步模型属性，请谨慎操作
	 * @param prog	进度通知对象
	 * @param changeModels	key:模型ID，vlue：新的父模型ID
	 * @throws Exception
	 */
	public void changeParentModelId(Progress prog,Map<String,String> changeModels)throws Exception;
	/**
	 * 删除表单模型
	 * @param formModelID	删除的表单模型ID
	 * @throws Exception
	 */
	public void deleteFormModel(String formModelID) throws Exception;
	
	/**
	 * 删除表单模型
	 * @param formModelID	删除的表单模型ID
	 * @throws Exception
	 */
	public void deleteFormModelAndChildren(String formModelID) throws Exception;
	/**
	 * 删除表单模型
	 * @param formModelIDs	删除的表单模型ID
	 * @throws Exception
	 */
	public void deleteFormModels(Set<String> formModelIDs) throws Exception;
	/**
	 * 检查删除的模型是否被其他模型引用
	 * @param formModelIds	删除的表单模型ID
	 * @return	校验结果
	 * @throws Exception
	 */
	public List<ValidationResult> verifyDeleteFormModels(Set<String> formModelIds)throws Exception;
	/**
	 * 强制删除模型，如果有被引用，将从其他模型中移除引用属性，同时子模型也有被删除
	 * @param prog 进度通知对象
	 * @param formModelIds	删除的表单模型ID
	 * @throws Exception
	 */
	public void forceDeleteFormModel(Progress prog,Set<String> formModelIds)throws Exception;
	
	public void clearCacheFormModel();
	/**
	 * 查询表单模型
	 * @param formModelID	模型ID
	 * @return	模型对象
	 * @throws Exception
	 */
	public FormModel queryFormModel(String formModelID)throws Exception;
	/**
	 * 查询表单模型,可选是否克隆缓存，当不需要对模型做修改时，设置不克隆缓存可提升执行性能，但要注意对缓存修改时不提交会污染缓存
	 * @param formModelID	模型ID
	 * @param cloneCache	是否克隆缓存
	 * @return	模型对象
	 * @throws Exception
	 */
	public FormModel queryFormModel(String formModelID,boolean cloneCache)throws Exception;
	/**
	 * 通过模型uuid查询表单模型
	 * @param uuid	模型uuid
	 * @return	模型对象
	 * @throws Exception
	 */
	public FormModel queryFormModelByUuid(String uuid)throws Exception;
	/**
	 * 查询子表单模型列表
	 * @param formModelID	模型ID
	 * @return	子模型列表
	 * @throws Exception
	 */
	public List<FormModel> queryChildModels(String formModelID)throws Exception;
	/**
	 * 查询所有表单模型
	 * @return	模型列表
	 * @throws Exception
	 */
	public List<FormModel> queryAllFormModel()throws Exception;
	
	public Set<String> queryAllChildModelIds(String formModelID)throws Exception;
	/**
	 * 根据包路径查询表单模型列表
	 * @param packagePath	模型包路径
	 * @return	模型列表
	 * @throws Exception
	 */
	public List<FormModel> queryFormModelByPackage(String packagePath)throws Exception;
	/**
	 * 分页查询表单模型结果集
	 * @param parentIds	父模型ID
	 * @param packagePath	模型包路径，填null时匹配所有
	 * @param keyword	搜索关键字，匹配模型名称或模型标签，填null时匹配所有
	 * @param pageNo	查询页码，页码从1开始
	 * @param pageSize	分页大小
	 * @return	表单模型结果集
	 * @throws Exception
	 */
	public ResultSet<FormModel> queryFormModelPage(List<String> parentIds, String packagePath, String keyword,int pageNo,int pageSize)throws Exception;
	/**
	 * 是否是指定模型的子模型或孙子模型
	 * @param formModelId
	 * @param targetFormModelId
	 * @return
	 * @throws Exception
	 */
	public boolean isInheritForm(String formModelId,String targetFormModelId)throws Exception;
	/**
	 * 查询模型的继承路径
	 * @param formModelId	模型ID
	 * @return	模型的祖先模型ID列表，从根路径开始，当前模型ID为列表末尾值
	 * @throws Exception
	 */
	public List<String> queryInheritPaths(String formModelId)throws Exception;
	/**
	 * 复制一份新的表单，表单中的嵌套模型uuid和附件重新获取设置
	 * @param form	表单对象
	 * @return	复制后的表单对象
	 * @throws Exception
	 */
	public Form copyForm(Form form)throws Exception;

	/**
	 * 复制一份新的表单，表单中的嵌套模型uuid和附件重新获取设置
	 * @param form
	 * @param targetModelId
	 * @return
	 * @throws Exception
	 */
	public Form copyForm(Form form,String targetModelId)throws Exception;
	/**
	 * 复制一份新的表单，表单中的嵌套模型uuid和附件重新获取设置
	 * @param form	表单对象
	 * @param ignoreCopyFields 忽略复制的属性
	 * @param copyRefAction 是否复制表单中的引用动作实例数据，复制时将一一创建动作实例的复本
	 * @return	复制后的表单对象
	 * @throws Exception
	 */
	public Form copyForm(Form form,Set<String> ignoreCopyFields,boolean copyRefAction)throws Exception;
	/**
	 * 创建表单
	 * @param form	表单对象
	 * @return	创建好的表单对象
	 * @throws Exception
	 */
	public Form createForm(IDao dao,Form form)throws Exception;
	/**
	 * 创建表单，携带表单操作观察者，在表单操作时执行相应回调处理
	 * @param prog	进度通知对象
	 * @param dao	
	 * @param form	表单对象
	 * @param observer	表单操作观察者
	 * @return
	 * @throws Exception
	 */
	public Form createForm(Progress prog,IDao dao,Form form,FormOpObserver observer)throws Exception;
	/**
	 * 批量创建表单对象
	 * @param dao
	 * @param list	表单对象列表
	 * @return
	 * @throws Exception
	 */
	public List<Form> createForms(IDao dao, List<Form> list)throws Exception;
	/**
	 * 批量创建表单，携带表单操作观察者，在表单操作时执行相应回调处理
	 * @param prog	进度通知对象
	 * @param dao	dao
	 * @param list	表单对象列表
	 * @param lockTarget 是否对依赖目标加读锁，为了快速插入，可以在外面自行加锁，无需显性释放，commit或者close 当前dao时会自动释放
	 * @param observer	表单操作观察者
	 * @return
	 * @throws Exception
	 */
	public List<Form> createForms(Progress prog,IDao dao, List<Form> list, boolean lockTarget,FormOpObserver observer)throws Exception;
	
	/**
	 * 更新表单
	 * @param form	表单对象
	 * @return
	 * @throws Exception
	 */
	public Form updateForm(IDao dao,Form form)throws Exception;
	/**
	 * 更新表单，携带表单操作观察者，在表单操作时执行相应回调处理
	 * @param prog	进度通知对象
	 * @param dao	
	 * @param form	表单对象
	 * @param observer	表单操作观察者
	 * @return
	 * @throws Exception
	 */
	public Form updateForm(Progress prog,IDao dao,Form form,FormOpObserver observer)throws Exception;
	/**
	 * 更新表单，可指定嵌套数据的更新模式，需要更新的表单属性和忽略更新的表单属性，携带表单操作观察者，在表单操作时执行相应回调处理
	 * @param dao
	 * @param form	表单对象
	 * @param mode	嵌套数据更新模式：Nothing（不更新），DeleteAndCreate（删除重建），IncrementUpdate（增量更新），DeleteAndCreateWithNewUuid（删除重建，重新分配uuid）
	 * @param updateFields	需要更新的表单属性，传null表示更新所有，不指定更新
	 * @param ignoreUpdateFields	忽略更新的表单属性，传null表示无须忽略
	 * @return	表单对象
	 * @throws Exception
	 */
	public Form updateForm(IDao dao,Form form,NestingTableUpdateMode mode,String[] updateFields,String[] ignoreUpdateFields)throws Exception;
	
	public Form updateForm(IDao dao,Form form,NestingTableUpdateMode mode,String[] updateFields,String[] ignoreUpdateFields,FormOpObserver observer)throws Exception;
	/**
	 * 批量更新表单
	 * @param prog	进度通知对象
	 * @param dao
	 * @param cnd	更新条件
	 * @param mapValue	更新的值
	 * @return 更新的记录数
	 * @throws Exception
	 */
	public int updateForms(Progress prog, IDao dao, String formModelId,Cnd cnd, Map<String, Object> mapValue,int commitBatchCount)throws Exception;
	/**
	 * 批量导入表单数据前的数据预处理操作，在数据导入导出时调用
	 * @param prog	进度通知对象
	 * @param dao	
	 * @param list	表单数据
	 * @throws Exception
	 */
	public void beforeBatchImportForms(Progress prog,IDao dao,List<Form> list) throws Exception;
	/**
	 * 批量导入表单数据
	 * @param prog	进度通知对象
	 * @param dao
	 * @param list	表单数据
	 * @throws Exception
	 */
	public void batchImportForms(Progress prog,IDao dao,List<Form> list) throws Exception;
	/**
	 * 查询表单
	 * @param formModelID	模型ID
	 * @param uuid	数据uuid
	 * @return	表单对象
	 * @throws Exception
	 */
	public Form queryForm(IDao dao,String formModelID,String uuid)throws Exception;
	/**
	 * 查询表单
	 * @param dao
	 * @param formModelID
	 * @param uuid
	 * @param compoundField	是否查询嵌套数据
	 * @param fields	指定查询的属性
	 * @return
	 * @throws Exception
	 */
	public Form queryForm(IDao dao, String formModelID, String uuid, boolean compoundField,String... fields)throws Exception;
	/**
	 * 查询表单属性
	 * @param dao
	 * @param formModelID	模型ID
	 * @param cnd	查询条件
	 * @param fields	属性code
	 * @return	只包含指定属性值的表单对象
	 * @throws Exception
	 */
	public Form queryFormFieldValue(IDao dao,String formModelID,Cnd cnd,String[] fields)throws Exception;
	/**
	 * 根据Uuid查询表单，会在所有模型中进行查找，模型数据较多时执行会比较缓慢，慎用
	 * @param formUuid	表单uuid
	 * @param formModelIds	表单模型ID
	 * @param fields	查询的表单属性，传空表示查询所有属性
	 * @return	表单对象
	 * @throws Exception
	 */
	public Form queryFormByUuid(String formUuid,List<String> formModelIds,String... fields)throws Exception;
	/**
	 * 根据编号查询表单
	 * @param formModelID	模型ID
	 * @param code	编号
	 * @return	表单对象
	 * @throws Exception
	 */
	public Form queryFormByCode(IDao dao,String formModelID,String code)throws Exception;
	/**
	 * 根据编号查询表单的uuid
	 * @param dao
	 * @param formModelId	模型ID
	 * @param code	编号
	 * @return	表单uuid
	 * @throws Exception
	 */
	public String queryFormUuidByCode(IDao dao,String formModelId,String code)throws Exception;
	/**
	 * 查询表单上的嵌套属性数据，并设置到表单上
	 * @param dao
	 * @param form
	 * @param nestingFields	嵌套属性code，选填，为null时查询所有嵌套属性
	 * @return
	 * @throws Exception
	 */
	public Form queryNestingTableData(IDao dao,Form form,String... nestingFields)throws Exception;
	/**
	 * 批量查询表单内的嵌套数据，只查询指定嵌套属性
	 * @param dao
	 * @param forms
	 * @param nestingFieldCodes
	 * @return
	 * @throws Exception
	 */
	public List<Form> batchQueryNestingTableData(IDao dao,List<Form> forms,Set<String> nestingFieldCodes)throws Exception;
	/**
	 * 根据搜索关键字构建全表检索条件表达式
	 * @param fields	检索属性列表
	 * @param keyword	搜索关键字
	 * @return	全表检索条件表达式
	 * @throws Exception
	 */
	public SqlExpressionGroup buildMatchQueryOfFields(List<FormField> fields,String keyword)throws Exception;
	/**
	 * 构建关联数据查询表达式
	 * @param fieldCode	关联属性code
	 * @param isMultiple	是否多选
	 * @param associationDatas	查询的关联值
	 * @return
	 */
	public SqlExpressionGroup buildAssociationDataQueryExpression(String fieldCode,boolean isMultiple,List<AssociationData> associationDatas);
	/**
	 * 查询表单分页结果
	 * @param formModelID	模型ID
	 * @param cnd	查询条件
	 * @param pageNo	分页页码，页码从1开始
	 * @param pageSize	分页大小
	 * @return	表单分页结果集
	 * @throws Exception
	 */
	public ResultSet<Form> queryFormPageWithoutNesting(IDao dao,String formModelID,Cnd cnd,int pageNo,int pageSize)throws Exception;
	/**
	 * 判断表单是否存在
	 * @param dao
	 * @param formModelID	模型ID
	 * @param formUuid	表单uuid
	 * @return
	 * @throws Exception
	 */
	public boolean isFormExist(IDao dao,String formModelID,String formUuid)throws Exception;
	/**
	 * 根据条件判断表单是否存在
	 * @param dao
	 * @param formModelID	模型ID
	 * @param cnd	条件
	 * @return
	 * @throws Exception
	 */
	public boolean isFormExistByCnd(IDao dao, String formModelID, Cnd cnd) throws Exception;
	/**
	 * 统计满足查询条件的表单记录数
	 * @param dao
	 * @param formModelID	模型ID
	 * @param cnd	条件
	 * @return	表单记录数
	 * @throws Exception
	 */
	public long countForm(IDao dao,String formModelID,Cnd cnd)throws Exception;
	/**
	 * 获取自增序号，可用于分配表单数据的自增编号
	 * @param modelTableName	模型表名
	 * @return	自增序号
	 * @throws Exception
	 */
	public long getIncreamentSeq(String modelTableName)throws Exception;
	
	/**
	 * 查询表单分页结果
	 * @param formModelID	模型ID
	 * @param cnd	条件
	 * @param pageNo	分页页码，页码从1开始
	 * @param pageSize	页码大小
	 * @param queryRowCount	是否查询表单记录数
	 * @param compoundField	是否查询嵌套属性数据
	 * @param fields	指定查询的表单属性，不传时查询所有属性
	 * @return	表单分页结果集
	 * @throws Exception
	 */
	public ResultSet<Form> queryFormPage(IDao dao,String formModelID,Cnd cnd,int pageNo,int pageSize,boolean queryRowCount,boolean compoundField,String... fields)throws Exception;
	/**
	 * 按自定义SQL查询表单分页结果，分页统计可以在Sql中使用 count(1) over() as totalCount，在extFields中设置totalCount查询出统计数量
	 * @param dao
	 * @param formModelID	模型ID
	 * @param sql	自定义查询SQL
	 * @param extFields 附带查询列，需要在自定义查询SQL中有定义该列的查询语句，查询记录数时需要携带totalCount，其他附带的查询列值都将设置在Form的extFields上
	 * @param cnd	条件
	 * @param pageNo	分页页码，页码从1开始
	 * @param pageSize	页码大小
	 * @return	表单分页结果集
	 * @throws Exception
	 */
	public ResultSet<Form> queryFormPageBySql(IDao dao,String formModelID, String sql, Set<String> extFields,Cnd cnd, int pageNo, int pageSize)throws Exception;
	/**
	 * 按自定义SQL查询表单分页结果，分页统计可以在Sql中使用 count(1) over() as totalCount，在extFields中设置totalCount查询出统计数量
	 * @param dao
	 * @param formModelID	模型ID
	 * @param sql	自定义查询SQL
	 * @param extFields	附带查询列，需要在自定义查询SQL中有定义该列的查询语句，查询记录数时需要携带totalCount，其他附带的查询列值都将设置在Form的extFields上
	 * @param replaceParam	查询变量（$开头的简单字符替换）
	 * @param varParam	查询参数（@开头的SQL语句参数，例如字符串会自动加单引号）
	 * @param cnd	条件
	 * @param pageNo	分页页码，页码从1开始
	 * @param pageSize	页码大小
	 * @return	表单分页结果集
	 * @throws Exception
	 */
	public ResultSet<Form> queryFormPageBySql(IDao dao,String formModelID, String sql, Set<String> extFields,Map<String, String> replaceParam,
			Map<String, Object> varParam,Cnd cnd, int pageNo, int pageSize)throws Exception;
//	public ResultSet<Form> queryFormPageBySql(IDao dao,String formModelID, String sql, Set<String> extFields,Map<String, Object> replaceParam,
//			Map<String, Object> queryParam, int pageNo, int pageSize)throws Exception;
	
	/**
	 * 按自定义SQL查询表单分页结果，分页统计可以在Sql中使用 count(1) over() as totalCount，在extFields中设置totalCount查询出统计数量
	 * @param dao
	 * @param formModelID	模型ID
	 * @param sql	自定义查询SQL
	 * @param extFields	附带查询列，需要在自定义查询SQL中有定义该列的查询语句，查询记录数时需要携带totalCount，其他附带的查询列值都将设置在Form的extFields上
	 * @param cnd	条件
	 * @param pageNo	分页页码，页码从1开始
	 * @param pageSize	页码大小
	 * @param compoundField	是否查询嵌套属性
	 * @return	表单分页结果集
	 * @throws Exception
	 */
	public ResultSet<Form> queryFormPageBySql(IDao dao,String formModelID, String sql, Set<String> extFields,Cnd cnd, int pageNo, int pageSize,boolean compoundField)throws Exception;

	/**
	 * 自定义查询语句，返回原始的查询结果，不做任何转换
	 * 返回结果中的DataRow为字段名（或Alias）对应库表记录值Object
	 * 如果需要总行数，可再调用queryLong另行查询
	 *  @param dao	dao数据操作句柄
	 * @param querySql	自定义的查询SQL
	 * @param pageNo	分页号
	 * @param pageSize	分页大小
	 *  样例：
	String sql = "select a.*, b.name as projectName from cjava_code a, cjava_project b where a.masterKey=b.uuid and b.name='默认项目'";
		ResultSet<DataRow> rs2 = queryDataRowsBySql(dao,sql, vars, params, 1, 20);
	 * @return 原始查询数据分页 注意：DataRow中的key一般都是小写，例如：a_masterkey、b_name等，不会因为SQL中字段名的大小写而改变，
	 * @throws Exception
	 */
	public List<DataRow> queryDataRowsBySql(IDao dao, String querySql, int pageNo, int pageSize)throws Exception;
	/**
	 * 从DAO服务中查询长整数值
	 * @param dao
	 * @param countSql	查询整数值SQL
	 * @param replaceParam	替换参数
	 * @param queryParam	表名替换参数
	 * @param dftValue	默认值
	 * @return
	 * @throws Exception
	 */
	public long queryLong(IDao dao, String countSql,Map<String, Object> replaceParam,
			Map<String, Object> queryParam,long dftValue)throws Exception;
	/**
	 * 删除表单
	 * @param formModelID	模型ID
	 * @param uuid	表单uuid
	 * @throws Exception
	 */
	public void deleteForm(IDao dao,String formModelID,String uuid)throws Exception;
	/**
	 * 删除表单,携带表单操作观察者，在表单操作时执行相应回调处理
	 * @param prog	进度通知对象
	 * @param dao
	 * @param formModelID	模型ID
	 * @param uuid	表单uuid
	 * @param observer	表单操作观察者
	 * @throws Exception
	 */
	public void deleteForm(Progress prog,IDao dao,String formModelID,String uuid,FormOpObserver observer)throws Exception;
	/**
	 * 按条件删除表单
	 * @param dao
	 * @param formModelID	模型ID
	 * @param cnd	条件
	 * @throws Exception
	 */
	public void deleteForm(IDao dao,String formModelID,Cnd cnd)throws Exception;
	/**
	 * 按条件删除表单
	 * @param dao
	 * @param formModelID	模型ID
	 * @param cnd	条件
	 * @param commitBatchCount 批量提交记录数，注意这里的批量提交不可回滚
	 * @throws Exception
	 */
	public void deleteForm(IDao dao,String formModelID,Cnd cnd,int commitBatchCount)throws Exception;
	/**
	 * 按条件删除表单,携带表单操作观察者，在表单操作时执行相应回调处理
	 * @param prog	进度通知对象
	 * @param dao
	 * @param formModelID	模型ID
	 * @param cnd	条件
	 * @param observer	表单操作观察者
	 * @throws Exception
	 */
	public void deleteForm(Progress prog,IDao dao,String formModelID,Cnd cnd,FormOpObserver observer)throws Exception;
	/**
	 * 按条件删除表单,携带表单操作观察者，在表单操作时执行相应回调处理
	 * @param prog	进度通知对象
	 * @param dao
	 * @param formModelID	模型ID
	 * @param cnd	条件
	 *  @param commitBatchCount 批量提交记录数，注意这里的批量提交不可回滚
	 * @param observer	表单操作观察者
	 * @throws Exception
	 */
	public void deleteForm(Progress prog,IDao dao,String formModelID,Cnd cnd,int commitBatchCount,FormOpObserver observer)throws Exception;
	/**
	 * 查询表单属性上的附件数据
	 * @param uuid	表单uuid
	 * @param fieldCode	表单属性Code
	 * @param fileName	文件名
	 * @return	附件对象
	 * @throws Exception
	 */
	public AttachData queryAttachData(IDao dao,String uuid,String fieldCode,String fileName)throws Exception;
	/**
	 * 查询表单属性上的附件字节数据
	 * @param dao
	 * @param uuid	表单uuid
	 * @param fieldCode	表单属性Code
	 * @param fileName	文件名
	 * @return	附件字节数据
	 * @throws Exception
	 */
	public byte[] queryAttachBytes(IDao dao,String uuid,String fieldCode,String fileName)throws Exception;
	
	/**
	 * 查询表单属性的附件列表
	 * @param dao
	 * @param uuid	表单uuid
	 * @param fieldCode	表单属性Code
	 * @return	附件列表
	 * @throws Exception
	 */
	public List<AttachData> queryAttachDatas(IDao dao,String uuid, String fieldCode) throws Exception ;
	/**
	 * 上传网络附件
	 * @param fileName
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public WebAttachData uploadWebAttach(String fileName,byte[] content)throws Exception;
	/**
	 * 上传网络附件
	 * @param fileName
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public WebAttachData uploadWebAttach(String fileName,InputStream in)throws Exception;
	
	/**
	 * 下载网络附件
	 * @param fileUuid
	 * @return
	 * @throws Exception
	 */
	public byte[] downloadWebAttach(String fileUuid)throws Exception;
	/**
	 * 打开网络附件传输流
	 * @param fileUuid
	 * @return
	 * @throws Exception
	 */
	public InputStream openWebAttachStream(String fileUuid)throws Exception;
	/**
	 * 获取网络附件的分享链接
	 * @param fileUuid
	 * @return
	 * @throws Exception
	 */
	public String getWebAttachShareUrl(String fileUuid)throws Exception;
	
	/**
	 * 查询表单属性的网络附件列表
	 * @param dao
	 * @param uuid	表单uuid
	 * @param fieldCode	表单属性Code
	 * @return	附件列表
	 * @throws Exception
	 */
	public List<WebAttachData> queryWebAttachDatas(IDao dao,String uuid, String fieldCode) throws Exception ;
	/**
	 *  根据条件返回查询结果的第一条记录，可查询在同一个事务下未提交的数据
	 * @param dao dao数据操作句柄
	 * @param cnd	查询条件
	 * @return
	 * @throws Exception
	 */
	public CDoLink queryFristLink(IDao dao, Cnd cnd) throws Exception;
	/**
	 * 根据条件查询链接列表，可查询在同一个事务下未提交的数据
	 * @param dao dao数据操作句柄
	 * @param cnd	查询条件
	 * @return	链接列表
	 * @throws Exception
	 */
	public List<CDoLink> queryLinkList(IDao dao, Cnd cnd) throws Exception;
	/**
	 * 根据条件查询链接分页，可查询在同一个事务下未提交的数据
	 * @param dao dao数据操作句柄
	 * @param cnd	查询条件
	 * @param pager 分页参数
	 * @return	链接分页
	 * @throws Exception
	 */
	public ResultSet<CDoLink> queryLinkPage(IDao dao, Cnd cnd,CPager pager) throws Exception;
	/**
	 * 创建链接，由调用方控制dao的提交操作
	 * @param dao dao数据操作句柄
	 * @param link 链接
	 * @throws Exception
	 */
	
	public void createLink(IDao dao, CDoLink link) throws Exception;
	/**
	 * 删除链接，由调用方控制dao的提交操作
	 * @param dao dao数据操作句柄
	 * @param link 链接
	 * @throws Exception
	 */
	
	public void deleteLink(IDao dao, CDoLink link) throws Exception;
	/**
	 * 通过条件删除链接，由调用方控制dao的提交操作
	 * @param dao dao数据操作句柄
	 * @param cnd	查询条件
	 * @throws Exception
	 */
	
	public void deleteLinkByCnd(IDao dao, Cnd cnd) throws Exception;
	/**
	 * 导出表单数据，json格式
	 * @param prog	进度通知对象
	 * @param expImpIntf	重载的导入导出接口，传null使用默认的导入导出接口FormDataExpImp
	 * @param formModelId	模型ID
	 * @param cnd	查询条件
	 * @return	压缩包数据
	 * @throws Exception
	 */
	public Pair<String, byte[]> exportFormData(Progress prog,ExportImportIntf expImpIntf,String formModelId,Cnd cnd)throws Exception;
	/**
	 * 导入表单数据，json格式
	 * @param prog	进度通知对象
	 * @param expImpIntf	重载的导入导出接口，传null使用默认的导入导出接口FormDataExpImp
	 * @param formModelId	模型ID
	 * @param zipFile	压缩包数据
	 * @throws Exception
	 */
	public void importFormData(Progress prog,ExportImportIntf expImpIntf,String formModelId,Pair<String, byte[]> zipFile)throws Exception;
	/**
	 * 查询指定列去重结果
	 * @param cteSql	CTE查询语句
	 * @param mainTableAlias	要过滤去重的主表视图
	 * @param distintColumns	去重列
	 * @param cnd	条件
	 * @param pageNo	分页页码，页码从1开始
	 * @param pageSize	分页大小
	 * @return
	 * @throws Exception
	 */
	public ResultSet<DataRow> queryColumnDistintValues(Map<String, String> cteSql, String mainTableAlias,
			Set<String> distintColumns, Cnd cnd, int pageNo, int pageSize) throws Exception ;
}
