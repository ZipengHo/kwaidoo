package cell.gpf.dc.runtime;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import com.cdao.dto.DataRow;
import com.cdao.model.CDoLink;
import com.kwaidoo.ms.tool.ToolUtilities;

import bap.cells.Cells;
import cell.ServiceCellIntf;
import cell.cdao.IDao;
import cell.gpf.cfg.IRuntimeContext;
import cmn.dto.Progress;
import gpf.adur.data.Form;
import gpf.adur.data.FormField;
import gpf.adur.data.ResultSet;
import gpf.dc.action.param.NodeStatusChangeEventParam;
import gpf.dc.config.OperateLogStatusHookDto;
import gpf.dc.config.PDC;
import gpf.dc.intf.FormOpObserver;
import gpf.dc.runtime.AbsSelectQuery;
import gpf.dc.runtime.DoneForm;
import gpf.dc.runtime.OperateLog;
import gpf.dc.runtime.PDCForm;
import gpf.dc.runtime.PDFForm;
import gpf.dc.runtime.PDFFormDoneQuery;
import gpf.dc.runtime.PDFFormProgressQuery;
import gpf.dc.runtime.PDFFormQueryOption;
import gpf.dc.runtime.PDFFormTodoQuery;
import gpf.dc.runtime.PDFInstance;
import gpf.dc.runtime.PdfInstanceProgress;
import gpf.dc.runtime.PdfInstanceStatus;
import gpf.dc.runtime.ToDoForm;
import gpf.dc.runtime.UnionQuery;
import gpf.dto.cfg.runtime.RunningThreadInfo;
import gpf.dto.model.data.FormPrivilegeDto;
import gpf.exception.CloseControlFlowException;
import gpf.exception.DeleteControlFlowException;
import gpf.exception.SubmitRunErrorException;
import gpf.i18n.GpfConst;
import gpf.model.observer.OperateLogStatusChangeProxyImpl;
/**
 * 流程运行管理接口
 * 管理流程表单提交执行、流程实例状态操作、流程操作记录操作
 */
public interface IPDFRuntimeMgr extends ServiceCellIntf{

	static IPDFRuntimeMgr get() {
		return Cells.get(IPDFRuntimeMgr.class);
	}
	
	public final static String CONFLICT_OPERATE_LOG_FIELD_NAME_PREFIX = "流程单步操作_";
	public final static String CONFLICT_FIELD_NAME_PREFIX = "流程_";
	
	public final static Set<String> OperateLogExtFields = ToolUtilities.newHashSet(GpfConst.STEP_UUID, OperateLog.CreatorCnName,OperateLog.Assignee, OperateLog.AssigneeCnName,OperateLog.NodeKey,
			   OperateLog.NodeName, OperateLog.StepName, OperateLog.StepOperator,OperateLog.StepOperatorCnName, OperateLog.ExecuteTime, OperateLog.Status,
			   "stepError"
			//   ,OperateLog.Assigner,OperateLog.AssignTime
			 );
	public final static List<String> PdfFormSystemFields = Arrays.asList(PDCForm.CreateTime,PDCForm.Creator,PDCForm.UpdateTime);
	
