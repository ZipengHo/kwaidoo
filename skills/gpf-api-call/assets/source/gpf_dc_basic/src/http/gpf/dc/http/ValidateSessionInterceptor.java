package gpf.dc.http;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kwaidoo.ms.tool.CmnUtil;
import com.leavay.common.util.ToolUtilities;

import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.cmn.jwt.IJwtService;
import cell.fe.gpf.dc.basic.IApplicationService;
import cell.gpf.adur.user.IUserMgr;
import cell.gpf.dc.http.CLoginRequestMapping;
import cell.web.IWebPlugin;
import cmn.anotation.ClassDeclare;
import cmn.http.servlet.HandlerInterceptor;
import cmn.http.util.HttpCookieUtil;
import cmn.http.util.HttpSessionUtil;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import gpf.adur.user.User;
import gpf.dc.basic.param.view.dto.ApplicationSetting;
import gpf.dc.http.exception.SessionErrorInfo;
import gpf.dc.http.exception.SessionException;
import web.jwt.exceptions.TokenExpiredException;
import web.mgr.WebUtil;
import web.servlet.handler.SecretConstant;
import web.util.StringUtils;
@ClassDeclare(
		label = "会话验证拦截器", 
		what = "", 
		why = "",
		how = "", 
		developer = "陈晓斌", 
		createTime = "2025-04-27", 
		updateTime = "2025-04-27", 
		version = "1.0"
		)
