package cell.gpf.study.nadur.action;

import org.nutz.dao.Cnd;

import com.kwaidoo.ms.tool.CmnUtil;

import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.action.IActionMgr;
import gpf.adur.action.Action;
import gpf.adur.action.ActionModel;
import gpf.adur.data.DataType;
import gpf.adur.data.FormField;
import gpf.adur.data.ResultSet;

public class ScanActionData {

	public static void scan(String actionModelId) throws Exception {
		IActionMgr actionMgr = IActionMgr.get();
		try (IDao dao = IDaoService.newIDao()) {
			Cnd cnd = null;
			int pageNo = 1;
			int pageSize = Integer.MAX_VALUE;
			ResultSet<Action> rs = actionMgr.queryActionPage(dao, actionModelId, cnd, pageNo, pageSize, true, true);
			ActionModel actionModel = actionMgr.queryActionModel(actionModelId);
			for (Action action : rs.getDataList()) {
				for (FormField field : actionModel.getFieldList()) {
					DataType dataType = field.getDataTypeEnum();
					if (dataType == DataType.Depend) {
						String value = action.getStringByCode(field.getCode());
						if (!CmnUtil.isStringEmpty(value)) {

						}
					} else if (dataType == DataType.NestingModel) {
					}
				}
			}
		}
	}
}
