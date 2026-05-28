package cell.gpf.dc.basic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.AviatorEvaluatorInstance;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.exception.FunctionNotFoundException;
import com.googlecode.aviator.runtime.type.AviatorFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.utils.Env;
import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.common.util.StringUtils;

import bap.cells.BasicServiceCell;
import bap.cells.Cells;
import bap.cells.exception.ClassLoaderConflictException;
import cell.CellIntf;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.dc.cache.ICacheMgr;
import cmn.util.ClassUtil;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.adur.data.Form;
import gpf.adur.data.FormModel;
import gpf.adur.data.ResultSet;
import gpf.dc.basic.dto.privilege.RuleDefine;
import gpf.dc.basic.exception.ExpressionException;
import gpf.dc.basic.expression.ExpressionRegisterIntf;
import gpf.dc.basic.expression.RuleIntf;
import gpf.dc.basic.i18n.GpfDCBasicI18n;

public class CExpressionMgr extends BasicServiceCell implements IExpressionMgr{
	public final static String EXCEPTION_NAME = "BussRuleEngineException";
	public final static String LOG = CExpressionMgr.class.getSimpleName();
	public static AviatorEvaluatorInstance aviatorEvaluator;
	public static boolean IsInitialized = false;
	//命名空间，空间内存放指定的规则函数
	Map<String,Env> nameSpaceMap = new ConcurrentHashMap<>();
	Map<String,Map<String,Throwable>> registErrorMap = new ConcurrentHashMap<>();
	@Override
	public Map<String, Env> getNameSpaceMap() throws Exception {
		return nameSpaceMap;
	}
	@Override
	public Map<String, Map<String, Throwable>> getRegistErrorMap() {
		return registErrorMap;
	}
	
	protected IDaoService getDaoService() {
		return Cells.get(IDaoService.class);
	}
	@Override
	public List<String> parseVariableNames(String expression) throws Exception {
//		AviatorEvaluatorInstance instance = AviatorEvaluator.getInstance();
        Expression expr = aviatorEvaluator.compile(expression);
        List<String> variableNames = expr.getVariableNames();
		return variableNames;
	}
	@Override
	public Object execute(Map<String, Object> envMap, String expression) throws Exception {
//		//注册自定义函数
//        if (System.currentTimeMillis()-lastRegisterTime>=600000) {
//            registerFun();
//            lastRegisterTime = System.currentTimeMillis();
//        }
		Set<String> nameSpaces = new LinkedHashSet<>();
		nameSpaces.add("");
		return execute(nameSpaces, envMap, expression);
	}
	
	@Override
	public Object execute(Set<String> nameSpaces, Map<String, Object> envMap, String expression) throws Exception {
		nameSpaces.remove(null);
		if(nameSpaces.isEmpty()) {
			throw new Exception("未指定规则运行的命名空间！传入命名空间：" + nameSpaces);
		}
		int waitCnt = 0;
		while(!IsInitialized) {
			if(waitCnt > 30) {
				throw new Exception("规则库初始化未完成！");
			}
			ToolUtilities.sleep(1000);
			waitCnt++;
		}
		for(String nameSpace : nameSpaces) {
			Env env = nameSpaceMap.get(nameSpace);
			if(env != null) {
				envMap.putAll(env);
			}
		}
		envMap.put("$ruleNamespace$", nameSpaces);
        //运行表达
        Object result = null;
        try {
            result = aviatorEvaluator.execute(expression, envMap);
        } catch (FunctionNotFoundException e) {
        	Set<String> funcNames = new LinkedHashSet<>();
        	for(String nameSpace : nameSpaces) {
    			Env env = nameSpaceMap.get(nameSpace);
    			if(env != null) {
    				funcNames.addAll(env.keySet());
    			}
    		}
            throw new ExpressionException("规则["+expression+"]执行异常，工作空间"+nameSpaces+",函数不存在。当前运行环境的可用的函数："+funcNames+"\n" + e.getMessage()) ;
        } 
        catch (IllegalArgumentException e) {
            throw new ExpressionException("规则["+expression+"]执行异常，工作空间"+nameSpaces+",函数的入参不匹配。\n" + e.getMessage());
        }
        //运行时的错误
        if (envMap.containsKey(EXCEPTION_NAME))
            throw (Exception) envMap.get(EXCEPTION_NAME);
        if(result instanceof AviatorObject) {
        	return ((AviatorObject) result).getValue(envMap);
        }
        //返回数据
        return result;
	}
	
	public RuleDefine convcert2RuleDefine(Form form)throws Exception{
		RuleDefine dto = new RuleDefine();
		dto.setUuid(form.getUuid()).setCode(form.getStringByCode(Form.Code))
		.setCodePath(form.getString(RuleDefine.CodePath)).setScope(form.getString(RuleDefine.Scope))
		.setParamDesc(form.getString(RuleDefine.ParamDesc)).setUseExample(form.getString(RuleDefine.UseExample));
		return dto;
	}
	
	Map<String,Long> clientModelDataTags = new ConcurrentHashMap<>();
	@Override
	public void registerFun(String formModelId) throws Exception {
		boolean isInheritForm = IFormMgr.get().isInheritForm(formModelId, RuleDefine.FormModelId);
		if(!isInheritForm) {
			throw new Exception(GpfDCBasicI18n.getString("{1} is not inherit from {2}!",formModelId,RuleDefine.FormModelId));
		}
		FormModel formModel = IFormMgr.get().queryFormModel(formModelId);
		registerFun(formModel,false);
	}
	