public class ValidateSessionInterceptor implements HandlerInterceptor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4099979698661789976L;
	private static String LOG = ValidateSessionInterceptor.class.getSimpleName();
	public final static String RefreshToken = CLoginRequestMapping.RefreshToken;
	/**
	 * 校验客户端IP
	 */
	boolean verifyClientIp = true;
	/**
	 * 校验浏览器类型
	 */
	boolean verifyUserAgent = false;
	/**
	 * 校验会话ID
	 */
	boolean verifySessionId = true;
	
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
    	HttpSession httpSession = httpServletRequest.getSession();
    	String sessionId = httpSession.getId();
    	Tracer tracer = TraceUtil.getCurrentTracer(LOG);
    	tracer.info(LOG, "http session id = " + sessionId);
    	String tokenName = SecretConstant.TOKEN;
		List<String> allCookiePaths = IWebPlugin.get().getAllCookiePath();
		
    	//首先从请求头中获取jwt串，与页面约定好存放jwt值的请求头属性名为token
        String jwt = httpServletRequest.getHeader(tokenName);
        //判断jwt是否有效
        if(!StringUtils.hasText(jwt)){
        	tracer.debug(LOG,"[登录校验拦截器]-请求头没拿到token信息，从cookie里面获取已写入的");
        	//TODO 目前从请求头没拿到token信息，所有先从cookie里面获取已写入的
        	Cookie cookie = HttpCookieUtil.getCookie(httpServletRequest.getCookies(), tokenName);
        	if(cookie != null)
        		jwt =cookie.getValue();
        	tracer.debug(LOG,"[登录校验拦截器]-cookie里的token信息:"+ jwt);
        }
        if(StringUtils.hasText(jwt)){
            //校验jwt是否有效,有效则返回json信息，无效则返回空
            AppUserInfo retJson = null;
            String refreshToken = null;
            String refreshJwt = jwt;
            long expireMilliSec = 15 * 60 * 1000;
			long refreshTokenExpireMilliSec = 7 * 24 * 60 * 60 * 1000L + expireMilliSec;
			Cookie refreshTokenCookie = WebUtil.getCookie(httpServletRequest.getCookies(), RefreshToken);
        	if(refreshTokenCookie != null) {
        		refreshToken = refreshTokenCookie.getValue();
        	}
			try{
            	retJson = IJwtService.get().validateJWT(jwt, AppUserInfo.class,null);
            }catch (TokenExpiredException e) {
            	retJson = IJwtService.get().parseJWT(jwt, AppUserInfo.class);
            	if(!CmnUtil.isStringEmpty(refreshToken)) {
            		AppUserInfo refreshUser = IJwtService.get().getUserInfo(refreshToken, AppUserInfo.class);
            		if(refreshUser != null) {
            			if(!CmnUtil.isStringEqual(refreshUser.getUserId(), retJson.getUserId())
            					|| !CmnUtil.isStringEqual(refreshUser.getSessionId(), retJson.getSessionId())) {
            				throw new SessionException(SessionErrorInfo.SessionExpired);
            			}else {
            				System.out.println("==============访问令牌已失效，刷新访问令牌===================");
            				//访问令牌已失效，刷新访问令牌
            				try(IDao dao = IDaoService.newIDao()){
            					String appCode = retJson.getAppCode();
            					String user = retJson.getName();
            					ApplicationSetting applicationSetting = IApplicationService.get().queryApplicationSettingByCode(dao, appCode);
            					if(applicationSetting == null) {
            						throw new SessionException(SessionErrorInfo.AppNotExist);
            					}
            					String userModelID = applicationSetting.getUserModelId();
	            				User userData = IUserMgr.get().queryUserByName(dao, userModelID, user,User.Code,User.Alias,User.UserName,User.TokenExpireTime);
	            				if(userData == null) {
	            					throw new SessionException(SessionErrorInfo.UserNotExist);
	            				}
	            				if(userData.getTokenExpireTime() != null) {
	            					if(userData.getTokenExpireTime() > expireMilliSec) {
	            						refreshTokenExpireMilliSec = userData.getTokenExpireTime() * 60 * 1000L;
	            					}
	            				}
	            				refreshJwt = IJwtService.get().generateJWT(retJson, expireMilliSec, null);
	            				IJwtService.get().updateRefreshTokenExpireTime(refreshToken, refreshTokenExpireMilliSec);
            				}
            			}
            		}else {
            			throw new SessionException(SessionErrorInfo.SessionExpired);
            		}
            	}else {
            		throw new SessionException(SessionErrorInfo.SessionExpired);
            	}
			}
            if(verifySessionId) {
	            if(!CmnUtil.isStringEqual(retJson.getSessionId(), sessionId)){
	            	tracer.warning(LOG, "session id 已变更，token 失效，token session id = " + retJson.getSessionId() + ", http session id = " + sessionId);
	            	throw new SessionException(SessionErrorInfo.SessionExpired);
	            }
            }
            //除了header需要有token，cookie也必须要有token，需要校验其中的用户信息是否一致，以免恶意使用高权限token进行请求
            Cookie orgCookie = WebUtil.getCookie(httpServletRequest.getCookies(), tokenName);
            if(orgCookie == null)
        		throw new SessionException(SessionErrorInfo.SessionNotFound);
        		String orgJwt =orgCookie.getValue();
        		AppUserInfo orgJson = IJwtService.get().parseJWT(orgJwt, AppUserInfo.class);
        		if(!CmnUtil.isStringEqual(retJson.getName(), orgJson.getName())) {
        			throw new SessionException(SessionErrorInfo.SessionInvalid);
        		}
                //校验客户端信息
            	String remoteAttr = httpServletRequest.getRemoteAddr();
                String userAgent = httpServletRequest.getHeader("User-Agent");
                if (ToolUtilities.isStringEqual(remoteAttr, retJson.getClientIp()) 
                		|| (!verifyClientIp)
                		) {
                	if(verifyUserAgent && !ToolUtilities.isStringEqual(userAgent, retJson.getUserAgent())) {
                		tracer.warning(LOG,"[登录校验拦截器]-客户端浏览器类型与Token中浏览器类型不一致，重新登录。当前浏览器类型:"+ userAgent);
                    	throw new SessionException(SessionErrorInfo.SessionInvalid);
                	}
                    //获取刷新后的jwt值，设置到响应头中
                    httpServletResponse.setHeader(SecretConstant.TOKEN, refreshJwt);
                    List<Cookie> allCookies = new ArrayList<>();
                    for(String cookiePath : allCookiePaths) {
                    	Cookie cookie = HttpCookieUtil.buildTokenCookie(tokenName,retJson, refreshJwt, refreshTokenExpireMilliSec,cookiePath);
                    	allCookies.add(cookie);
                    	Cookie refreshCookie = HttpCookieUtil.buildTokenCookie(RefreshToken,retJson, refreshToken, refreshTokenExpireMilliSec,cookiePath);
                    	allCookies.add(refreshCookie);
                    }
                    HttpCookieUtil.setCookie(httpServletResponse,allCookies);
                    httpServletResponse.setHeader("X-Frame-Options", "SAMEORIGIN");	
                    HttpSessionUtil.login(retJson);
                    return true;
                }else{
                	tracer.warning(LOG,"[登录校验拦截器]-客户端主机地址与JWT中存的主机地址不一致，重新登录。当前浏览器主机地址:"+ remoteAttr);
                	throw new SessionException(SessionErrorInfo.SessionInvalid);
                }
        }else {
        	tracer.warning(LOG,"[登录校验拦截器]-JWT非法或已超时，重新登录");
        	throw new SessionException(SessionErrorInfo.SessionExpired);
        }
    }
    
    @Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
    	//请求结束后要移除SessionInfo
		HttpSessionUtil.logout();
	}
    
}