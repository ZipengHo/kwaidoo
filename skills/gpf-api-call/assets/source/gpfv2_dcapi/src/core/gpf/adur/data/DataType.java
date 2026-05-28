package gpf.adur.data;

import java.util.HashMap;
import java.util.Map;

import com.kwaidoo.ms.tool.CmnUtil;
/**
 * 数据类型
 * @author chenxb
 *
 */
public enum DataType {
	Text() //文本
	,Boolean() //布尔值
	,Long() //整数
	,Decimal()//小数
	,Password() //密码
	,Date() //时间
	,Attach()//附件
	,WebAttach()//网络附件
	,Binary()//字节数组
	,Relate()//关联
	,Depend()//强关联
	,NestingModel()//嵌套表格
	,SubSheet()//子sheet
	,RichDocument()//富文本
	,KeyFormField()
	,KeyValue()
	,SelectFormField()
	,SelectSheetField()//选择属性列表，查询属性列表的接口，入参，type, className,configUuid,key,
	,SelectNode()
	,Image()//图片
	;
	
	private static Map<String,DataType> map = new HashMap<String,DataType>(){
		{
			put(Text.name(), Text);
			put(Boolean.name(), Boolean);
			put(Long.name(), Long);
			put(Decimal.name(), Decimal);
			put(Password.name(), Password);
			put(Date.name(), Date);
			put(Attach.name(), Attach);
			put(WebAttach.name(),WebAttach);
			put(Binary.name(), Binary);
			put(Relate.name(), Relate);
			put(Depend.name(), Depend);
			put(NestingModel.name(), NestingModel);
			put(SubSheet.name(), SubSheet);
			put(RichDocument.name(), RichDocument);
			put(KeyFormField.name(), KeyFormField);
			put(KeyValue.name(), KeyValue);
			put(SelectFormField.name(), SelectFormField);
			put(SelectSheetField.name(), SelectSheetField);
			put(SelectNode.name(), SelectNode);
			put(Image.name(), Image);
		}
	};
	DataType() {
	}
	
	public static DataType fromValue(String dataType) { 
		if(CmnUtil.isStringEmpty(dataType))
			return null;
		return map.get(dataType);
//		for(DataType type : DataType.values()) {
//			if(CmnUtil.isStringEqual(type.name(), dataType))
//				return type;
//		}
//		return null;
	}
	
}
