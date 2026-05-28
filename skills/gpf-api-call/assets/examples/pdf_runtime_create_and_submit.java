import cell.cdao.IDao;
import cell.gpf.dc.runtime.IDCRuntimeContext;
import cell.gpf.dc.runtime.IPDFRuntimeMgr;
import gpf.adur.data.ResultSet;
import gpf.dc.runtime.PDCForm;
import gpf.dc.runtime.PDFForm;
import org.nutz.dao.Cnd;

public class PdfRuntimeCreateAndSubmitExample {

    public PDCForm createAndSubmit(
            IDao dao,
            String pdfUuid,
            String operator,
            String actionName,
            String userModelId,
            String orgModelId,
            String title) throws Exception {
        IDCRuntimeContext rtx = IPDFRuntimeMgr.get().newRuntimeContext();
        rtx.setDao(dao);
        rtx.setOperator(operator);
        rtx.setActionName(actionName);
        rtx.setUserModelId(userModelId);
        rtx.setOrgModelId(orgModelId);

        PDCForm startForm = IPDFRuntimeMgr.get().newStartForm(rtx, pdfUuid, false);
        startForm.setAttrValue("标题", title);

        return IPDFRuntimeMgr.get().createAndSubmitPDCForm(
                pdfUuid,
                startForm.getNodeKey(),
                rtx,
                startForm);
    }

    public ResultSet<PDFForm> queryPage(String pdfUuid, String creator, int pageNo, int pageSize) throws Exception {
        Cnd cnd = Cnd.NEW();
        cnd.where().andEquals(PDFForm.Creator, creator);
        return IPDFRuntimeMgr.get().queryPDFFormPage(pdfUuid, cnd, null, pageNo, pageSize);
    }
}
