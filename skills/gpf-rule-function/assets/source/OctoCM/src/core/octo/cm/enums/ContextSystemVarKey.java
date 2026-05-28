package octo.cm.enums;

import cell.cdao.IDao;
import cell.octo.cm.IContext;
import cmn.dto.Progress;
import cmn.reflect.TypeToken;
import gpf.adur.data.Form;
import gpf.adur.user.User;
import gpf.dc.basic.param.view.dto.ApplicationSetting;
import gpf.dc.http.AppUserInfo;
import octo.cm.dto.ContextModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cell.octo.cm.adapter.IOperationExecuteCallback;

public class ContextSystemVarKey<T> extends AbstractSystemVarKey<T>{

	/**
	 * 上下文系统变量键常量定义
	 */
	public static class Const{
		/** 上下文对象 */
		public final static String $context$ = "$context$";
		/** 数据库访问对象 (事务对象) */
		public final static String $dao$ = "$dao$";
		/** 进度通知/异步处理进度 */
		public final static String $progress$ = "$progress$";
		/** 当前操作人用户信息 */
		public final static String $operator$ = "$operator$";
		/** 应用全局配置信息 */
		public static final String $applicationSetting$ = "$applicationSetting$";
		/** 用户模型唯一标识 ID */
		public final static String $userModelId$ = "$userModelId$";
		/** 组织模型唯一标识 ID */
		public final static String $orgModelId$ = "$orgModelId$";
		/** 上下文模型 (ContextModel) */
		public final static String $cm$ = "$cm$";
		/** 语义对象实例 (单条表单数据) */
		public final static String $cmInstance$ = "$cmInstance$";
		/** 语义对象实例列表 (多条表单数据) */
		public final static String $cmInstanceList$ = "$cmInstanceList$";
		/** 驱动/执行器名称 */
		public final static String $driverName$ = "$driverName$";
		/** 当前执行的方法名称 */
		public final static String $methodName$ = "$methodName$";
		/** 方法执行时的参数映射关系 */
		public final static String $parameterMappings$ = "$parameterMappings$";
		/** 当前上下文环境下的表单模型ID */
		public final static String $formModelId$ = "$formModelId$";
		/** 当前业务表单数据 */
		public final static String $form$ = "$form$";
		/** 逻辑运行后的输出结果 */
		public final static String $output$ = "$output$";
		/** 规则引擎执行的命名空间 */
		public final static String $ruleNamespace$ = "$ruleNamespace$";
		/** 逻辑/规则运行的环境变量 */
		public final static String $env$ = "$env$";
		/** 当前登录会话信息 */
		public final static String $sessionInfo$ = "$sessionInfo$";
		/** 系统运行中捕获的异常信息 */
		public final static String $exception$ = "$exception$";
		/** 标记位：运行过程中表单是否已被执行保存操作 */
		public final static String $formSaved$ = "$formSaved$";
//		/** 操作执行前置回调句柄列表 */
//		public final static String $beforeOperationCallbacks$ = "$beforeOperationCallbacks$";
		/** 操作执行后置回调句柄列表（finally 中执行） */
		public final static String $afterOperationCallbacks$ = "$afterOperationCallbacks$";
	}

	// --- 实例定义：使用 Const 中的常量进行初始化 ---

	//上下文
	public final static ContextSystemVarKey<IContext> $context$ = new ContextSystemVarKey<>(Const.$context$, IContext.class);
	//事务对象
	public final static ContextSystemVarKey<IDao> $dao$ = new ContextSystemVarKey<>(Const.$dao$, IDao.class);
	//进度通知对象
	public final static ContextSystemVarKey<Progress> $progress$ = new ContextSystemVarKey<>(Const.$progress$, Progress.class);
	//操作人
	public final static ContextSystemVarKey<User> $operator$ = new ContextSystemVarKey<>(Const.$operator$, User.class);
	//应用配置
	public static final ContextSystemVarKey<ApplicationSetting> $applicationSetting$ = new ContextSystemVarKey<>(Const.$applicationSetting$, ApplicationSetting.class);
	//用户模型ID
	public final static ContextSystemVarKey<String> $userModelId$ = new ContextSystemVarKey<>(Const.$userModelId$, String.class);
	//组织模型ID
	public final static ContextSystemVarKey<String> $orgModelId$ = new ContextSystemVarKey<>(Const.$orgModelId$, String.class);
	//上下文模型
	public final static ContextSystemVarKey<ContextModel> $cm$ = new ContextSystemVarKey<>(Const.$cm$, ContextModel.class);
	//语义对象
	public final static ContextSystemVarKey<Form> $cmInstance$ = new ContextSystemVarKey<>(Const.$cmInstance$, Form.class);
	//语义对象列表
	public final static ContextSystemVarKey<List<Form>> $cmInstanceList$ = new ContextSystemVarKey<>(Const.$cmInstanceList$, new TypeToken<List<Form>>() {});
	// 驱动名称
	public final static ContextSystemVarKey<String> $driverName$ = new ContextSystemVarKey<>(Const.$driverName$, String.class);
	// 方法名称
	public final static ContextSystemVarKey<String> $methodName$ = new ContextSystemVarKey<>(Const.$methodName$, String.class);
	// 方法参数映射
	public final static ContextSystemVarKey<Map<String, String>> $parameterMappings$ = new ContextSystemVarKey<>(Const.$parameterMappings$, new TypeToken<Map<String, String>>(){});
	//表单数据
	public final static ContextSystemVarKey<Form> $form$ = new ContextSystemVarKey<>(Const.$form$, Form.class);
	//运行输出
	public final static ContextSystemVarKey<Object> $output$ = new ContextSystemVarKey<>(Const.$output$, Object.class);
	//规则命名空间
	public final static ContextSystemVarKey<Set<String>> $ruleNamespace$ = new ContextSystemVarKey<>(Const.$ruleNamespace$, new TypeToken<Set<String>>(){});
	//规则运行环境
	public final static ContextSystemVarKey<Map<String,Object>> $env$ = new ContextSystemVarKey<>(Const.$env$, new TypeToken<Map<String,Object>>(){});
	//会话信息
	public final static ContextSystemVarKey<AppUserInfo> $sessionInfo$ = new ContextSystemVarKey<>(Const.$sessionInfo$, new TypeToken<AppUserInfo>(){});
	//异常信息
	public final static ContextSystemVarKey<Throwable> $exception$ = new ContextSystemVarKey<>(Const.$exception$, new TypeToken<Throwable>(){});
	//运行过程中是否保存了表单
	public final static ContextSystemVarKey<Boolean> $formSaved$ = new ContextSystemVarKey<>(Const.$formSaved$, Boolean.class);
//	//操作执行前置回调列表
//	public final static ContextSystemVarKey<List<IOperationExecuteCallback>> $beforeOperationCallbacks$ = new ContextSystemVarKey<>(Const.$beforeOperationCallbacks$, new TypeToken<List<IOperationExecuteCallback>>(){});
	//操作执行后置回调列表（finally 语义）
	public final static ContextSystemVarKey<List<IOperationExecuteCallback>> $afterOperationCallbacks$ = new ContextSystemVarKey<>(Const.$afterOperationCallbacks$, new TypeToken<List<IOperationExecuteCallback>>(){});

	public ContextSystemVarKey(String varKey, Class<T> varClass) {
		super(varKey, varClass);
	}

	public ContextSystemVarKey(String varKey, TypeToken<T> type) {
		super(varKey, type);
	}

	public static void main(String[] args) {
		System.out.println(ContextSystemVarKey.$dao$.getVarKey());
	}

}