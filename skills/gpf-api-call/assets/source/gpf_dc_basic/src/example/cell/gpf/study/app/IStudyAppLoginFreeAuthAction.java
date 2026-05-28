package cell.gpf.study.app;

import cell.CellIntf;
import cell.cdao.IDao;
import cell.gpf.adur.user.IUserMgr;
import cmn.anotation.ClassDeclare;
import cmn.util.JsonUtil;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import com.kwaidoo.ms.tool.CmnUtil;
import fe.cmn.app.Context;
import fe.cmn.data.UrlMsgDto;
import fe.cmn.sys.QueryUrl;
import gpf.adur.user.User;
import gpf.dc.basic.fe.component.BaseFeActionIntf;
import gpf.dc.basic.fe.component.app.AppCacheUtil;
import gpf.dc.basic.fe.intf.AppCacheMgrIntf;
import gpf.dc.basic.param.view.BaseFeActionParameter;
import gpf.dc.basic.param.view.dto.ApplicationSetting;
import gpf.exception.VerifyException;

@ClassDeclare(label = "应用免密登录动作代码样例"
,what="应用免密登录动作代码样例"
, why = ""
, how = ""
,developer="陈晓斌"
,version = "1.0"
,createTime = "2025-09-05"
,updateTime = "2025-09-05")
public interface IStudyAppLoginFreeAuthAction <T extends BaseFeActionParameter> extends CellIntf, BaseFeActionIntf<T>{
	@Override
	default Object execute(T input) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		Context context = input.getContext();
		ApplicationSetting appSetting = AppCacheUtil.getSetting(context);
		AppCacheMgrIntf cacheMgr = AppCacheUtil.getAppCacheMgr(context, appSetting);
		//获取信道缓存中的初始页面链接参数
		UrlMsgDto urlMsg = (UrlMsgDto) cacheMgr.getCacheValue(context, AppCacheMgrIntf.UrlMsg);
		if(urlMsg == null) {
			urlMsg = QueryUrl.queryUrlMsg(context.getChannel());
		}
		String queryParams = urlMsg.getSearch();
		//从？之后开始
		queryParams = queryParams.substring(1);
		String[] paramPairStrs = queryParams.split("&");
		boolean isMatch = false;
		for(String paramPairStr : paramPairStrs){
			String[] paramPair = paramPairStr.split("=");
			if(paramPair.length == 2){
				if(paramPair[0].equals("token")){
					isMatch = CmnUtil.isStringEqual(paramPair[1],"who");
					if(!isMatch){
						throw new VerifyException("Token校验失败！" + paramPair[1]);
					}
					break;
				}
			}
		}
		tracer.info("urlMsg = "+JsonUtil.toJson(urlMsg));
		String userModelId = input.getRtx().getUserModelId();
		IDao dao = input.getRtx().getDao();
		String name = "test";
		//最终返回免登的用户信息
		User user = IUserMgr.get().queryUserByName(dao, userModelId, name);
		user.setExtField("扩展参数","扩展值");
		return user;
	}
	@Override
	default Class<? extends T> getInputParamClass() {
		//填写类上泛型T声明的动作模型参数类
		return (Class<? extends T>) BaseFeActionParameter.class;
	}

}