	/**
	 * 构建新的运行上下文
	 * @return
	 * @throws Exception
	 */
	public IDCRuntimeContext newRuntimeContext()throws Exception;
	/**
	 * 创建流程实例，只创建步提交，已弃用
	 * @param rtx	运行上下文中必须具备以下参数：
	 * rtx.setOperator("User_001");//用户编号
	 * rtx.setUserModel("");//用户模型ID
	 * rtx.setOrgModel("");//组织模型ID
	 * rtx.setActionName("");//提交动作
	 * @param pdfUuid	流程模型ID
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public PDFInstance createPDFInstance(IDCRuntimeContext rtx,String pdfUuid)throws Exception;
	
	/**
	 * 创建流程实例，并提交开始节点表单
	 * @param rtx	运行上下文,必须具备以下参数：
	 * rtx.setOperator("User_001");//用户编号
	 * rtx.setUserModel("");//用户模型ID
	 * rtx.setOrgModel("");//组织模型ID
	 * rtx.setActionName("");//提交动作
	 * @param pdfUuid	流程模型ID
	 * @param form	流程表单
	 * @param	observer	流程表单操作观察者
	 * @return
	 * @throws Exception
	 */
	public PDFInstance createPDFInstance(IDCRuntimeContext rtx, String pdfUuid, PDCForm form,FormOpObserver observer)throws Exception;
	/**
	 * 查询流程实例
	 * @param pdfInstUuid	流程实例uuid
	 * @return
	 * @throws Exception
	 */
	public PDFInstance queryPDFInstance(String pdfInstUuid)throws Exception;
	/**
	 * 更新流程实例，可能会影响流程运行，谨慎操作
	 * @param pdfInstace
	 * @return
	 * @throws Exception
	 */
	public PDFInstance updatePDFInstace(PDFInstance pdfInstance)throws Exception;
	
