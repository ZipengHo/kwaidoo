package cell.gpf.dc.basic.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import com.kwaidoo.ms.tool.CmnUtil;
import com.leavay.common.util.TriplePair;
import com.leavay.nio.crpc.RpcMap;

import cell.CellIntf;
import cell.cdao.IDao;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.cfg.SaveTotalFormCallback;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import cmn.enums.NestingTableUpdateMode;
import cmn.enums.TraceLevel;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.adur.data.Form;
import gpf.adur.data.FormField;
import gpf.adur.data.FormModel;
import gpf.adur.data.ResultSet;
import gpf.dc.concrete.RefFormField;
import gpf.dc.config.PDC;

public interface ISaveTotalFormCallback extends CellIntf, SaveTotalFormCallback {

//	public final static String MAPPING_FLAG = "TOTAL_FORM_TO_BUSSINESS_FORM";
	
	public static void main(String[] args) {
        List<Map<String, String>> list1 = new ArrayList<>();
        List<Map<String, String>> list2 = new ArrayList<>();

        Map<String, String> m1 = new HashMap<>();
        m1.put("a", "1");
        m1.put("b", "1");
        list1.add(m1);

        Map<String, String> m2 = new HashMap<>();
        m2.put("b", "2");
        m2.put("a", "1");
        list2.add(m2);

        System.out.println(list1.equals(list2)); // true
    }
	
	@Override
	public default void onSaveTotalForm(IDCRuntimeContext rtx,RpcMap<Object> callbackParams,boolean isCreate,Set<String> updateFields,Set<String> ignoreUpdateFields) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		tracer.info("总表单保存后回调");
		tracer.logStart();
		IDao dao = rtx.getDao();
		PDC pdc = rtx.getPdc();
		Form data = rtx.getTotalForm();
		mappingTotalForm(dao, pdc, data,updateFields,ignoreUpdateFields);
		tracer.infoCost("", "总表单保存后回调耗时");
	}

	public static void mappingTotalForm(IDao dao, PDC pdc, Form totalForm,Set<String> updateFields,Set<String> ignoreUpdateFields) throws Exception {
		List<RefFormField> refFormFields = pdc.getRefFieldList();
		Map<String, Map<String, String>> mappingFieldMap = new LinkedHashMap<>();
		Map<String, FormModel> formModelMap = new LinkedHashMap<>();
		for (RefFormField refFormField : refFormFields) {
			if(updateFields != null && !updateFields.contains(refFormField.getCode())) {
				continue;
			}
			if (!mappingFieldMap.containsKey(refFormField.getFormModelID())) {
				mappingFieldMap.put(refFormField.getFormModelID(), new LinkedHashMap<>());
			}
			FormModel formModel = formModelMap.get(refFormField.getFormModelID());
			if (formModel == null) {
				formModel = IFormMgr.get().queryFormModel(refFormField.getFormModelID());
				formModelMap.put(refFormField.getFormModelID(), formModel);
			}
			Map<String, FormField> fieldMap = formModel.getFieldMap();
			FormField field = fieldMap.get(refFormField.getFormFieldCode());
			if(field == null)
				continue;
			mappingFieldMap.get(refFormField.getFormModelID()).put(refFormField.getCode(),
					field.getCode());
		}
		mappingData(dao, totalForm, mappingFieldMap);
	}

	@MethodDeclare(label = "根据数据映射配置将表单数据映射到指定模型中数据中，并创建表单数据与指定模型数据的关联关系", how = "", what = "", why = "", inputs = {
			@InputDeclare(desc = "", label = "事务对象", name = "dao", exampleValue = "$dao$"),
			@InputDeclare(desc = "", label = "表单数据", name = "data"),
			@InputDeclare(desc = "", label = "映射配置", name = "mappingConfigs") })
	public static void mappingData(IDao dao, Form data, Map<String, Map<String, String>> mappingFieldMap) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
//		tracer.info("回写数据：" + data);
		tracer.info("回写数据：" + mappingFieldMap);
		IFormMgr formMgr = IFormMgr.get();
		FormModel formModel = formMgr.queryFormModel(data.getFormModelId());
		Map<String, TriplePair<Form, Set<String>, Boolean>> mappingTargetDatas = new LinkedHashMap<>();
		mappingValue(dao, mappingFieldMap, formModel.getFieldMap(), data, mappingTargetDatas);
