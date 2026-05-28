package gpf.dc.basic.dto.util;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.dfc.gui.LvUtil;

import cell.gpf.adur.data.IFormMgr;
import gpf.adur.data.DataType;
import gpf.adur.data.Form;
import gpf.adur.data.FormField;
import gpf.adur.data.FormModel;

public class DtoConvertCodeGenerator implements Serializable{

	public static String upperCase(String str) {
		return str.substring(0, 1).toUpperCase()+str.substring(1);
	}
	
	public static String convert2Form(Class dtoClass) throws Exception {
		String formModelId = (String) ToolUtilities.getStaticFieldValue(dtoClass, "FormModelId");
		FormModel formModel = IFormMgr.get().queryFormModel(formModelId);
		Map<String,String> regexMap = new LinkedHashMap<>();
		regexMap.put("#dtoClass#", dtoClass.getSimpleName());
		List<String> hiddenField = formModel.getHiddenFields();
		StringBuffer javaCode = new StringBuffer();
		javaCode.append("public Form convert#dtoClass#2Form(#dtoClass# dto) throws Exception {\r\n" + 
				"		Form form = new Form(#dtoClass#.FormModelId);\r\n" + 
				"		form.setUuid(dto.getUuid()).setAttrValueByCode(Form.Code, dto.getCode());\r\n");
		String setCodeTmplt = "form.setAttrValue(#dtoClass#.s#upperFieldCode#, dto.get#upperFieldCode#());\r\n";
//		String setAssocDataTmplt = ".setAttrValue(#dtoClass#.#upperFieldCode#, dto.get#upperFieldCode#())\r\n";
		String setTableDataCodeTmplt = "form.setAttrValue(#dtoClass#.s#upper#, convertToTableData(workspace.getVariables(), v->convertWorkSpaceVariable2Form(v)));\\r\\n";
		String setAttachDataCodeTmplt = "";
		
		for(FormField field : formModel.getFieldList()) {
			if(hiddenField.contains(field.getCode())){
				continue;
			}
			if(field.getCode().equals(Form.Code))
				continue;
			String fieldSetterCode = "";
			Map<String,String> fieldRegexMap = new LinkedHashMap<>();
			String upperFieldCode = upperCase(field.getCode());
			fieldRegexMap.put("#upperFieldCode#", upperFieldCode);
			fieldRegexMap.put("#fieldCode#", field.getCode());
			DataType dataType = field.getDataTypeEnum();
			switch (dataType) {
			case Text:
			case Depend:
			case Password:
				fieldSetterCode = setCodeTmplt;
				break;
			case Attach:
				fieldSetterCode = setAttachDataCodeTmplt;
				break;
			case Binary:
			case Image:
			case RichDocument:
				fieldSetterCode = setCodeTmplt;
				break;
			case Boolean:
				fieldSetterCode = setCodeTmplt;
				break;
			case Date:
			case Long:
				fieldSetterCode = setCodeTmplt;
				break;
			case Decimal:
				fieldSetterCode = setCodeTmplt;
				break;
			case NestingModel:
				fieldSetterCode = setTableDataCodeTmplt;
				break;
			case Relate:
			case KeyValue:
			default:
				LvUtil.trace("未支持的属性类型" + field.getName()+","+dataType);
				break;
			}
			javaCode.append(ToolUtilities.replaceAll(fieldSetterCode, fieldRegexMap));
		}
		javaCode.append("		return form;\r\n" + 
				"	}" ); 
		return ToolUtilities.replaceAll(javaCode.toString(), regexMap);
	}
	
	public static String convert2Dto(Class dtoClass) throws Exception {
		String formModelId = (String) ToolUtilities.getStaticFieldValue(dtoClass, "FormModelId");
		FormModel formModel = IFormMgr.get().queryFormModel(formModelId);
		Map<String,String> regexMap = new LinkedHashMap<>();
		regexMap.put("#dtoClass#", dtoClass.getSimpleName());
		List<String> hiddenField = formModel.getHiddenFields();
		StringBuffer javaCode = new StringBuffer();
		javaCode.append("public #dtoClass# convert2#dtoClass#(Form form) throws Exception {\r\n" + 
				"		#dtoClass# dto = new #dtoClass#();\r\n" + 
				"		dto.setUuid(form.getUuid()).setCode(form.getStringByCode(Form.Code));\r\n" ); 
		String setCodeTmplt = "dto.set#upperFieldCode#(form.get#Type#(#dtoClass#.s#upperFieldCode#));";
		String setTableDataCodeTmplt = "TableData #fieldCode# = form.getTable(#dtoClass#.s#upperFieldCode#);\r\n" + 
				"		if(#fieldCode# != null)\r\n" + 
				"			dto.set#upperFieldCode#(convert2List(#fieldCode#.getRows(), v->convert2#SubDtoClass#(v)));";
		String setAttachDataCodeTmplt = "List<AttachData> #fieldCode# = form.getAttachments(#dtoClass#.s#upperFieldCode#);\r\n" + 
				"		if(#fieldCode# != null)\r\n" + 
				"			dto.set#upperFieldCode#(convert2List(#fieldCode#.getRows(), v->convert2#SubDtoClass#(v)));";
		
		for(FormField field : formModel.getFieldList()) {
			if(hiddenField.contains(field.getCode())){
				continue;
			}
			if(field.getCode().equals(Form.Code))
				continue;
			String fieldSetterCode = "";
			Map<String,String> fieldRegexMap = new LinkedHashMap<>();
			String upperFieldCode = upperCase(field.getCode());
			fieldRegexMap.put("#upperFieldCode#", upperFieldCode);
			fieldRegexMap.put("#fieldCode#", field.getCode());
			DataType dataType = field.getDataTypeEnum();
			switch (dataType) {
			case Text:
			case Depend:
			case Password:
				fieldRegexMap.put("#Type#", "String");
				fieldSetterCode = setCodeTmplt;
				break;
			case Attach:
				fieldSetterCode = setAttachDataCodeTmplt;
				break;
			case Binary:
			case Image:
			case RichDocument:
				fieldSetterCode = setCodeTmplt;
				fieldRegexMap.put("#Type#", "ByteArray");
				break;
			case Boolean:
				fieldSetterCode = setCodeTmplt;
				fieldRegexMap.put("#Type#", "Boolean");
				break;
			case Date:
			case Long:
				fieldSetterCode = setCodeTmplt;
				fieldRegexMap.put("#Type#", "Long");
				break;
			case Decimal:
				fieldSetterCode = setCodeTmplt;
				fieldRegexMap.put("#Type#", "Double");
				break;
			case NestingModel:
				fieldSetterCode = setTableDataCodeTmplt;
				break;
			case KeyValue:
				fieldRegexMap.put("#Type#", "List");
				break;
			case Relate:
			default:
				LvUtil.trace("未支持的属性类型" + field.getName()+","+dataType);
				break;
			}
			javaCode.append(ToolUtilities.replaceAll(fieldSetterCode, fieldRegexMap));
		}
		
		javaCode.append("		return dto;\r\n" + 
				"	}");
		return ToolUtilities.replaceAll(javaCode.toString(), regexMap);
	}
	
	public static void main(String[] args) throws Exception {
		Class dtoClass = null;
		LvUtil.trace(convert2Form(dtoClass));
		LvUtil.trace(convert2Dto(dtoClass));
	}
}