	/**
	 * 更新流程实例，可能会影响流程运行，谨慎操作
	 * @param pdfInstace
	 * @return
	 * @throws Exception
	 */
	public PDFInstance updatePDFInstace(IDao dao,PDFInstance pdfInstance)throws Exception;
	/**
	 * 同步流程实例，当流程模型节点或关系发生变更时，需要同步流程实例保持于流程模型定义上的配置一致
	 * @param pdfUuid	流程模型ID
	 * @param pdfInstUuids	流程实例uuid列表
	 * @throws Exception
	 */
	public void synchornizePDFInstances(Progress prog,String pdfUuid, List<String> pdfInstUuids)throws Exception;
	/**
	 * 同步所有流程实例，当流程模型节点或关系发生变更时，需要同步流程实例保持于流程模型定义上的配置一致
	 * @param prog	进度通知对象
	 * @param pdfUuid	流程模型ID
	 * @throws Exception
	 */
	public void synchornizeAllPDFInstances(Progress prog,String pdfUuid)throws Exception;
	/**
	 * 查询PDF关联的流程实例关系
	 * @param dao
	 * @param pdfUuid	流程模型ID
	 * @return	srcKey 流程模型中控制流cfg的uuid，dstKey 流程实例Uuid
	 * @throws Exception
	 */
	public List<CDoLink> queryPDFInstanceLinksByPdfUuid(IDao dao,String pdfUuid)throws Exception;
	/**
	 * 删除流程实例
	 * @param pdfInstUuid	流程实例Uuid
	 * @throws Exception
	 */
	public void deletePDFInstance(String pdfInstUuid)throws Exception;
	/**
	 * 判断流程实例是否是启动状态
	 * @param pdfInstUuid	流程实例Uuid
	 * @return
	 * @throws Exception
	 */
	public boolean pdfInstanceIsStarted(String pdfInstUuid)throws Exception;
	/**
	 * 启动流程实例
	 * @param pdfInstUuid	流程实例Uuid
	 * @throws Exception
	 */
	public void startPDFInstance(String pdfInstUuid) throws Exception ;
	/**
	 * 停止流程实例
	 * @param pdfInstUuid	流程实例Uuid
	 * @throws Exception
	 */
	public void stopPDFInstance(String pdfInstUuid) throws Exception ;
	/**
	 * 重置流程实例的节点状态，如果流程实例已被关闭时且没有节点状态时则重启流程实例
	 * @param pdfInstUuid	流程实例Uuid
	 * @param nodeKeys	重置节点的key
	 * @throws Exception
	 */
	public void resetPDFInstance(String pdfInstUuid, List<String> nodeKeys) throws Exception;
	/**
	 * 重置流程实例的所有节点状态，如果流程实例已被关闭时则重启流程实例
	 * @param pdfInstUuid	流程实例Uuid
	 * @throws Exception
	 */
	public void resetPDFInstanceAllNode(String pdfInstUuid) throws Exception;
	/**
	 * 查询流程实例的节点状态
	 * @param pdfInstUuid	流程实例Uuid
	 * @return
	 * @throws Exception
	 */
	public PdfInstanceStatus queryPDFInstanceStatus(String pdfInstUuid) throws Exception;
	/**
	 * 创建并返回提交后的PDC表单
	 * @param pdfUuid	流程模型ID
	 * @param rtx	运行上下文,必须具备以下参数：
	 * rtx.setOperator("User_001");//用户编号
	 * rtx.setUserModel("");//用户模型ID
	 * rtx.setOrgModel("");//组织模型ID
	 * rtx.setActionName("");//提交动作
	 * @param form	PDC表单
	 * @return
	 * @throws Exception
	 */
	public PDCForm createAndSubmitPDCForm(String pdfUuid,IDCRuntimeContext rtx,PDCForm form)throws Exception;
	public PDCForm createAndSubmitPDCForm(String pdfUuid,String nodeKey, IDCRuntimeContext rtx,PDCForm form)throws Exception;
	public PDCForm createAndSubmitPDCForm(String pdfUuid,IDCRuntimeContext rtx,PDCForm form,FormOpObserver observer)throws Exception;
	/**
	 * 订阅流程状态变更，订阅后需要在流程的触发表配置配置触发事件，监听指令为GpfEvent.EVT_NODE_STATUS_CHANGED，配置相关的处理动作,代码示例
		PDFInstance srcPdfInst = input.getRtx().getPdfInstance();
		RefPDCNode refPdcNode = input.getRtx().getRefPDCNode();
		String relatePdfUuid = input.getPdfUuid();
		PDF relatePdf = IPDFMgr.get().queryPDF(relatePdfUuid);
		String startNode = relatePdf.getStartNode();
		IDCRuntimeContext relateRtx = IPDFRuntimeMgr.get().newRuntimeContext();
		PDCForm relatePdcForm = IPDFRuntimeMgr.get().newStartForm(relateRtx, relatePdfUuid);
		relateRtx.setOperator(input.getRtx().getOperator());
		relateRtx.setActionName(input.getActionName());
		PDFInstance relatePdfInst = IPDFRuntimeMgr.get().createRelatePDFInstanceAndSubmit(srcPdfInst.getUuid(), refPdcNode.getKey(), relatePdfUuid, startNode, relateRtx, relatePdcForm);
		//订阅流程的状态变化
//		subscribe// 源流程实例 源节点 目标流程实例，目标节点
		NodeStatusChangeEventParam eventParam = new NodeStatusChangeEventParam();
		eventParam.setDataUuid(input.getRtx().getTotalForm().getUuid());
		eventParam.setNodeKey(refPdcNode.getKey());
		eventParam.setPdfUuid(input.getRtx().getPdfUuid());
		eventParam.setPdfInstUuid(srcPdfInst.getUuid());
		eventParam.setSubscribePdfInstUuid(relatePdfInst.getUuid());
		RefPDCNode subscribeNode = null;
		for(RefPDCNode node : relatePdfInst.getNodes()) {
			if(CmnUtil.isStringEqual(node.getName(), "结束")) {
				subscribeNode = node;
				break;
			}
		}
		eventParam.setSubscribeNodeKey(subscribeNode.getKey());
		eventParam.setStatus(Arrays.asList(OperateLogStatus.finish.name()));
		IPDFRuntimeMgr.get().subscribeNodeStatusChanged(input.getRtx().getDao(), eventParam);
	 * @param dao
	 * @param eventParam 订阅节点状态变更事件参数
	 * @throws Exception
	 */
	public void subscribeNodeStatusChanged(IDao dao, NodeStatusChangeEventParam eventParam) throws Exception; 
	public PDFInstance createRelatePDFInstanceAndSubmit(String srcPdfInstUuid, String srcNodeKey,String pdfUuid, String nodeKey,
			IDCRuntimeContext RC, PDCForm form)throws Exception;
	public PDFInstance querySrcPDFInstance(String pdfInstUuid)throws Exception;
	public List<PDFInstance> queryDstPDFInstanceList(String pdfInstUuid,String nodeKey)throws Exception;
	/**
	 * 提交PDC表单
	 * @param pdfInstUuid	流程实例uuid
	 * @param nodeKey	提交节点Key
	 * @param rtx	运行上下文，需要具备以下参数：
	 * rtx.setPdfUuid("");//流程模型ID
	 * rtx.setOperator("User_001");//用户编号
	 * rtx.setActionName("");//提交动作
	 * @param form	PDC表单
	 * @throws Exception
	 */
	public PDCForm submitPDCForm(String pdfInstUuid, String nodeKey, IDCRuntimeContext rtx,PDCForm form)throws Exception;
	/**
	 * 提交PDC表单
	 * @param pdfInstUuid	流程实例uuid
	 * @param nodeKey	提交节点Key
	 * @param rtx	运行上下文
	 * @param form	PDC表单
	 * @param observer	表单操作观察者
	 * @throws Exception
	 */
	public PDCForm submitPDCForm(String pdfInstUuid, String nodeKey, IDCRuntimeContext rtx,PDCForm form,FormOpObserver observer)throws Exception;
	
