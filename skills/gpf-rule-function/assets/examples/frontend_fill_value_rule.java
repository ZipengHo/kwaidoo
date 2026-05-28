package cell.example.rule;

import cell.CellIntf;
import cell.gpf.adur.data.IFormMgr;
import cmn.anotation.ClassDeclare;
import cmn.anotation.InputDeclare;
import cmn.anotation.MethodDeclare;
import fe.cmn.panel.PanelContext;
import fe.util.component.AbsComponent;
import gpf.adur.data.AssociationData;
import gpf.adur.data.Form;
import gpf.dc.basic.fe.component.view.AbsFormView;
import gpf.dc.basic.param.view.BaseFeActionParameter;

@ClassDeclare(
        label = "界面填值规则",
        what = "根据界面交互自动回填值",
        why = "提升表单录入效率",
        how = "在字段值变化事件中配置使用",
        developer = "开发者",
        createTime = "2026-03-10",
        updateTime = "2026-03-10",
        version = "1.0"
)
public interface FrontendFillValueRule extends CellIntf {
    String FIELD_PRODUCT = "产品";
    String FIELD_PRICE = "单价";
    String FIELD_SUBTOTAL = "小计";
    String FIELD_QUANTITY = "数量";

    @MethodDeclare(
            label = "回填单价小计",
            what = "选择产品后回填单价并计算小计",
            how = "在产品字段值变化事件中使用",
            why = "减少重复录入和人工计算",
            inputs = {
                    @InputDeclare(desc = "动作参数", name = "input", label = "动作参数", exampleValue = "$ActionParameter$"),
                    @InputDeclare(desc = "表单数据", name = "form", label = "表单数据", exampleValue = "$form$")
            }
    )
    default void fillPriceAndSubtotal(BaseFeActionParameter input, Form form) throws Exception {
        PanelContext panelContext = input.getPanelContext();
        AbsFormView absFormView = (AbsFormView) ((AbsComponent) input.getCurrentComponent());
        AssociationData product = form.getAssociation(FIELD_PRODUCT);
        String priceFieldCode = IFormMgr.get().getFieldCode(FIELD_PRICE);
        String subtotalFieldCode = IFormMgr.get().getFieldCode(FIELD_SUBTOTAL);

        if (product == null || product.getForm() == null) {
            absFormView.setEditorValue(panelContext, form, priceFieldCode, null);
            absFormView.setEditorValue(panelContext, form, subtotalFieldCode, null);
            return;
        }

        Form productForm = product.getForm();
        Double price = productForm.getDouble(FIELD_PRICE);
        Long quantity = form.getLong(FIELD_QUANTITY);
        absFormView.setEditorValue(panelContext, form, priceFieldCode, price);
        if (price != null && quantity != null) {
            absFormView.setEditorValue(panelContext, form, subtotalFieldCode, price * quantity);
        }
    }
}
