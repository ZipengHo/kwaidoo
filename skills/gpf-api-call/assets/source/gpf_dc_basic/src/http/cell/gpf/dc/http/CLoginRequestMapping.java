package cell.gpf.dc.http;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.dfc.gui.LvUtil;

import cell.bap.servlet.CHttpServletRequest;
import cell.bap.servlet.IHttpServletRequest;
import cell.bap.servlet.IHttpServletResponse;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.cmn.jwt.IJwtService;
import cell.fe.gpf.dc.basic.IApplicationService;
import cell.gpf.adur.user.IUserMgr;
import cell.web.IWebPlugin;
import cmn.dto.session.SessionUtil;
import cmn.enums.ErrorLevel;
import cmn.exception.VerifyException;
import cmn.http.cells.BasicCell_RequestMapping;
import cmn.http.dto.SessionInfo;
import cmn.http.util.HttpCookieUtil;
import cmn.http.util.HttpSessionUtil;
import cmn.util.JwtUtil;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.adur.user.User;
import gpf.dc.basic.param.view.dto.ApplicationSetting;
import gpf.dc.http.AppUserInfo;
import gpf.dc.http.exception.SessionErrorInfo;
import gpf.dc.http.exception.SessionException;
import reactor.core.publisher.Flux;
import web.servlet.handler.SecretConstant;

public class CLoginRequestMapping extends BasicCell_RequestMapping implements ILoginRequestMapping{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6897639601944264584L;
//	RequestMappingContext context;
//	@Override
//	public RequestMappingContext getContext() {
//		return context;
//	}
//	@Override
//	public void setContext(RequestMappingContext context) {
//		this.context = context;
//	}
	List<String> allCookiePaths = Arrays.asList("/","/gpfdc/app");
	
	public final static String RefreshToken = "refreshToken";

