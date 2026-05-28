package gpf.dc.basic.intf;

import java.io.Serializable;
import java.util.Set;

import org.nutz.dao.Cnd;

import cell.cdao.IDao;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.dc.backup.IBackupService;
import cmn.dto.Progress;
import gpf.adur.data.Form;
import gpf.adur.data.ResultSet;
import gpf.dc.intf.FormOpObserver;
import web.dto.Pair;
/**
 * 表单数据操作接口，实现此接口干预表格上对表单数据的操作
 * @author chenxb
 *
 */
public interface FormActionIntf extends Serializable{
	
	default Form newForm(String formModelId)throws Exception{
		Form form = new Form();
		form.setFormModelId(formModelId);
		return form;
	}

	default Form create(IDao dao,Form form,FormOpObserver observer)throws Exception{
		return IFormMgr.get().createForm(null, dao, form, observer);
	}
	
	default Form update(IDao dao,Form form,FormOpObserver observer)throws Exception{
		return IFormMgr.get().updateForm(null, dao, form, observer);
	}
	
	default Form query(IDao dao,String formModelId,String uuid)throws Exception{
		return IFormMgr.get().queryForm(dao, formModelId, uuid);
	}
	
	default ResultSet<Form> queryPage(IDao dao,String formModelId,String querySql,Set<String> extFields,Cnd cnd,int pageNo,int pageSize)throws Exception{
		return IFormMgr.get().queryFormPageBySql(dao, formModelId, querySql, extFields, cnd, pageNo, pageSize);
	}
	
	default void delete(IDao dao,String formModelId,String uuid,FormOpObserver observer)throws Exception{
		IFormMgr.get().deleteForm(null,dao,formModelId, uuid,observer);
	}
	
	default Pair<String, byte[]> exportData(Progress prog,String formModelId,Cnd cnd, FormOpObserver observer)throws Exception{
		return IBackupService.get().exportFormToExcel(prog, null, formModelId, cnd);
	}
	
	default void importData(Progress prog,String formModelId,Pair<String,byte[]> file,FormOpObserver observer)throws Exception{
		IBackupService.get().importFormFormExcel(prog, null, formModelId, file,observer);
	}
}
