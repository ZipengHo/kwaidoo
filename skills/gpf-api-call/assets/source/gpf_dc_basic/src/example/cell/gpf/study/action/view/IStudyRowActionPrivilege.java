package cell.gpf.study.action.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cell.CellIntf;
import cmn.anotation.ClassDeclare;
import gpf.adur.data.Form;
import gpf.dc.basic.action.intf.FeRowActionPrivilegeIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;
import gpf.dc.runtime.PDFForm;
import gpf.dto.model.data.ActionPrivilegeDto;
@ClassDeclare(label = "表格、树行操作权限代码样例"
,what="表格、树行操作权限代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyRowActionPrivilege <T extends BaseFeActionParameter> extends CellIntf, FeRowActionPrivilegeIntf<T>{

	@Override
	default Map<String, List<ActionPrivilegeDto>> caculateRowActionPrivilege(T input) throws Exception {
		Map<String, List<ActionPrivilegeDto>> map = new LinkedHashMap<>();
		List<Form> rows = getRows(input);
		for(Form form : rows) {
			String rowId;
			if(form instanceof PDFForm) {
				rowId = ((PDFForm) form).getOpLogUuid();
			}else {
				rowId = form.getUuid();
			}
			List<ActionPrivilegeDto> actionPrivs = new ArrayList<>();
			//动作名称为视图上定义的按钮名称
			actionPrivs.add(new ActionPrivilegeDto().setName("删除").setOperatable(true).setVisible(true));
			//每行记录需要单独设置动作权限
			map.put(rowId, actionPrivs);
		}
		return map;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseFeActionParameter.class;
	}
}