//		tracer.info("回写目标数据模型：" + mappingTargetDatas.keySet());
		for (String key : mappingTargetDatas.keySet()) {
			TriplePair<Form, Set<String>, Boolean> pair = mappingTargetDatas.get(key);
			Form targetData = pair.left;
			if (pair.right) {
				tracer.info("更新目标数据：" + targetData);
				Set<String> updateFieldSets = pair.mid;
				if(!updateFieldSets.isEmpty()) {
					tracer.logStart();
					tracer.info("更新属性列表：" + updateFieldSets);
//					if(tracer.getLevel() == TraceLevel.DEBUG) {
//						for(String updateField : updateFieldSets) {
//							tracer.info("来源属性["+updateField+"]值：" + data.getAttrValueByCode(updateField));
//							tracer.info("目标属性["+updateField+"]值：" + targetData.getAttrValueByCode(updateField));
//						}
//					}
					if(tracer.isDebug()){
						tracer.info("目标数据编号：" + targetData.getStringByCode(Form.Code));
						for(String updateField : updateFieldSets) {
							tracer.info("目标数据属性["+updateField+"]值：" + targetData.getAttrValueByCode(updateField));
						}
					}
					formMgr.updateForm(dao, targetData, NestingTableUpdateMode.IncrementUpdate, updateFieldSets.toArray(new String[0]), null);
					tracer.infoCost("", "更新目标数据耗时");
				}else {
					tracer.info("目标数据无变动，无须更新");	
				}
			} else {
				if (CmnUtil.isStringEmpty(targetData.getStringByCode(Form.Code))) {
					targetData.setAttrValueByCode(Form.Code, data.getStringByCode(Form.Code));
				}
				tracer.info("创建目标数据：" + targetData);
				tracer.logStart();
				targetData.setAttrValueByCode(Form.Owner, data.getUuid());
				dao.putCachedUuidClass(data.getUuid(), data.getFormModelId());
				targetData = formMgr.createForm(dao, targetData);
				tracer.infoCost("", "创建目标数据耗时");
//				CDoLink link = new CDoLink();
//				link.setSrcClass(data.getFormModelId()).setSrcKey(data.getUuid())
//						.setDstClass(targetData.getFormModelId()).setDstKey(targetData.getUuid())
//						.setFlag(MAPPING_FLAG);
//				tracer.info("创建数据关系：" + link);
//				IRelationMgr.get().createLink(dao, link);
//				tracer.infoCost("", "创建数据关系耗时");
			}
		}
	}

	/**
	 * 根据映射配置将数据映射到目标模型
	 * 
	 * @param dao
	 * @param mappingConfigMap
	 * @param fieldMap
	 * @param data
	 * @param mappingTargetDatas
	 * @throws Exception
	 */
	public static void mappingValue(IDao dao, Map<String, Map<String, String>> mappingConfigMap,
			Map<String, FormField> fieldMap, Form data, Map<String, TriplePair<Form, Set<String>, Boolean>> mappingTargetDatas)
			throws Exception {
		IFormMgr formMgr = IFormMgr.get();
		for (String dstModelId : mappingConfigMap.keySet()) {
			Form copyData = formMgr.copyForm(data);
			copyData.setAttrValueByCode(Form.Code, data.getAttrValueByCode(Form.Code));
			copyData.setUuid(data.getUuid());
			Map<String, String> mappingFields = mappingConfigMap.get(dstModelId);
			for (String srcFieldCode : mappingFields.keySet()) {
				FormField srcField =fieldMap.get(srcFieldCode);
				if(srcField == null)
					continue;
				String targetFieldCode = mappingFields.get(srcFieldCode);
				Object value = copyData.getAttrValueByCode(srcField.getCode());
				TriplePair<Form, Set<String>, Boolean> targetPair = queryOrCreateTargetData(dao, copyData, dstModelId, mappingTargetDatas);
				Form targetData = targetPair.left;
				targetPair.mid.add(targetFieldCode);
				targetData.setAttrValueByCode(targetFieldCode, value);
			}
		}
	}

	/**
	 * 查询或创建映射的目标模型数据
	 * 
	 * @param dao
	 * @param srcData
	 * @param dstModelId
	 * @param mappingTargetDatas
	 * @return
	 * @throws Exception
	 */
	public static TriplePair<Form, Set<String>, Boolean> queryOrCreateTargetData(IDao dao, Form srcData, String dstModelId,
			Map<String, TriplePair<Form, Set<String>, Boolean>> mappingTargetDatas) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		String mappingTargetDataKey = srcData.getFormModelId() + "[" + srcData.getUuid() + "]->" + dstModelId;
		if (mappingTargetDatas.containsKey(mappingTargetDataKey)) {
			return mappingTargetDatas.get(mappingTargetDataKey);
		}
		boolean isCreated = false;
		IFormMgr formMgr = IFormMgr.get();
//		Cnd cnd = Cnd.NEW();
//		cnd.and(new SqlExpressionGroup().andEquals(CDoLink.SrcClass, srcData.getFormModelId())
//				.andEquals(CDoLink.SrcKey, srcData.getUuid()).andEquals(CDoLink.DstClass, dstModelId)
//				.andEquals(CDoLink.Flag, MAPPING_FLAG))
//			;
//		tracer.info(cnd);
//		IRelationMgr relationMgr = IRelationMgr.get();
//		List<CDoLink> links = relationMgr.queryLinks(dao, cnd);
		Cnd cnd = Cnd.where(new SqlExpressionGroup().andEquals(Form.Owner, srcData.getUuid()));
		ResultSet<Form> rs = formMgr.queryFormPage(dao, dstModelId, cnd, 1, 1, false, true);
		Form targetForm = null;
		if (rs.isEmpty()) {
			targetForm = new Form(dstModelId);
			targetForm.setAttrValueByCode(Form.Code, srcData.getStringByCode(Form.Code));
			targetForm.setAttrValueByCode(Form.Owner, srcData.getUuid());
			tracer.info("新增映射表单数据：" + dstModelId);
		} else {
//			CDoLink link = links.get(0);
//			String dstUuid = link.getDstKey();
//			targetForm = formMgr.queryForm(dao, dstModelId, dstUuid);
//			if (targetForm == null) {
//				targetForm = new Form(dstModelId);
//				targetForm.setUuid(dstUuid);
//				targetForm.setAttrValueByCode(Form.Code, srcData.getStringByCode(Form.Code));
//				tracer.info("新增映射表单数据：" + dstModelId + ",uuid = " + dstUuid);
//			} else {
			targetForm = rs.getDataList().get(0);
				isCreated = true;
//			}
		}
		mappingTargetDatas.put(mappingTargetDataKey, new TriplePair<>(targetForm, new LinkedHashSet<>(),isCreated));
		return mappingTargetDatas.get(mappingTargetDataKey);
	}

}
