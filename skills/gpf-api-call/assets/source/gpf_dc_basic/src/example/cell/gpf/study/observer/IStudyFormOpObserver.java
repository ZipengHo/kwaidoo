package cell.gpf.study.observer;

import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;

import com.cdao.impl.entity.field.GID;

import bap.cells.Cells;
import cell.CellIntf;
import cell.cdao.IDao;
import cmn.anotation.ClassDeclare;
import cmn.dto.Progress;
import cmn.dto.model.extend.intf.ObserverContext;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.adur.action.Action;
import gpf.adur.data.Form;
import gpf.adur.role.Org;
import gpf.adur.role.Role;
import gpf.adur.user.User;
import gpf.dc.config.PDC;
import gpf.dc.intf.FormOpObserver;
import gpf.dc.runtime.PDCForm;
@ClassDeclare(label = "GPF数据操作监听样例"
,what="GPF数据操作监听样例"
, why = ""
, how = "实现相应数据操作监听接口添加自定义的处理逻辑"
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-24"
,updateTime = "2025-01-24")
public interface IStudyFormOpObserver extends CellIntf,FormOpObserver{

	static IStudyFormOpObserver get() {
		return Cells.get(IStudyFormOpObserver.class);
	}
	
	@Override
	default void onBeforeCreate(Progress prog, ObserverContext context) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		//获取上下文中的dao对象
		IDao dao = context.getDao();
		Object data = context.getBeforeOpData();
		if(data instanceof PDC) {
			tracer.info("创建PDC前："+((PDC) data).getCode());
		}else if(data instanceof Action) {
			tracer.info("创建Action前："+((Action) data).getCode());
		}else if(data instanceof User) {
			tracer.info("创建User前："+((User) data).getCode());
		}else if(data instanceof Org) {
			tracer.info("创建Org前："+((Org) data).getCode());
		}else if(data instanceof Role) {
			tracer.info("创建Role前："+((Role) data).getCode());
		}else if(data instanceof Form) {
			tracer.info("创建Form前："+((Form) data).getUuid());
		}
	}

	@Override
	default void onAfterCreate(Progress prog, ObserverContext context) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		//获取上下文中的dao对象
		IDao dao = context.getDao();
		Object data = context.getAfterOpData();
		System.out.println("======================data:"+data);
		if(data instanceof PDC) {
			tracer.info("创建PDC后："+((PDC) data).getCode());
		}else if(data instanceof Action) {
			tracer.info("创建Action后："+((Action) data).getCode());
		}else if(data instanceof User) {
			tracer.info("创建User后："+((User) data).getCode());
		}else if(data instanceof Org) {
			tracer.info("创建Org后："+((Org) data).getCode());
		}else if(data instanceof Role) {
			tracer.info("创建Role后："+((Role) data).getCode());
		}else if(data instanceof PDCForm) {
			PDCForm form = (PDCForm) data;
			tracer.info("创建PDCForm后："+((Form) data).getUuid());
			System.out.println("创建PDCForm后："+((Form) data).getUuid());
			dao.queryDo(new GID(form.getFormModelId(),form.getUuid()));
		}else if(data instanceof Form) {
			tracer.info("创建Form后："+((Form) data).getUuid());
		}
	}

	@Override
	default void onBeforeBatchCreate(Progress prog, ObserverContext context) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		//获取上下文中的dao对象
		IDao dao = context.getDao();
		List list = (List) context.getBeforeOpData();
		for(int i =0;i<list.size();i++) {
			Object data = list.get(i);
			if(data instanceof PDC) {
				tracer.info("批量创建PDC前："+((PDC) data).getCode());
			}else if(data instanceof Action) {
				tracer.info("批量创建Action前："+((Action) data).getCode());
			}else if(data instanceof User) {
				tracer.info("批量创建User前："+((User) data).getCode());
			}else if(data instanceof Org) {
				tracer.info("批量创建Org前："+((Org) data).getCode());
			}else if(data instanceof Role) {
				tracer.info("批量创建Role前："+((Role) data).getCode());
			}else if(data instanceof Form) {
				tracer.info("批量创建Form前："+((Form) data).getUuid());
			}
		}
	}

	@Override
	default void onAfterBatchCreate(Progress prog, ObserverContext context) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		//获取上下文中的dao对象
		IDao dao = context.getDao();
		List list = (List) context.getAfterOpData();
		for(int i =0;i<list.size();i++) {
			Object data = list.get(i);
			if(data instanceof PDC) {
				tracer.info("批量创建PDC后："+((PDC) data).getCode());
			}else if(data instanceof Action) {
				tracer.info("批量创建Action后："+((Action) data).getCode());
			}else if(data instanceof User) {
				tracer.info("批量创建User后："+((User) data).getCode());
			}else if(data instanceof Org) {
				tracer.info("批量创建Org后："+((Org) data).getCode());
			}else if(data instanceof Role) {
				tracer.info("批量创建Role后："+((Role) data).getCode());
			}else if(data instanceof Form) {
				tracer.info("批量创建Form后："+((Form) data).getUuid());
			}
		}
	}

	@Override
	default void onBeforeUpdate(Progress prog, ObserverContext context) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		//获取上下文中的dao对象
		IDao dao = context.getDao();
		Object data = context.getBeforeOpData();
		if(data instanceof PDC) {
			tracer.info("更新PDC前："+((PDC) data).getCode());
		}else if(data instanceof Action) {
			tracer.info("更新Action前："+((Action) data).getCode());
		}else if(data instanceof User) {
			tracer.info("更新User前："+((User) data).getCode());
		}else if(data instanceof Org) {
			tracer.info("更新Org前："+((Org) data).getCode());
		}else if(data instanceof Role) {
			tracer.info("更新Role前："+((Role) data).getCode());
		}else if(data instanceof Form) {
			tracer.info("更新Form前："+((Form) data).getUuid());
		}
	}

	@Override
	default void onAfterUpdate(Progress prog, ObserverContext context) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		//获取上下文中的dao对象
		IDao dao = context.getDao();
		Object data = context.getAfterOpData();
		if(data instanceof PDC) {
			tracer.info("更新PDC后："+((PDC) data).getCode());
		}else if(data instanceof Action) {
			tracer.info("更新Action后："+((Action) data).getCode());
		}else if(data instanceof User) {
			tracer.info("更新User后："+((User) data).getCode());
		}else if(data instanceof Org) {
			tracer.info("更新Org后："+((Org) data).getCode());
		}else if(data instanceof Role) {
			tracer.info("更新Role后："+((Role) data).getCode());
		}else if(data instanceof Form) {
			tracer.info("更新Form后："+((Form) data).getUuid());
		}
	}

	@Override
	default void onBeforeBatchUpdate(Progress prog, ObserverContext context) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		//获取上下文中的dao对象
		IDao dao = context.getDao();
		Map<String,Object> param = context.getContext();
		String formModelId = (String) param.get(FormOpObserver.ContextKey_FormModelId);
		Cnd cnd = (Cnd) param.get(FormOpObserver.ContextKey_Cnd);
		Map<String,Object> mapValue = (Map<String, Object>) param.get(FormOpObserver.ContextKey_MapValue);
		tracer.info("批量更新数据：" + formModelId);
		tracer.info("更新条件：" + cnd);
		tracer.info("更新值：" + mapValue);
	}

	@Override
	default void onAfterBatchUpdate(Progress prog, ObserverContext context) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		//获取上下文中的dao对象
		IDao dao = context.getDao();
		Map<String,Object> param = context.getContext();
		String formModelId = (String) param.get(FormOpObserver.ContextKey_FormModelId);
		Cnd cnd = (Cnd) param.get(FormOpObserver.ContextKey_Cnd);
		Map<String,Object> mapValue = (Map<String, Object>) param.get(FormOpObserver.ContextKey_MapValue);
		tracer.info("批量更新数据：" + formModelId);
		tracer.info("更新条件：" + cnd);
		tracer.info("更新值：" + mapValue);
	}

	@Override
	default void onBeforeDelete(Progress prog, ObserverContext context) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		//获取上下文中的dao对象
		IDao dao = context.getDao();
		Object data = context.getBeforeOpData();
		if(data instanceof PDC) {
			tracer.info("删除PDC前："+((PDC) data).getCode());
		}else if(data instanceof Action) {
			tracer.info("删除Action前："+((Action) data).getCode());
		}else if(data instanceof User) {
			tracer.info("删除User前："+((User) data).getCode());
		}else if(data instanceof Org) {
			tracer.info("删除Org前："+((Org) data).getCode());
		}else if(data instanceof Role) {
			tracer.info("删除Role前："+((Role) data).getCode());
		}else if(data instanceof Form) {
			tracer.info("删除Form前："+((Form) data).getUuid());
		}
	}

	@Override
	default void onAfterDelete(Progress prog, ObserverContext context) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		//获取上下文中的dao对象
		IDao dao = context.getDao();
		//需要拿删除前的对象，删除后的为null
		Object data = context.getBeforeOpData();
		if(data instanceof PDC) {
			tracer.info("删除PDC后："+((PDC) data).getCode());
		}else if(data instanceof Action) {
			tracer.info("删除Action后："+((Action) data).getCode());
		}else if(data instanceof User) {
			tracer.info("删除User后："+((User) data).getCode());
		}else if(data instanceof Org) {
			tracer.info("删除Org后："+((Org) data).getCode());
		}else if(data instanceof Role) {
			tracer.info("删除Role后："+((Role) data).getCode());
		}else if(data instanceof Form) {
			tracer.info("删除Form后："+((Form) data).getUuid());
		}
	}

	@Override
	default void onAfterImport(Progress prog, ObserverContext<List<Form>> context) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		//获取上下文中的dao对象
		IDao dao = context.getDao();
		List list = (List) context.getAfterOpData();
		for(Object data : list) {
			if(data instanceof PDC) {
				tracer.info("导入PDC后："+((PDC) data).getCode());
			}else if(data instanceof Action) {
				tracer.info("导入Action后："+((Action) data).getCode());
			}else if(data instanceof User) {
				tracer.info("导入User后："+((User) data).getCode());
			}else if(data instanceof Org) {
				tracer.info("导入Org后："+((Org) data).getCode());
			}else if(data instanceof Role) {
				tracer.info("导入Role后："+((Role) data).getCode());
			}else if(data instanceof Form) {
				tracer.info("导入Form后："+((Form) data).getUuid());
			}
		}
	}

	@Override
	default void onBeforeBatchDelete(Progress prog, ObserverContext context) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		//获取上下文中的dao对象
		IDao dao = context.getDao();
		Map<String,Object> param = context.getContext();
		String formModelId = (String) param.get(FormOpObserver.ContextKey_FormModelId);
		Cnd cnd = (Cnd) param.get(FormOpObserver.ContextKey_Cnd);
		tracer.info("批量删除数据：" + formModelId);
		tracer.info("删除条件：" + cnd);
	}

	@Override
	default void onAfterBatchDelete(Progress prog, ObserverContext context) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		//获取上下文中的dao对象
		IDao dao = context.getDao();
		Map<String,Object> param = context.getContext();
		String formModelId = (String) param.get(FormOpObserver.ContextKey_FormModelId);
		Cnd cnd = (Cnd) param.get(FormOpObserver.ContextKey_Cnd);
		tracer.info("批量删除数据：" + formModelId);
		tracer.info("删除条件：" + cnd);
	}
}