	@Override
	public void login(String appCode,String user, String password) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		try(IDao dao = IDaoService.newIDao()){
			ApplicationSetting applicationSetting = IApplicationService.get().queryApplicationSettingByCode(dao, appCode);
			if(applicationSetting == null) {
				throw new SessionException(SessionErrorInfo.AppNotExist);
			}
			String userModelID = applicationSetting.getUserModelId();
			User userData = IUserMgr.get().queryUserByName(dao, userModelID, user,User.Code,User.Alias,User.UserName,User.TokenExpireTime);
			if(userData == null) {
				throw new SessionException(SessionErrorInfo.UserNotExist);
			}
			boolean success = IUserMgr.get().verifyPassword(dao, userData.getCode(), password);
			if(!success) {
				throw new SessionException(SessionErrorInfo.LoginFailed);
			}
			long expireMilliSec = 15 * 60 * 1000;
			long refreshTokenExpireMilliSec = 7 * 24 * 60 * 60 * 1000L + expireMilliSec;
			if(userData.getTokenExpireTime() != null) {
				if(userData.getTokenExpireTime() > expireMilliSec) {
					refreshTokenExpireMilliSec = userData.getTokenExpireTime() * 60 * 1000L;
				}
			}
			AppUserInfo userInfo = new AppUserInfo();
			userInfo.setUserId(userData.getCode()).setName(userData.getUserName()).setFullName(userData.getFullName())
			.setExpireMilliSec(expireMilliSec);
			userInfo.setAppCode(appCode);
			IHttpServletRequest request = getContext().getHttpServlet().getRequest();
			IHttpServletResponse response = getContext().getHttpServlet().getResponse();
			String sessionId = null;//request.getRequestedSessionId();
			if(request instanceof CHttpServletRequest) {
				HttpServletRequest originalRequest = (HttpServletRequest) ToolUtilities.getFieldValue(request, "originalRequest");
				HttpSession httpSession = originalRequest.getSession();
				sessionId = httpSession.getId();
			}
			if(CmnUtil.isStringEmpty(sessionId)) {
				sessionId = SessionUtil.allockThreadSessionIdIfNull();
			}
			String remoteAddr = request.getRemoteAddr();
			String userAgent = request.getHeader("User-Agent");
			userInfo.setClientIp(remoteAddr).setUserAgent(userAgent).setSessionId(sessionId);
			LvUtil.trace("sessionId = " + sessionId);
			//将结果设置回响应头
//			String jwt = JwtUtil.generateJWT(userInfo.getUserId(), userInfo, userInfo.getExpireMilliSec(), null);
//			userInfo.setFreshToken(jwt);
			String jwt = IJwtService.get().generateJWT(userInfo, expireMilliSec, null);
			String refreshToken = IJwtService.get().createRefreshToken(userInfo, refreshTokenExpireMilliSec);
			tracer.debug("登录成功，设置响应头Token: " + jwt);
			String tokenName = SecretConstant.TOKEN;
			response.setHeader(tokenName, jwt);
			//TODO 这里写一下cookie
//			List<String> allCookiePaths = Arrays.asList("/","/gpf/app");//IWebPlugin.get().getAllCookiePath();
			List<Cookie> allCookies = new ArrayList<>();
			//将访问令牌和刷新令牌同时写入cookies
			for(String cookiePath : allCookiePaths) {
				Cookie cookie = HttpCookieUtil.buildTokenCookie(tokenName,userInfo, jwt, refreshTokenExpireMilliSec,cookiePath);
				allCookies.add(cookie);
				Cookie refreshCookie = HttpCookieUtil.buildTokenCookie(RefreshToken,userInfo, refreshToken, refreshTokenExpireMilliSec,cookiePath);
				allCookies.add(refreshCookie);
			}
			tracer.debug("登录成功，设置Cookie: " + allCookies);
			HttpCookieUtil.setCookie(getContext().getHttpServlet(),allCookies);
		}
	}
	
	@Override
	public void logout() throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		SessionInfo sessionInfo = getContext().getSessionInfo();
		// 校验客户端信息
		if(sessionInfo instanceof AppUserInfo) {
			AppUserInfo userInfo = (AppUserInfo) sessionInfo;
			IHttpServletRequest request = getContext().getHttpServlet().getRequest();
			IHttpServletResponse response = getContext().getHttpServlet().getResponse();
			String remoteAttr = request.getRemoteAddr();
			String userAgent = request.getHeader("User-Agent");
			if (ToolUtilities.isStringEqual(remoteAttr, userInfo.getClientIp())) {
				userInfo.setExpireMilliSec(-1000);
				String expireJwt = JwtUtil.expireToken(userInfo.getUserId(), userInfo, null);
				tracer.debug("退出登录，设置响应头Token: " + expireJwt);
				// 获取刷新后的jwt值，设置到响应头中
				response.setHeader(SecretConstant.TOKEN, expireJwt);
				try {
					String tokenName = IWebPlugin.get().getCookieTokeName();
//							String cookiePath = IWebPlugin.get().getCookieMainPath();
//					List<String> allCookiePaths = IWebPlugin.get().getAllCookiePath();
					List<Cookie> allCookies = new ArrayList<>();
					for (String cookiePath : allCookiePaths) {
						Cookie cookie = HttpCookieUtil.buildTokenCookie(tokenName, userInfo, expireJwt, -1000,
								cookiePath);
						allCookies.add(cookie);
					}
					HttpCookieUtil.setCookie(getContext().getHttpServlet(), allCookies);
					tracer.debug("退出登录，设置Cookie: " + allCookies);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		//注意：HttpSessionUtil的操作为ThreadLocal操作，不支持云调试
		HttpSessionUtil.logout();	
	}
	
	@Override
	public User getCurrentUser() throws Exception {
		SessionInfo sessionInfo = getContext().getSessionInfo();
		if(sessionInfo == null)
			throw new SessionException(SessionErrorInfo.SessionNotFound);
		Tracer tracer = TraceUtil.getCurrentTracer();
		AppUserInfo userInfo = (AppUserInfo) sessionInfo;
		String appCode = userInfo.getAppCode();
		tracer.info("appCode = " + appCode);
		if(CmnUtil.isStringEmpty(appCode)) {
			throw new SessionException(ErrorLevel.WARN, "", "应用编号不存在！");
		}
		String userCode = sessionInfo.getUserId();
		tracer.info("userCode = " + userCode);
		try(IDao dao = IDaoService.newIDao()){
			ApplicationSetting appSetting = IApplicationService.get().queryApplicationSettingByCode(dao, appCode);
			if(appSetting == null)
				throw new VerifyException("应用["+appCode+"]不存在！");
			String userModelID = appSetting.getUserModelId();
			User user = IUserMgr.get().queryUserByCode(dao, userModelID, userCode, User.UUID,User.Code,User.UserName,User.FullName,User.Phone,User.Email,User.Gender);
			return user;
		}
	}
	
}
