package octo.cm.enums;

import cmn.reflect.TypeToken;
import fe.cmn.app.Context;
import fe.cmn.editor.SelectEditorQuerier;
import fe.cmn.event.EventDto;
import fe.cmn.panel.PanelContext;
import fe.cmn.widget.ListenerDto;
import fe.util.component.Component;

/**
 * 前端界面运行环境的系统变量
 */
public class FeContextSystemVarKey<T> extends AbstractSystemVarKey<T>{

	/**
	 * 前端上下文系统变量键常量定义
	 */
	public static class Const {
		/** 全局的应用上下文 */
		public final static String $feAppContext$ = "$feAppContext$";
		/** 面板/页面容器的上下文 */
		public final static String $feContext$ = "$feContext$";
		/** 当前触发的监听器配置信息 */
		public final static String $listener$ = "$listener$";
		/** 前端事件传输对象（包含事件源、参数等） */
		public final static String $event$ = "$event$";
		/** 当前正在操作的 UI 组件对象 */
		public final static String $currentComponent$ = "$currentComponent$";
		/** 下拉列表/选择器的查询器对象 */
		public final static String $SelectEditorQuerier$ = "$SelectEditorQuerier$";
	}

	// --- 实例定义：使用 Const 中的常量进行初始化 ---

	/** 全局的应用上下文 */
	public final static FeContextSystemVarKey<Context> $feAppContext$ = new FeContextSystemVarKey<>(Const.$feAppContext$, Context.class);

	/** 面板的上下文 */
	public final static FeContextSystemVarKey<PanelContext> $feContext$ = new FeContextSystemVarKey<>(Const.$feContext$, PanelContext.class);

	/** 监听器对象 */
	public final static FeContextSystemVarKey<ListenerDto> $listener$ = new FeContextSystemVarKey<>(Const.$listener$, ListenerDto.class);

	/** 事件对象 */
	public final static FeContextSystemVarKey<EventDto> $event$ = new FeContextSystemVarKey<>(Const.$event$, EventDto.class);

	/** 当前组件 */
	public final static FeContextSystemVarKey<Component> $currentComponent$ = new FeContextSystemVarKey<>(Const.$currentComponent$, Component.class);

	/** 下拉列表的查询器 */
	public final static FeContextSystemVarKey<SelectEditorQuerier> $SelectEditorQuerier$ = new FeContextSystemVarKey<>(Const.$SelectEditorQuerier$, SelectEditorQuerier.class);


	public FeContextSystemVarKey(String varKey, Class<T> varClass) {
		super(varKey, varClass);
	}

	public FeContextSystemVarKey(String varKey, TypeToken<T> type) {
		super(varKey, type);
	}
}