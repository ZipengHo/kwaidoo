package gui.gpf;

import javax.swing.UIManager;

import com.leavay.client.util.CNClientUtil;
import com.leavay.common.BasicRes;
import com.leavay.common.util.MppContext;
import com.leavay.common.util.ToolUtilities;
import com.leavay.dfc.mgr.javaDev.JPluginAgentMgr;
import com.leavay.ms.tool.CmnUtil;
import com.leavay.nio.crpc.CRpcAdapter;
import com.project.gui.common.GuiUtil;

import wfe.gui.WfeMainFrame;

public class GpfMainFrame extends WfeMainFrame{
	private static BasicRes _res = new BasicRes("LanguageRes/wfe_zh_CN.properties", "LanguageRes/wfe_en_US.properties");
	public static void main(String[] args) throws Exception{
        MppContext.init(ToolUtilities.getAbsolutPath("conf/base.conf"));
        System.out.println("getRpcTimeout="+CRpcAdapter.getRpcTimeout());

		JPluginAgentMgr.setEnable(false);
        UIManager.setLookAndFeel("com.jtattoo.plaf.aero.AeroLookAndFeel");
        GuiUtil.setTranslator(_res);

        CmnUtil.out("Arguments:" + ToolUtilities.logString(args, false));
        CmnUtil.out(ToolUtilities.logString(System.getProperties(), true));
        
//        DBDatasource ds = new DBDatasource();
//        ds.setDriverClass("org.postgresql.Driver");
//        ds.setUrl("jdbc:postgresql://127.0.0.1:5432/cdao");
//        ds.setUserName("dfc");
//        ds.setPassword("dfc");
//        ds.setMinConns(2);
//        ds.setMaxConns(10);
//        CDaoMgr.getMe().init(ds);
        System.out.println(ToolUtilities.logString(System.getProperties(), true));
        
        CNClientUtil.setBEHost("127.0.0.1");
        CNClientUtil.setNioPort(2020);
//        CNClientUtil.setNioPort(13000);
        
        if (!logon())
        {
            System.exit(0);
            return;
        }
        WfeMainFrame frm = new WfeMainFrame();
        frm.showMainFrame();
	}
}
