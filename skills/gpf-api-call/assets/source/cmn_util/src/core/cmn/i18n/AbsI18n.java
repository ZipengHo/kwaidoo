package cmn.i18n;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import com.kwaidoo.ms.tool.CmnUtil;
import com.leavay.client.util.CNClientUtil;
import com.leavay.common.util.Utils;

import cn.hutool.core.io.resource.InputStreamResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.setting.Setting;
import io.netty.util.CharsetUtil;

public abstract class AbsI18n implements I18nIntf{
	private Setting res;//new Setting("LanguageRes/cmn_util_i18n.setting");
	
	public AbsI18n() {
		initSetting();
	}
	
	protected void initSetting() {
		String resFileName = getResourceFileName();
		List<Resource> resources = getResources();
		if(!CmnUtil.isStringEmpty(resFileName)) {
			String path = "LanguageRes/"+resFileName;
			URL url = CNClientUtil.getResourceURL(path);
            if (url != null) {
            	InputStream ins = null;
            	try {
            		ins = url.openStream();
					ByteArrayInputStream bis = new ByteArrayInputStream(Utils.getBytes(ins));
					Resource memRes = new InputStreamResource(bis);
					res = new Setting(memRes,CharsetUtil.UTF_8,true);
				} catch (IOException e) {
					e.printStackTrace();
				}finally {
					Utils.close(ins);
				}
//            	res = new Setting(url,CharsetUtil.UTF_8,true);
            }
		}else if(!CmnUtil.isCollectionEmpty(resources)) {
			res = new Setting();
			for(Resource resource : resources) {
				Setting setting = new Setting(resource, CharsetUtil.UTF_8, true);
				res.addSetting(setting);
			}
		}
		if(res == null)
			res = new Setting();
		if(resFileName != null) {
			File localFile = new File("./conf/LanguageRes/",resFileName);
			if(localFile.exists()) {
				Setting localRes = new Setting(localFile, CharsetUtil.UTF_8, true);
				res.addSetting(localRes);
			}
		}
	}
	
	@Override
	public List<Resource> getResources() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Setting getSetting() {
		return res;
	}


}
