package cell.gpf.study.action.view.param;

import java.util.Arrays;
import java.util.LinkedHashSet;

import com.kwaidoo.ms.tool.CmnUtil;

import cmn.anotation.FieldDeclare;
import gpf.dc.fe.component.editor.AssocFieldExtendQueryParam;

public class StudyAssocFieldExtendQueryParam extends AssocFieldExtendQueryParam{

	/**
	 * 
	 */
	private static final long serialVersionUID = -362079961864734505L;
	@FieldDeclare(label = "规则命名空间",desc = "多个命名空间用,分隔")
	String namespaces;
	@FieldDeclare(label = "规则",desc = "")
	String rule;
	public String getNamespaces() {
		return namespaces;
	}
	public LinkedHashSet<String> getNamespaceSet() {
		LinkedHashSet<String> set = new LinkedHashSet<>();
		if(!CmnUtil.isStringEmpty(namespaces)) {
			set.addAll(Arrays.asList(namespaces.split(",")));
		}
		return set;
	}
	public String getRule() {
		return rule;
	}
	public StudyAssocFieldExtendQueryParam setNamespaces(String namespaces) {
		this.namespaces = namespaces;
		return this;
	}
	public StudyAssocFieldExtendQueryParam setRule(String rule) {
		this.rule = rule;
		return this;
	}
	
	@Override
	public String toString() {
		return "规则命名空间：" + CmnUtil.getString(namespaces,"") + ",规则：" + CmnUtil.getString(rule,"");
	}
	
}
