package gpf.study.fe.component.fieldExtend;

import com.kwaidoo.ms.tool.CmnUtil;
import com.leavay.common.util.javac.ClassFactory;

import cmn.anotation.ClassDeclare;
import cmn.anotation.FieldDeclare;
import cmn.i18n.AbsI18n;
import fe.cmn.data.NullPojo;
import fe.cmn.data.PairDto;
import fe.cmn.editor.SelectEditorDto;
import fe.cmn.panel.PanelContext;
import fe.cmn.panel.PanelValue;
import fe.cmn.table.TableCellDto;
import fe.cmn.table.TableColumnDto;
import fe.cmn.widget.WidgetDto;
import fe.util.FeSelectUtil;
import fe.util.component.param.DataEditParam;
import fe.util.editor.valuehanlder.EditorFieldDefine;
import gpf.adur.data.BaseFormFieldExtend;
import gpf.adur.data.DataType;
import gpf.adur.data.FormField;
import gpf.dc.basic.fe.component.fieldextend.editpanel.SimpleFormFieldExtendEditPanel;
import gpf.dc.basic.i18n.GpfDCBasicI18n;
import gpf.dc.fe.annotation.formfielddisplay.AcceptDataType;
import gpf.dc.fe.component.adur.FormFieldExtendPanel;
import gpf.dc.fe.component.adur.data.field.FormFieldEditorFactory;
import gpf.dc.fe.component.adur.data.field.handler.TextSelectEditorHandler;
import gpf.dc.fe.dto.fieldextend.FormFieldDisplayIntf;
@ClassDeclare(label = "属性扩展实现样例"
,what="演示如何实现表单属性的显示扩展"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-02-21"
,updateTime = "2025-02-21")
//定义接受的属性类型，用于生成属性扩展列表说明文档
@AcceptDataType({DataType.Text})
public class StudyFormFieldExtend extends BaseFormFieldExtend implements FormFieldDisplayIntf{

	/**
	 * 
	 */
	private static final long serialVersionUID = -607284400551843521L;
	
	// 1. 定义属性显示扩展的配置参数
	@FieldDeclare(label="枚举类",desc="")
	String enumClass;

	// 2.定义属性扩展适用于哪些属性定义
	@Override
	public boolean accept(FormField field) {
		return field.getDataTypeEnum() == DataType.Text;
	}
	//3.构建属性扩展参数的配置界面组件
	@Override
	public FormFieldExtendPanel getExtendInfoEditPanel(PanelContext context, BaseFormFieldExtend data)
			throws Exception {
		//这里用简单的属性扩展编辑面板举例，如果需要更多交互编辑的，需要另外定义属性扩展编辑界面组件
		SimpleFormFieldExtendEditPanel<StudyFormFieldExtend> editPanel = new SimpleFormFieldExtendEditPanel<>();
		DataEditParam<StudyFormFieldExtend> editParam = DataEditParam.create((StudyFormFieldExtend) data);
		editPanel.setWidgetParam(editParam);
		return editPanel;
	}
	//4.构建在表单上显示的编辑组件，这里以下拉列表举例
	@Override
	public WidgetDto buildFormFieldEditor(FormFieldEditorFactory factory, PanelContext context, FormField fieldDef,
			Object form, Object fieldValue, boolean writable) throws Exception {
		SelectEditorDto editor = null;
		PairDto[] items = getSelectItems();
		String selectOption = (String) fieldValue;
		PairDto<String, String> item = FeSelectUtil.matchItem(items, selectOption);
		editor = getFeBuilderPortal().newSelect(fieldDef.getCode(), items, item);
		editor.setFilterable(true);
		editor.setWritable(writable);
		return editor;
	}
	//5.构建在表单上显示的编辑组件，当表格上需要以编辑组件样式展示时定义
	@Override
	public TableColumnDto buildTableColumnEditor(FormFieldEditorFactory factory, PanelContext context, FormField field)
			throws Exception {
		TableColumnDto column = new TableColumnDto(field.getCode(), field.getName());
		column.setEditor(buildFormFieldEditor(factory, context, field, null, null, true));
		return column;
	}
	
	//6.构建在表格行上回显的单元格文本内容，当构建的界面组件类型值与默认的属性界面组件类型值不一致时实现
	@Override
	public TableCellDto buildLabelTableCell(FormFieldEditorFactory factory, PanelContext context, Object form,
			FormField field, Object value) throws Exception {
		String sValue = (String) value;
		if (!CmnUtil.isStringEmpty(sValue)) {
			PairDto<String, String> item = FeSelectUtil.matchItem(getSelectItems(), sValue);
			return new TableCellDto(item.getValue());
		}
		return new TableCellDto("");
	}
	//7.构建在可编辑表格行上的单元格内容，当构建的界面组件类型值与默认的属性界面组件类型值不一致时实现，
	// 根据具体单元格编辑器的类型决定单元格中的值类型，以下拉列表举例，当值为空值设置NullPojo，当值不为空值设置PairDto
	@Override
	public TableCellDto buildEditorTableCell(FormFieldEditorFactory factory, PanelContext context, Object data,
			FormField field, Object value) throws Exception {
		if (NullPojo.isNull(value))
			return new TableCellDto(new NullPojo());
		PairDto[] items = getSelectItems();
		PairDto item = FeSelectUtil.matchItem(items, (String) value);
		if (item == null) {
			return new TableCellDto(new NullPojo());
		} else {
			return new TableCellDto(item);
		}
	}
	//8.构建界面值类型处理器，当构建的界面组件类型值与默认的属性界面组件类型值不一致时，需要重新声明对表单上该属性界面值的处理器，
	//如当前例子中是对文本类型属性的显示扩展为下拉列表，文本类型的界面值类型为String，下拉列表的界面值类型为PairDto,需要重新定义处理
	@Override
	public EditorFieldDefine getFormFieldEditorHandler(PanelContext panelContext, PanelValue panelValue,
													   FormField field,FormFieldEditorFactory factory) throws Exception {
		EditorFieldDefine editorDef = buildDefaultFormFieldEditorHandler(panelContext, panelValue, field, factory);
		editorDef.setEditorTypeHandler(TextSelectEditorHandler.class).setBinaryData(field);
		return editorDef;
	}

	
	public PairDto[] getSelectItems() {
		PairDto[] items = null;
		if (!CmnUtil.isStringEmpty(enumClass)) {
			try {
				Class<? extends Enum> enum1 = (Class<? extends Enum>) ClassFactory.getValidClassLoader().loadClass(enumClass.trim());
				AbsI18n i18n = GpfDCBasicI18n.get();
				items = FeSelectUtil.getEnumItemsInGroup(enum1, i18n);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return items;
	}
}