	/**
	 * 初始化开始节点的PDC表单
	 * @param rtx	运行上下文，必须具备以下参数：
	 * rtx.setOperator("User_001");//用户编号
	 * rtx.setUserModel("");//用户模型ID
	 * rtx.setOrgModel("");//组织模型ID
	 * @param pdfUuid	流程模型ID
	 * @return
	 * @throws Exception
	 */
	public PDCForm newStartForm(IDCRuntimeContext rtx,String pdfUuid)throws Exception;
	/**
	 * 初始化开始节点的PDC表单，可选是否执行鉴权和确权动作
	 * @param rtx	运行上下文，必须具备以下参数：
	 * rtx.setOperator("User_001");//用户编号
	 * rtx.setUserModel("");//用户模型ID
	 * rtx.setOrgModel("");//组织模型ID
	 * @param pdfUuid	流程模型ID
	 * @param executeAction	是否执行鉴权和确权动作
	 * @return
	 * @throws Exception
	 */
	public PDCForm newStartForm(IDCRuntimeContext rtx,String pdfUuid,boolean executeAction)throws Exception;
	/**
	 * 根据指定表单构建开始节点的表单副本
	 * @param rtx	运行上下文，必须具备以下参数：
	 * rtx.setOperator("User_001");//用户编号
	 * rtx.setUserModel("");//用户模型ID
	 * rtx.setOrgModel("");//组织模型ID
	 * @param pdfUuid	流程模型ID
	 * @param executeAction	是否执行鉴权和确权动作
	 * @param copyForm	复制表单
	 * @return
	 * @throws Exception
	 */
	public PDCForm newStartForm(IDCRuntimeContext rtx, String pdfUuid, boolean executeAction, PDCForm copyForm)throws Exception;
	/**
	 * 查询流程实例总表单数据
	 * @param dao
	 * @param pdfUuid	流程模型ID
	 * @param formUuid	总表单uuid
	 * @param fields	查询属性（可选，默认查所有，排除操作记录数据）
	 * @return
	 * @throws Exception
	 */
	public Form queryTotalForm(IDao dao,String pdfUuid, String formUuid, String... fields)throws Exception;
	/**
	 * 根据条件查询流程实例总表单数据
	 * @param dao
	 * @param pdfUuid	流程模型ID
	 * @param cnd	查询条件
	 * @param fields	查询属性（可选，默认查所有，排除操作记录数据）
	 * @return
	 * @throws Exception
	 */
	public Form queryTotalForm(IDao dao,String pdfUuid, Cnd cnd, String... fields)throws Exception;
	/**
	 * 查询PDC表单
	 * 如：try(IDao dao = IDaoService.get().newDao()) {
			IDCRuntimeContext rtx = IPDFRuntimeMgr.get().newRuntimeContext();
			rtx.setPdfUuid(pdfUuid);
			rtx.setOperator(user);
			String opLogUuid = cdo.getOpLogUuid();
			rtx.setDao(dao);
			PDCForm pdcForm =IPDFRuntimeMgr.get().queryPDCForm(rtx, pdfUuid, opLogUuid);
			return pdcForm;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	 * @param rtx	运行上下文，必须具备以下参数：
	 * rtx.setOperator("User_001");//用户编号
	 * rtx.setUserModel("");//用户模型ID
	 * rtx.setOrgModel("");//组织模型ID
	 * @param pdfUuid	流程模型ID
	 * @param opLogUuid	操作记录uuid
	 * @return
	 * @throws Exception
	 */
	public PDCForm queryPDCForm(IDCRuntimeContext rtx,String pdfUuid, String opLogUuid,String... fields)throws Exception;
	/**
	 * 查询PDC表单，可选是否执行鉴权和确认动作
	 * @param rtx	上下文参数
	 * @param pdfUuid	流程模型ID
	 * @param opLogUuid	操作记录唯一标识  
	 * @param executeAction	是否执行鉴权和确认动作
	 * @param fields
	 * @return
	 * @throws Exception
	 */
	public PDCForm queryPDCForm(IDCRuntimeContext rtx,String pdfUuid, String opLogUuid,boolean executeAction,String... fields)throws Exception;
	/**
	 * 查询PDC历史表单，从历史的交互单数据获取
	 * @param pdfUuid
	 * @param opLogUuid
	 * @return
	 * @throws Exception
	 */
	public PDCForm queryHistoryPDCForm(String pdfUuid,String opLogUuid)throws Exception;
	/**
	 * 模拟自动节点路由，跳过指定路由节点的提交
	 * @param pdfUuid	流程模型ID
	 * @param pdfInstUuid	流程实例
	 * @param routerPaths	节点路由	key：节点	value : 离开节点
	 * @throws Exception
	 */
	public void mockRunRouterPaths(String pdfUuid,String pdfInstUuid, Map<String,Set<String>> routerPaths)throws Exception;
	/**
	 * 计算表单权限
	 * @param rtx	上下文参数，需要在上下文中包含，必须具备以下参数：
	 * rtx.setPdfUuid("");//流程模型ID
	 * rtx.setOperator("User_001");//用户编号
	 * rtx.setUserModel("");//用户模型ID
	 * rtx.setOrgModel("");//组织模型ID
	 * @param pdc	PDC
	 * @param pdcForm	节点表单
	 * @return
	 * @throws Exception
	 */
	public FormPrivilegeDto caculateFormPrivilegeDto(IDCRuntimeContext rtx,PDC pdc,PDCForm pdcForm) throws Exception ;
	/**
	 * 根据节点Key查找最新的PDC表单
	 * @param rtx	上下文参数
	 * @param pdfUuid 流程模型ID
	 * @param pdfInstUuid	流程实例Uuid
	 * @param nodeKey	指定节点key
	 * @return
	 * @throws Exception
	 */
	public PDCForm queryLatestPDCForm(IDCRuntimeContext rtx, String pdfUuid,String pdfInstUuid, String nodeKey,String... fields)throws Exception;
	
