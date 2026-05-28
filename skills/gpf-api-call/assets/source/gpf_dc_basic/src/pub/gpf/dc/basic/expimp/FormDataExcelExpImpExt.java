package gpf.dc.basic.expimp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;

import com.kwaidoo.ms.tool.CmnUtil;

import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.data.IFormMgr;
import gpf.adur.data.AssociationData;
import gpf.adur.data.Form;
import gpf.adur.data.FormField;
import gpf.adur.data.FormModel;
import gpf.adur.data.ResultSet;
import gpf.dc.expimp.ExpImpContext;
import gpf.dc.expimp.FormDataExcelExpImp;

public class FormDataExcelExpImpExt extends FormDataExcelExpImp{

	Map<String,String> assocDataExpSettingMap = new LinkedHashMap<>();
	Map<String,Map<String,Form>> cacheAssocCodeData = new LinkedHashMap<>();
	
	Map<String,Map<String,Form>> cacheAssocTextData = new LinkedHashMap<>();
	
	public Map<String, String> getAssocDataExpSettingMap() {
		return assocDataExpSettingMap;
	}
	public void setAssocDataExpSettingMap(Map<String, String> assocDataExpSettingMap) {
		this.assocDataExpSettingMap = assocDataExpSettingMap;
	}
	
	public String getAssocFormText(AssociationData value) throws Exception {
		if(!cacheAssocCodeData.containsKey(value.getFormModelId())) {
			cacheAssocCodeData.put(value.getFormModelId(), new LinkedHashMap<>());
		}
		Form form = cacheAssocCodeData.get(value.getFormModelId()).get(value.getValue());
		if(form == null) {
			form = value.getForm();
			cacheAssocCodeData.get(value.getFormModelId()).put(value.getValue(), form);
		}
		String exportField = assocDataExpSettingMap.get(value.getFormModelId());
		FormModel model = IFormMgr.get().queryFormModel(value.getFormModelId());
		FormField exportFormField = model.getFieldByName(exportField);
		String label = "";
		if(exportFormField != null) {
			label = CmnUtil.getString(form.getAttrValueByCode(exportFormField.getCode()),"");
		}
		return CmnUtil.isStringEmpty(label) ? value.getValue() : label;
	}
	
	public String getAssocFormCode(String formModelId,String text) throws Exception {
		if(!cacheAssocTextData.containsKey(formModelId)) {
			cacheAssocTextData.put(formModelId, new LinkedHashMap<>());
		}
		Form form = cacheAssocTextData.get(formModelId).get(text);
		if(form == null) {
			String exportField = assocDataExpSettingMap.get(formModelId);
			FormModel model = IFormMgr.get().queryFormModel(formModelId);
			FormField exportFormField = model.getFieldByName(exportField);
			if(exportFormField == null) {
				return text;
			}
			try(IDao dao = IDaoService.get().newDao()){
				Cnd cnd = Cnd.NEW();
				ResultSet<Form> rs = IFormMgr.get().queryFormPageWithoutNesting(dao, formModelId, cnd, 1, 1);
				if(rs.isEmpty())
					return "";
				form = rs.getDataList().get(0);
				cacheAssocTextData.get(formModelId).put(text, form);
			}
		}
		if(form == null)
			return "";
		return form.getStringByCode(Form.Code);
	}
	
	@Override
	public String writeAssocaDataToCell(ExpImpContext context,AssociationData value) throws Exception {
		if(assocDataExpSettingMap.containsKey(value.getFormModelId())) {
			return getAssocFormText(value);
		}else
			return super.writeAssocaDataToCell(context,value);
	}
	
	@Override
	public String writeAssocaDataToCell(ExpImpContext context,List<AssociationData> values) throws Exception {
		List<String> codes = new ArrayList<>();
		if (!CmnUtil.isCollectionEmpty(values)) {
			for (AssociationData value : values) {
				if(assocDataExpSettingMap.containsKey(value.getFormModelId())) {
					codes.add(getAssocFormText(value));
				}else
					codes.add(value.getValue());
			}
		}
		return String.join(",", codes);
	}
	
	@Override
	public AssociationData readAssocaDataFromCell(ExpImpContext context,String assocModelId, String value) throws Exception {
		if(CmnUtil.isStringEmpty(value))
			return null;
		if(assocDataExpSettingMap.containsKey(assocModelId)) {
			String code = getAssocFormCode(assocModelId, value);
			if(CmnUtil.isStringEmpty(code)) {
				return null;
			}
			return new AssociationData(assocModelId, code);
		}else
			return new AssociationData(assocModelId, value);
	}
	
	@Override
	public List<AssociationData> readAssocaDatasFromCell(ExpImpContext context,String assocModelId, String value) throws Exception {
		if(CmnUtil.isStringEmpty(value))
			return null;
		String[] codes = value.split(",");
		List<AssociationData> datas = new ArrayList<>();
		for(String code: codes) {
			AssociationData assocData = readAssocaDataFromCell(context,assocModelId, code);
			if(assocData != null)
				datas.add(assocData);
		}
		return datas;
	}
}
