package octo.cm.httpservice;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bap.cells.Cells;
import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;
import com.kwaidoo.ms.tool.Utils;
import com.leavay.dfc.gui.LvUtil.AutoTracer;

import bap.cells.exception.ClassLoaderConflictException;
import cell.octo.cm.logservice.ILogService;
import cmn.anotation.ClassDeclare;
import cmn.enums.ErrorLevel;
import cmn.exception.BaseException;
import cmn.http.servlet.HandlerInterceptor;
import cmn.http.util.HttpSessionUtil;
import cmn.servlet.RepeatableReadRequestWrapper;
import cmn.util.JsonUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.useragent.Browser;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import gpf.exception.VerifyException;
import octo.cm.dto.logservice.HttpRequestLogDto;

@ClassDeclare(label = "请求日志记录拦截器", what = "", why = "", how = "", developer = "陈晓斌", createTime = "2025-04-27", updateTime = "2025-04-27", version = "1.0")
public class HttpRequestLogInterceptor implements HandlerInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4099979698661789976L;
	private static String LOG = HttpRequestLogInterceptor.class.getSimpleName();

	private static ThreadLocal<HttpRequestLogDto> requestLogLocal = new ThreadLocal<>();
	private static ThreadLocal<String> BusDomainCodeLocal = new ThreadLocal<>();
	
	public static String getBusDomainCode() {
		return BusDomainCodeLocal.get();
	}

	public static HttpRequestLogDto getRequestLog() {
		return requestLogLocal.get();
	}

	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object handler) throws Exception {
		HttpRequestLogDto log = new HttpRequestLogDto();
		String path = URLUtil.getPath(httpServletRequest.getRequestURI());
		String requestMethod = httpServletRequest.getMethod();
		log.setUrl(path).setRequestMethod(requestMethod).setStartTime(System.currentTimeMillis());
		log.setCode(ToolUtilities.allockUUIDWithUnderline());
		fillRequestLog(httpServletRequest, log);
		requestLogLocal.set(log);
		BusDomainCodeLocal.set(httpServletRequest.getHeader("Busdomaincode"));
		return true;
	}
	
	private void fillRequestLog(HttpServletRequest httpServletRequest,HttpRequestLogDto log) {
		String clientIp = getClientIp(httpServletRequest);
		if(clientIp != null) {
			log.setClientIP(clientIp);
		}
		String uaStr = httpServletRequest.getHeader("User-Agent");
		String sessionId = httpServletRequest.getSession().getId();
		log.setUserAgent(uaStr);
		log.setSessionId(sessionId);
		if (uaStr != null) {
			UserAgent userAgent = UserAgentUtil.parse(uaStr);
			if(userAgent != null) {
				Browser browser = userAgent.getBrowser();
				if(browser != null) {
					log.setBrowserName(browser.getName())
//					.setBrowserType(browser.getVersion(uaStr))
					;
				}
				log.setClientType(userAgent.getPlatform().getName());
			}
			if(isMiniProgram(uaStr)) {
				log.setBrowserType("MiniProgram");
			}else {
				log.setBrowserType("Web");
			}
		}
		//请求头信息
		Map<String,String> requestHeaders = new LinkedHashMap<>();
		Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
		while(headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			String value = httpServletRequest.getHeader(headerName);
			requestHeaders.put(headerName, value);
		}
		log.setRequestHeader(JsonUtil.toPrettyJson(requestHeaders));
		//请求参数
		Map<String,String> requestParams = new LinkedHashMap<>();
		Enumeration<String> paramNames = httpServletRequest.getParameterNames();
		while(paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			String value = httpServletRequest.getParameter(paramName);
			requestParams.put(paramName, value);
		}
		log.setParams(JsonUtil.toPrettyJson(requestParams));
		//请求体
		if(httpServletRequest instanceof RepeatableReadRequestWrapper && CmnUtil.isStringEqual(httpServletRequest.getContentType(), "application/json")) {
			String requestBody = ((RepeatableReadRequestWrapper)httpServletRequest).getRequestBodyAsString();
			log.setRequestBody(requestBody);
		}
	}
	
	public static byte[] getBytes(InputStream ins) throws IOException {
		ByteArrayOutputStream out = null;
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(ins);
			out = new ByteArrayOutputStream();
			byte[] bs = new byte[1024 * 10];
			int offset;
			while ((offset = in.read(bs)) > -1) {
				out.write(bs, 0, offset);
			}
			return out.toByteArray();
		} finally {
			Utils.close(out);
		}
	}

	/**
	 * 判断是否运行在微信小程序中
	 * @param userAgent
	 * @return
	 */
	public static boolean isMiniProgram(String userAgent) {
		if(CmnUtil.isStringEmpty(userAgent))
			return false;
		//PC端浏览器标识，先用miniProgram 忽略大小写判定
		//Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 MicroMessenger/7.0.20.1781(0x6700143B) NetType/WIFI MiniProgramEnv/Windows WindowsWechat/WMPF WindowsWechat(0x63090a13) XWEB/9129
		return userAgent.toLowerCase().contains("miniprogram");
	}
	/**
	 * 获取客户端 IP（适配反向代理）
	 */
	public static String getClientIp(HttpServletRequest request) {
		if (request == null)
			return "未知 IP";

		// 优先读取反向代理头（Hutool 的 ServletUtil 可简化获取头信息，也可直接用 request.getHeader()）
		String[] ipHeaders = { "X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP" };
		for (String header : ipHeaders) {
			String ip = request.getHeader(header);
			if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
				// 处理多 IP 场景（取第一个）
				if (ip.contains(",")) {
					ip = ip.split(",")[0].trim();
				}
				return ip;
			}
		}

		// 兜底：原生远程地址（本地测试转换 IPv6 回环地址）
		String ip = request.getRemoteAddr();
		return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) throws Exception {
		// 请求结束后要移除SessionInfo
		try {
			HttpRequestLogDto log = requestLogLocal.get();
			long endTime = System.currentTimeMillis();
			long costTime = endTime - log.getStartTime();
			log.setEndTime(endTime).setCostTime(costTime);
			AutoTracer tracer = HttpSessionUtil.getTracer();
			if (tracer != null) {
				log.setTrace(tracer.getTrace());
				tracer.clear();
			}
			if (ex != null) {
				BaseException exp1 = ToolUtilities.getCauseExcpetion(ex, BaseException.class);
				if (exp1 != null) {
					log.setErrorBrief(exp1.getMessage()).setErrorLevel(exp1.getErrorLevel().name())
							.setErrorCode(exp1.getErrorCode());
				} else {
					VerifyException exp2 = ToolUtilities.getCauseExcpetion(ex, VerifyException.class);
					if (exp2 != null) {
						log.setErrorLevel(ErrorLevel.INFO.name()).setErrorBrief(exp2.getMessage());
					} else {
						cmn.exception.VerifyException exp3 = ToolUtilities.getCauseExcpetion(ex,
								cmn.exception.VerifyException.class);
						if (exp3 != null) {
							log.setErrorLevel(ErrorLevel.INFO.name()).setErrorBrief(exp3.getMessage());
						} else {
							Throwable exp4 = ToolUtilities.getExceptionRootCause(ex);
							log.setErrorLevel(ErrorLevel.ERROR.name()).setErrorBrief(exp4.getMessage());
						}
					}
				}
				log.setRunError(true).setError(ToolUtilities.getFullExceptionStack(ex, true));
			}
			Collection<String> headerNames = response.getHeaderNames();
			Map<String,String> responseHeaders = new LinkedHashMap<>();
			for(String headerName : headerNames) {
				String value = response.getHeader(headerName);
				responseHeaders.put(headerName, value);
			}
			log.setResponseHeader(JsonUtil.toPrettyJson(responseHeaders));
			try {
				ILogService.get().logHttpRequest(log);
			}catch (ClassLoaderConflictException e) {
				try{
					ILogService logService = (ILogService) Cells.get(ILogService.class.getName());
					logService.logHttpRequest(log);
//					ToolUtilities.warnAndError(LOG, ToolUtilities.getFullExceptionStack(e));
				}catch (ClassLoaderConflictException ex1){
					tracer.trace("Cells.get(ILogService.class.getName()) 执行失败！");
					ToolUtilities.warnAndError(LOG, ToolUtilities.getFullExceptionStack(ex1));
				}
			}
		} catch (Exception e) {
			ToolUtilities.warnAndError(LOG, ToolUtilities.getFullExceptionStack(e));
		} finally {
			requestLogLocal.remove();
			BusDomainCodeLocal.remove();
		}
	}

}