	public PDCForm queryLatestPDCForm(IDCRuntimeContext RC, String pdfUuid,String pdfInstUuid, String nodeKey,boolean executeAction,String... fields)throws Exception;
	
	/**
	 * 查询表单上的嵌套属性数据，并设置到表单上
	 * @param dao
	 * @param form
	 * @param nestingFields	嵌套属性code，选填，为null时查询所有嵌套属性
	 * @return
	 * @throws Exception
	 */
	public PDCForm queryNestingTableData(IDao dao,PDCForm form,String... nestingFields)throws Exception;
	/**
	 * 查询PDF表单的属性列表
	 * @param pdfUuid
	 * @return
	 * @throws Exception
	 */
	public List<FormField> queryPDFFormFields(String pdfUuid)throws Exception;
	/**
	 * 查询PDF表单的属性列表
	 * @param pdfUuid
	 * @param addOpLogFieldPrefix 表单属性中的操作记录属性名称是否加上前缀
	 * @return
	 * @throws Exception
	 */
	public List<FormField> queryPDFFormFields(String pdfUuid,boolean addOpLogFieldPrefix) throws Exception ;
	
	/**
	 * 获取流程视图中附加查询的字段定义列表,如状态枚举、错误详情等
	 * @return
	 */
	public List<FormField> queryPdfFormExtQueryFields()throws Exception;
	