	protected void registerFun(FormModel formModel,boolean ignoreError) throws Exception {
		String formModelId = formModel.getId();
		long clientTimeTag = CmnUtil.getLong(clientModelDataTags.get(formModelId),System.currentTimeMillis());
		long modelDataTag = CmnUtil.getLong(ICacheMgr.get().getModelDataTag(formModelId),-1);
		Tracer tracer = TraceUtil.getCurrentTracer();
		if(clientTimeTag == modelDataTag) {
			tracer.debug(LOG,"==========="+formModel.getNameText()+" 缓存标识一致，无须重新注册============");
			tracer.debug(LOG,"clientTimeTag = " + clientTimeTag + ",modelDataTag = " + modelDataTag);
			return;
		}
		tracer.info(LOG,"==========="+formModel.getNameText()+" 缓存已刷新，重新注册表达式函数============");
		tracer.info(LOG,"clientTimeTag = " + clientTimeTag + ",modelDataTag = " + modelDataTag);
		
		
		try(IDao dao = getDaoService().newDao()){
			List<Form> formList = IFormMgr.get().queryFormPageWithoutNesting(dao, formModelId, null, 1, Integer.MAX_VALUE).getDataList();
			List<AviatorFunction> functions = new ArrayList<>();
			for (Form form : formList) {
				RuleDefine rule = IBasicCacheMgr.get().computeCacheIfAbsent(formModel.getName(), form.getStringByCode(Form.Code), RuleDefine.class, k->{
					try {
						return convcert2RuleDefine(form);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});//convcert2RuleDefine(form);
				if (StringUtils.isEmpty(rule.getCodePath()))     
					continue;
				//执行注册
				try {
					Class<?> clazz = Class.forName(rule.getCodePath());
					Object object = clazz.newInstance();
					if (!(object instanceof AviatorFunction))                               
						throw new Exception("Aviator注册函数失败，该函数没有继承AviatorFunction:" + rule.getCode());
					if(object instanceof RuleIntf) {
						((RuleIntf) object).setName(rule.getCode());
					}
					functions.add((AviatorFunction) object);
//					aviatorEvaluator.addFunction((AviatorFunction) object);
				}catch (Exception e) {
					if(!ignoreError)
						throw e;
					else {
						ToolUtilities.warning(LOG, ToolUtilities.getFullExceptionStack(e));
					}
				}
			}
			if(!functions.isEmpty()) {
				registerFun(functions);
			}
		}
		clientModelDataTags.put(formModelId, modelDataTag);
	}

	@Override
	public void registerFun(List<AviatorFunction> functions) throws Exception {
//		for(AviatorFunction function : functions) {
//			aviatorEvaluator.addFunction(function);
//		}
		registerFun("", functions);
	}
	
	@Override
	public void registerFun(String nameSpace, List<AviatorFunction> functions) throws Exception {
		if(!nameSpaceMap.containsKey(nameSpace)) {
			nameSpaceMap.put(nameSpace, new Env());
		}
		for(AviatorFunction func : functions) {
			nameSpaceMap.get(nameSpace).put(func.getName(), func);
		}
	}
	@Override
	public void addRegistFunError(String nameSpace,String funcName,Throwable exception) {
		if(!registErrorMap.containsKey(nameSpace)) {
			registErrorMap.put(nameSpace, new LinkedHashMap<String, Throwable>());
		}
		registErrorMap.get(nameSpace).put(funcName, exception);
	}
	
	Thread mainThread;
	@Override
	protected void doStartService() throws Exception {
		aviatorEvaluator = AviatorEvaluator.newInstance();
		if(mainThread == null) {
			mainThread = new Thread("Expression Cache Reresh Thread") {
		    	public void run() {
					while (true) {
						List<String> parentIds = new ArrayList<>();
						parentIds.add(RuleDefine.FormModelId);
						try {
							ResultSet<FormModel> rs = IFormMgr.get().queryFormModelPage(parentIds, null, null, 1, Integer.MAX_VALUE);
							for(FormModel model : rs.getDataList()) {
								try {
									registerFun(model,true);
								}catch (InterruptedException e) {
									e.printStackTrace();
									return;
								}catch (Throwable e) {
									e.printStackTrace();
								}
							}
							try {
								Set<Class> classes = ClassUtil.searchSubClass(ExpressionRegisterIntf.class, null);
								for(Class clazz : classes) {
									try {
										ExpressionRegisterIntf registIntf = null;
										if(CellIntf.class.isAssignableFrom(clazz)) {
											registIntf = (ExpressionRegisterIntf) Cells.get(clazz);
										}else {
											registIntf = (ExpressionRegisterIntf) clazz.newInstance();
										}
//										System.out.println("注册表达式：" + registIntf);
										registIntf.registerFun(true);
									}catch (InterruptedException e) {
										e.printStackTrace();
										return;
									}catch (Exception e) {
										e.printStackTrace();
									}
								}
							}catch (Throwable e) {
								e.printStackTrace();
							}
							IsInitialized = true;
						} catch (ClassLoaderConflictException e) {
							throw new RuntimeException(e);
						}catch (InterruptedException e) {
							e.printStackTrace();
							return;
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//1分钟检查一次
						ToolUtilities.sleep(5 * 1000 );
					}
				}
		    };
		    mainThread.start();
		}
	}
	
	public Tracer getTracer() {
		return TraceUtil.getCurrentTracer(LOG);
	}
	@Override
	protected void doStopService() {
		if(aviatorEvaluator != null) {
			aviatorEvaluator.clearExpressionCache();
		}
		if(mainThread != null) {
			Tracer tracer = getTracer();
			tracer.info("服务Cell调用 doStopService，中断监控线程： Expression Cache Reresh Thread");
			mainThread.interrupt();
			mainThread = null;
		}
	}

}
