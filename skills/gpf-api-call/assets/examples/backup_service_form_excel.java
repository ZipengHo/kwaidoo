import cell.cdao.IDao;
import cell.function.CConsumer;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.dc.backup.IBackupService;
import cmn.dto.Progress;
import gpf.adur.data.Form;
import gpf.dc.intf.ExportImportIntf;
import gpf.dc.intf.FormOpObserver;
import org.nutz.dao.Cnd;
import web.dto.Pair;

import java.util.ArrayList;
import java.util.function.Consumer;

public class BackupServiceFormExcelExample {

    public Pair<String, byte[]> exportExcel(
            Progress prog,
            ExportImportIntf expImpIntf,
            String formModelId,
            String statusFieldCode,
            String status) throws Exception {
        Cnd cnd = Cnd.NEW();
        cnd.where().andEquals(statusFieldCode, status);
        //导出的是带有Excel的zip压缩包
        return IBackupService.get().exportFormToExcel(prog, expImpIntf, formModelId, cnd);
    }

    public ArrayList<Form> beforeBatchImport(
            Progress prog,
            IDao dao,
            ArrayList<Form> forms,
            FormOpObserver observer) throws Exception {
        return IBackupService.get().preImportForms(
                prog,
                dao,
                forms,
                observer,
                CConsumer.NEW(new Consumer<ArrayList<Form>>() {
                    @Override
                    public void accept(ArrayList<Form> t) {
                        try {
                            IFormMgr.get().beforeBatchImportForms(prog, dao, t);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }));
    }
}