	/**
	 * 构建PDC表单查询的默认CTE语句,默认所有数据,不带权限过滤
	 * @param pdfUuid
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> buildPDFFormQueryCteSql(String pdfUuid,PDFFormQueryOption queryOption)throws Exception;
	public String buildPDFFormQuerySql(String pdfUuid,PDFFormQueryOption queryOption,Cnd cnd,SqlExpressionGroup privilegeExpr)throws Exception;
	public String buildPDFFormCountSql(String pdfUuid,PDFFormQueryOption queryOption,Cnd cnd,SqlExpressionGroup privilegeExpr)throws Exception;
	
	/**
	 * 查询PDF表单分页
	 * @param pdfUuid	流程模型ID
	 * @param cnd	查询条件
	 * @param privilegeExpr	权限表达式
	 * @param pageNo	查询页码，从1开始
	 * @param pageSize	分页大小
	 * @return
	 * @throws Exception
	 */
	public ResultSet<PDFForm> queryPDFFormPage(String pdfUuid, Cnd cnd, SqlExpressionGroup privilegeExpr, int pageNo,int pageSize)throws Exception;
	/**
	 * 查询PDF表单分页
	 * @param pdfUuid	流程模型ID
	 * @param queryOption	PDF表单查询参数，用于提前筛选查询结果集，优化性能时使用
	 * @param cnd	查询条件
	 * @param privilegeExpr	权限表达式
	 * @param pageNo	查询页码，从1开始
	 * @param pageSize	分页大小
	 * @return	
	 * @throws Exception
	 */
	public ResultSet<PDFForm> queryPDFFormPage(String pdfUuid, PDFFormQueryOption queryOption,Cnd cnd, SqlExpressionGroup privilegeExpr, int pageNo,int pageSize)throws Exception;
	
