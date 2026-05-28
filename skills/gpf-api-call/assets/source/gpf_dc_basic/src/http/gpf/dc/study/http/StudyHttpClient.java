package gpf.dc.study.http;

import java.io.Serializable;

import cmn.enums.ErrorLevel;
import cmn.exception.BaseException;
import cmn.util.JsonUtil;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;

public class StudyHttpClient {

	public final static String baseApiUrl = "http://127.0.0.1:8090";

	public static class Result implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6598936044553466058L;
		public static final int ERROR = 0;
		public static final int SUCCESS = 1;
		public static final int LOGIN = 2;
		public static final int INFO = 3;
		public static final int ALERT = 4;
		Integer STATE;
		String ERR_CODE;
		String ERR_MSG;
		String ERR_BRIEF;
		Object CONTENT;

		public int getSTATE() {
			return STATE;
		}

		public String getERR_CODE() {
			return ERR_CODE;
		}

		public Result setERR_CODE(String eRR_CODE) {
			ERR_CODE = eRR_CODE;
			return this;
		}

		public String getERR_MSG() {
			return ERR_MSG;
		}

		public String getERR_BRIEF() {
			return ERR_BRIEF;
		}

		public Object getCONTENT() {
			return CONTENT;
		}

		public Result setSTATE(int sTATE) {
			STATE = sTATE;
			return this;
		}

		public Result setERR_MSG(String eRR_MSG) {
			ERR_MSG = eRR_MSG;
			return this;
		}

		public Result setERR_BRIEF(String eRR_BRIEF) {
			ERR_BRIEF = eRR_BRIEF;
			return this;
		}

		public Result setCONTENT(Object cONTENT) {
			CONTENT = cONTENT;
			return this;
		}

		public boolean isSuccess() {
			return STATE != null && STATE == SUCCESS;
		}

		public boolean isError() {
			return STATE != null && STATE == ERROR;
		}

	}

	public static Result parseResult(String content) throws Exception {
		return parseResult(content, false);
	}

	public static Result parseResult(String content, boolean isDownload) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		tracer.debug("响应内容：" + content);
//		try (IJson json = IJsonService.get().getJson()) {
//			Result result = json.fromJson(content, Result.class);
		Result result = JsonUtil.fromJson(content, Result.class);
			if (result.isSuccess()) {
				return result;
			} else {
				if (result.isError()) {
					throw new BaseException(ErrorLevel.WARN, result.getERR_CODE(), result.getERR_BRIEF(),
							new Exception(result.getERR_MSG()));
				} else {
					if (isDownload) {
						result.setSTATE(Result.SUCCESS);
						result.setCONTENT(content);
						return result;
					} else {
						throw new Exception(result.getERR_BRIEF(), new Exception(result.getERR_MSG()));
					}
				}
			}
//		}
	}
//	// 维护Cookie键值对
//    private final static Map<String, String> cookieMap = new HashMap<>();
//	 /**
//     * 将本地Cookie添加到请求头
//     */
//    private static void addCookiesToRequest(HttpRequest request) {
//        if (cookieMap.isEmpty()) {
//            return;
//        }
//        // 构建Cookie字符串（name1=value1; name2=value2）
//        StringBuilder cookieStr = new StringBuilder();
//        for (Map.Entry<String, String> entry : cookieMap.entrySet()) {
//            if (cookieStr.length() > 0) {
//                cookieStr.append("; ");
//            }
//            cookieStr.append(entry.getKey()).append("=").append(entry.getValue());
//        }
//        // 设置Cookie请求头
//        request.header("Cookie", cookieStr.toString());
//    }
//
//    /**
//     * 从响应头的Set-Cookie更新本地Cookie池
//     */
//    private static void updateCookiesFromResponse(HttpResponse response) {
//        List<String> setCookieHeaders = response.headers().get("Set-Cookie");
//        if (setCookieHeaders == null || setCookieHeaders.isEmpty()) {
//            return;
//        }
//
//        // 解析每个Set-Cookie头
//        for (String setCookie : setCookieHeaders) {
//            // Set-Cookie格式示例：SESSIONID=abc123; Path=/; HttpOnly; Secure
//            // 提取键值对部分（分号前的内容）
//            String[] cookieParts = setCookie.split(";");
//            if (cookieParts.length == 0) {
//                continue;
//            }
//            String[] keyValue = cookieParts[0].split("=", 2);
//            if (keyValue.length == 2) {
//                String name = keyValue[0].trim();
//                String value = keyValue[1].trim();
//                // 更新Cookie池（覆盖旧值）
//                cookieMap.put(name, value);
//            }
//        }
//    }

	public static void login(HttpRequest request, String appCode, String user, String password) throws Exception {
//		HttpResponse response = HttpRequest.post(urlStr + "/gpfdc/app/login")
//		addCookiesToRequest(request);
		request.setUrl(baseApiUrl + "/gpfdc/app/login");
		HttpResponse response = request
				.form("appCode", appCode)
				.form("user", user).form("password", password).execute();
//		updateCookiesFromResponse(response);
		
		String content = response.body();
		if (response.isOk()) {
			Result result = parseResult(content);
			if (result.isSuccess()) {
				System.out.println(content);
			} else {
//					throw new Exception(result.getERR_MSG());
			}
		} else if (response.getStatus() == HttpStatus.HTTP_NOT_FOUND) {
			throw new Exception("Http状态码:" + response.getStatus() + ",未找到请求接口：" + request.getUrl());
		} else {
			throw new Exception("Http状态码:" + response.getStatus() + ",响应内容：" + content);
		}
	}

	public static Object getCurrentUser(HttpRequest request) throws Exception {
//		HttpResponse response = HttpRequest.post(urlStr + "/gpfdc/app/getCurrentUser").execute();
//		addCookiesToRequest(request);
		request.setUrl(baseApiUrl + "/gpfdc/app/getCurrentUser");
		HttpResponse response = request.execute();
		String content = response.body();
		if (response.isOk()) {
			Result result = parseResult(content);
			if (result.isSuccess()) {
				return result.getCONTENT();
			} else {
				throw new Exception(result.getERR_MSG());
			}
		} else if (response.getStatus() == HttpStatus.HTTP_NOT_FOUND) {
			throw new Exception("Http状态码:" + response.getStatus() + ",未找到请求接口：" + request.getUrl());
		} else {
			throw new Exception("Http状态码:" + response.getStatus() + ",响应内容：" + content);
		}
	}

	public static void main(String[] args) throws Exception {
		// 创建一个 CookieStore 用于存储和共享 Cookie
		Tracer tracer = TraceUtil.getCurrentTracer();
		// 创建客户端配置
		HttpRequest request = HttpUtil.createRequest(Method.POST, baseApiUrl)
				 .enableDefaultCookie(); // 启用默认的Cookie管理
		// 获取 HttpClient实例
		login(request, "JIT", "admin", "123456");
		Object userObj = getCurrentUser(request);
		tracer.info(userObj);
	}
}
