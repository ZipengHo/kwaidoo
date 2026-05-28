package cell.gpf.study.action;

import java.util.ArrayList;
import java.util.List;

import cell.CellIntf;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cell.gpf.study.nadur.form.StudyFormMockConst;
import cell.gpf.study.nadur.pdf.StudyPDFMockConst;
import cmn.anotation.ClassDeclare;
import cmn.util.Tracer;
import gpf.dc.action.intf.FormPrivilegeIntf;
import gpf.dc.action.param.BaseActionParameter;
import gpf.dc.basic.expression.matchUser.MatchIentifyRuleIntf;
import gpf.dto.model.data.ActionPrivilegeDto;
import gpf.dto.model.data.FieldPrivilegeDto;
import gpf.dto.model.data.FormPrivilegeDto;
@ClassDeclare(label = "流程表单权限代码样例"
,what="流程表单权限代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyCaculateFormPrivilege <T extends BaseActionParameter> extends  CellIntf,FormPrivilegeIntf<T>{


	@Override
	default FormPrivilegeDto caculatePrivilege(T input) throws Exception {
		IDCRuntimeContext rtx = input.getRtx();
		//要计算权限的身份
		String currentIdentify = (String) rtx.getParam(MatchIentifyRuleIntf.Key_CurrentIdentify);
		Tracer tracer = newTracer();
		tracer.info("当前身份：" + currentIdentify);
		FormPrivilegeDto formPrivilege = new FormPrivilegeDto();
		//设置表单属性权限
		List<FieldPrivilegeDto> fieldPrivileges = new ArrayList<>();
		fieldPrivileges.add(new FieldPrivilegeDto()
				.setField(IFormMgr.get().getFieldCode(StudyFormMockConst.TextField))
				.setVisible(true).setWritable(true)
				);
		formPrivilege.setFieldPrivileges(fieldPrivileges);
		//设置动作权限
		List<ActionPrivilegeDto> actionPrivileges = new ArrayList<>();
		actionPrivileges.add(new ActionPrivilegeDto().setName(StudyPDFMockConst.TestAction).setOperatable(true).setVisible(true));
		formPrivilege.setActionPrivileges(actionPrivileges);
		
		return formPrivilege;
	}
	
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseActionParameter.class;
	}

}