	/**
	 * 查询PDF表单分页
	 * @param pdfUuid	流程模型ID
	 * @param queryOption	PDF表单查询参数，用于提前筛选查询结果集，优化性能时使用
	 * @param cnd	查询条件
	 * @param privilegeExpr	权限表达式
	 * @param pageNo	查询页码，从1开始
	 * @param pageSize	分页大小
	 * @param queryRowCount 是否查询记录数
	 * @param compoundField	是否查询嵌套属性数据
	 * @return
	 * @throws Exception
	 */
	public ResultSet<PDFForm> queryPDFFormPage(String pdfUuid, PDFFormQueryOption queryOption,Cnd cnd, SqlExpressionGroup privilegeExpr, int pageNo,int pageSize,boolean queryRowCount,boolean compoundField)throws Exception;
	/**
	 * 通过自定义SQL查询PDF表单分页，可使用count(1) voer() as totalcount统计总记录数
	 * @param pdfUuid	流程模型ID
	 * @param querySql	自定义查询SQL
	 * @param extFields	附带查询的属性
	 * @param cnd	查询条件
	 * @param pageNo	查询页码，从1开始
	 * @param pageSize	分页大小
	 * @return
	 * @throws Exception
	 */
	public ResultSet<PDFForm> queryPDFFormPageBySql(String pdfUuid, String querySql,Set<String> extFields,Cnd cnd, int pageNo,int pageSize)
			throws Exception;
	/**
	 * 查询待办数据，待办数据是通过在节点Start时在操作记录中设置接收人产生
	 * @param user	用户编号
	 * @param unionQuery	集合查询配置，将多条流程的待办列表构建成一个集合查询视图
	 * @param cnd	查询条件
	 * @param pageNo	查询页码，从1开始
	 * @param pageSize	分页大小
	 * @return
	 * @throws Exception
	 */
	public ResultSet<ToDoForm> queryToDoPage(String user, UnionQuery<PDFFormTodoQuery> unionQuery,
			Cnd cnd, int pageNo,int pageSize)throws Exception;
	/**
	 * 查询已办数据，已办数据是在节点执行后通过操作记录的操作人产生
	 * @param user	用户编号
	 * @param unionQuery	集合查询配置，将多条流程的已办列表构建成一个集合查询视图
	 * @param cnd	查询条件
	 * @param pageNo	查询页码，从1开始
	 * @param pageSize	分页大小
	 * @return
	 * @throws Exception
	 */
	public ResultSet<DoneForm> queryDonePage(String user, UnionQuery<PDFFormDoneQuery> unionQuery, Cnd cnd, int pageNo,int pageSize)
			throws Exception;
	/**
	 * 查询集合视图
	 * @param dao
	 * @param unionQuery	集合视图查询配置
	 * @param cnd	查询条件
	 * @param pageNo	查询页码，从1开始
	 * @param pageSize	分页大小
	 * @return
	 * @throws Exception
	 */
	public <T extends AbsSelectQuery> ResultSet<DataRow> queryUnionPage(IDao dao,UnionQuery<T> unionQuery, Cnd cnd, int pageNo,int pageSize)
			throws Exception;
	/**
	 * 查询流程追踪
	 * @param user	用户编号
	 * @param unionQuery	
	 * @param cnd	查询条件
	 * @param pageNo	查询页码，从1开始
	 * @param pageSize	分页大小
	 * @return
	 * @throws Exception
	 */
	public ResultSet<PdfInstanceProgress> queryProgressPage(String user,UnionQuery<PDFFormProgressQuery> unionQuery,Cnd cnd, int pageNo,int pageSize)throws Exception;
	/**
	 * 懒查询用户的待办数量
	 * @param user	用户编号
	 * @param unionQuery	待办集合视图查询配置
	 * @param refreshNow	是否立即查询
	 * @return
	 * @throws Exception
	 */
	public long lazyQueryTodoCount(String user, UnionQuery<PDFFormTodoQuery> unionQuery,boolean refreshNow)throws Exception;
	/**
	 * 查询用户的待办数量
	 * @param user	用户编号
	 * @param unionQuery	待办集合视图查询配置
	 * @return
	 * @throws Exception
	 */
	public long queryTodoCount(String user, UnionQuery<PDFFormTodoQuery> unionQuery)throws Exception;
	
	//---------------------------------------操作日志-----------------------------------------------------
	/**
	 * 查询操作日志分页
	 * @param pdfUuid
	 * @param pdfFormUuid
	 * @param cnd
	 * @param pager
	 * @return
	 * @throws Exception
	 */
	public ResultSet<OperateLog> queryOperateLogPage(String pdfUuid, String pdfFormUuid, Cnd cnd, int pageNo,int pageSize)throws Exception;
	
	public OperateLog queryOperateLog(String pdfUuid,String opLogUuid,String... fields)throws Exception;
	/**
	 * 根据当前操作记录查询上一步操作记录
	 * @param opLogUuid
	 * @return
	 * @throws Exception
	 */
	public OperateLog queryPreviousOperateLog(String pdfUuid,String opLogUuid)throws Exception;
	
