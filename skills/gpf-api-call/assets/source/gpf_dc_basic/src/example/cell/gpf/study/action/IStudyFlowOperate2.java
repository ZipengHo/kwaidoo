package cell.gpf.study.action;

import java.util.LinkedHashSet;

import com.leavay.dfc.gui.LvUtil;

import cell.CellIntf;
import cell.gpf.dc.basic.node.behavior.INodeLoopHeadBehavior;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cmn.anotation.ClassDeclare;
import gpf.adur.data.Form;
import gpf.adur.data.TableData;
import gpf.dc.action.intf.BaseActionIntf;
import gpf.dc.action.param.BaseActionParameter;
import gpf.dc.config.RefPDCNode;
import gpf.dc.runtime.PDCForm;
import gpf.exception.VerifyException;
@ClassDeclare(label = "流程其他操作代码样例"
,what="流程其他操作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-01-17"
,updateTime = "2025-01-17")
public interface IStudyFlowOperate2<T extends BaseActionParameter> extends CellIntf,BaseActionIntf<T>{

	@Override
	default Object execute(T input) throws Exception {
		IDCRuntimeContext rtx = input.getRtx();
		RefPDCNode headNode = INodeLoopHeadBehavior.getLoopHeadNode(rtx.getPdfInstance(), rtx.getRefPDCNode(), new LinkedHashSet<>());
		LvUtil.trace("循环头：" + headNode.getName());
		int index = INodeLoopHeadBehavior.getLoopIndex(rtx, headNode);
		LvUtil.trace("循环下标：" + index);
		PDCForm pdcForm = input.getRtx().getPdcForm();
		TableData table = pdcForm.getTable("嵌套");
		Form form = table.getData(0);
		form.setAttrValue("AAA", "AAA"+index);
		form.setAttrValue("BBB", "BBB"+index);
		TableData table2 = form.getTable("测试必填");
		if(table2 == null) {
			if(index > 1)
				throw new VerifyException("出错了 table2");
			table2 = new TableData();
			form.setAttrValue("测试必填", table2);
		}
		if(table2.isEmtpy()) {
			if(index > 1)
				throw new VerifyException("出错了 table2");
			table2.add(new Form("gpf.md.slave.TestNotNull2"));
		}
		Form form2 = table2.getData(0);
		form2.setAttrValue("AAA", "AAAA"+index);
		form2.setAttrValue("BBB", "BBBB"+index);
		TableData table3 = form2.getTable("嵌套");
		if(table3 == null) {
			if(index > 1)
				throw new VerifyException("出错了table3 ");
			table3 = new TableData();
			form2.setAttrValue("嵌套", table3);
		}
		if(table3.isEmtpy()) {
			if(index > 1)
				throw new VerifyException("出错了table3 ");
			table3.add(new Form("gpf.md.test.slave.TestNestingData"));
		}
		Form form3 = table3.getData(0);
		form3.setAttrValue("文本", "文本"+index);
//		pdcForm.setAttrValue("整数", (long)index);
		rtx.setPdcForm(pdcForm);
		return null;
	}
	
	@Override
	default Class<? extends T> getInputParamClass() {
		return (Class<? extends T>) BaseActionParameter.class;
	}
}
