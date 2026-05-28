package octo.cm.enums;

import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.reflect.TypeToken;
import gpf.dc.basic.param.view.BaseFeActionParameter;

public class GpfContextSystemVarKey<T> extends AbstractSystemVarKey<T>{

	/**
	 * GPF 上下文系统变量键常量定义
	 */
	public static class Const {
		/** DC 运行时上下文对象 */
		public final static String $IDCRuntimeContext$ = "$IDCRuntimeContext$";
		/** 前端动作参数基类对象 */
		public final static String $ActionParameter$ = "$ActionParameter$";
		/** 操作员编码/工号 */
		public final static String $operatorCode$ = "$operatorCode$";
	}

	// --- 实例定义：使用 Const 中的常量进行初始化 ---

	/** DC运行时上下文 */
	public final static GpfContextSystemVarKey<IDCRuntimeContext> $IDCRuntimeContext$ = new GpfContextSystemVarKey<>(Const.$IDCRuntimeContext$, IDCRuntimeContext.class);

	/** 动作参数 */
	public final static GpfContextSystemVarKey<BaseFeActionParameter> $ActionParameter$ = new GpfContextSystemVarKey<>(Const.$ActionParameter$, BaseFeActionParameter.class);

	/** 操作员编码 */
	public final static GpfContextSystemVarKey<String> $operatorCode$ = new GpfContextSystemVarKey<>(Const.$operatorCode$, String.class);

	public GpfContextSystemVarKey(String varKey, Class<T> varClass) {
		super(varKey, varClass);
	}

	public GpfContextSystemVarKey(String varKey, TypeToken<T> type) {
		super(varKey, type);
	}
}