	public ResultSet<OperateLog> queryPreviousOperateLogPage(String pdfUuid,String opLogUuid, Cnd cnd, int pageNo,int pageSize) throws Exception; 
	/**
	 * 忽略指定操作记录的状态，用于一些错误状态的修复
	 * @param opLogUuid
	 * @param ignoreReason
	 * @throws Exception
	 */
	public void ignoreOperateLog(String pdfUuid,String opLogUuid,String user,String ignoreReason)throws Exception;
	/**
	 * 删除流程历史操作记录，谨慎操作，
	 * @param prog
	 * @param pdfUuid
	 * @param stayLogKeepDay stay日志保留天数
	 * @throws Exception
	 */
	public void deleteStayOperateLog(Progress prog,String pdfUuid,Long stayLogKeepDay)throws Exception;
	//---------------------------------------------------------------------------------------------------------
	/**
	 * 保存流程实例的运行上下文参数
	 * @param dao
	 * @param pdfInstUuid	流程实例Uuid
	 * @param key	参数名
	 * @param value	参数值
	 * @throws Exception
	 */
	public void savePDFInstanceContext(IDao dao,String pdfInstUuid,String key,Object value)throws Exception;
	/**
	 * 获取流程实例 持久化后的运行上下文参数
	 * @param dao
	 * @param pdfInstUuid	流程实例Uuid
	 * @param key	参数名
	 * @return
	 * @throws Exception
	 */
	public Object getPDFInstanceContext(IDao dao,String pdfInstUuid,String key)throws Exception;
	/**
	 * 查询所有流程上下文参数
	 * @param dao
	 * @param pdfInstUuid	流程实例Uuid
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> queryAllPDFInstanceContext(IDao dao, String pdfInstUuid)throws Exception;
	
	public ResultSet<DataRow> queryColumnDistintValues(Map<String,String> cteSql,String mainTableAlias,Set<String> distintColumns,Cnd cnd,int pageNo,int pageSize)throws Exception;
	/**
	 * 抛出删除流程实例的信号，在流程节点提交运行时生效
	 * @param pdfInstance
	 * @param nodeName
	 * @param msg
	 * @throws DeleteControlFlowException
	 */
    public void throwDeletePDFInstance(String pdfInstance,String nodeName,String msg) throws DeleteControlFlowException;
    /**
           * 抛出关闭流程实例的信号，在流程节点提交运行时生效
     * @param pdfInstance
     * @param nodeName
     * @param msg
     * @throws CloseControlFlowException
     */
    public void throwClosePDFInstance(String pdfInstance,String nodeName,String msg) throws CloseControlFlowException;
    /**
     * 抛出提交表单出错的信号，将更新流程节点的状态为出错
     * @param pdfInstance
     * @param nodeName
     * @param msg
     * @param cause 根源异常，可为null
     * @throws Exception
     */
    public void throwSubmitError(String pdfInstance, String nodeName, String msg,Throwable cause)throws SubmitRunErrorException;
    
    public void notifyOperateStatusChanged(OperateLogStatusChangeProxyImpl observerImpl,String invokeMethod,IRuntimeContext rtx,OperateLogStatusHookDto option) ;
    
  //---------------------------------节点运行线程管理------------------------------------------------------------------------
    /**
     * 查询正在运行节点的线程信息
     * @return
     * @throws Exception
     */
	public List<RunningThreadInfo> getRunningNodeInfos(boolean onlyBlocked)throws Exception;
	/**
	 * 获取线程堆栈信息
	 * @param threadId
	 * @return
	 * @throws Exception
	 */
	public String getThreadStackInfo(long threadId)throws Exception;
	/**
	 * 获取所有线程堆栈信息
	 * @param onlyBlocked
	 * @return
	 * @throws Exception
	 */
	public String getAllThreadStackInfo(boolean onlyBlocked)throws Exception;
	/**
	 * 尝试中断线程，注意，阻塞的线程有可能中断失败，如在执行数据库执行语句
	 * @param threadId	线程ID
	 * @param timeOut	超时时间，单位：毫秒
	 * @throws Exception
	 */
	public void tryInterruptThread(long threadId,long timeOut)throws Exception; 
}
