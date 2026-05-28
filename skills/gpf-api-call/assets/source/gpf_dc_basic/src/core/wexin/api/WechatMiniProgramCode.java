package wexin.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.kwaidoo.ms.tool.Utils;

import cell.fe.gpf.dc.basic.IAppFeLoginPage;
import cmn.http.HttpStatus;
import cmn.util.JsonUtil;
import cmn.util.TraceUtil;
import cmn.util.Tracer;

// 自定义Token无效异常
class TokenInvalidException extends Exception {
	public TokenInvalidException(String message) {
		super(message);
	}
}

// 自定义场景值转换异常
class SceneConvertException extends Exception {
	public SceneConvertException(String message, Throwable cause) {
		super(message, cause);
	}
}

public class WechatMiniProgramCode {

	// 获取access_token的URL
	private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

	// 获取不限制的小程序码的URL
	private static final String UNLIMITED_CODE_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=%s";

	// 缓存access_token和过期时间
	private static final Map<String, String> TOKEN_CACHE = new ConcurrentHashMap<>();
	private static final ReentrantLock LOCK = new ReentrantLock();

	// 提前5分钟刷新token，避免在临界时间点失效
	private static final long REFRESH_ADVANCE = 5 * 60 * 1000;

	// 构建参数对应的scene
	private static final String BUILD_SCENE_URL = "/shortlink/buildSceneOfProjectConfig?projectConfig=%s&expireTimeMills=-1";

	// 获取scene对应的参数
	private static final String GET_SCENE_URL = "/shortlink/getProjectConfigByScene?scene=%s";

	public static void main(String[] args) {
		Tracer tracer = TraceUtil.getCurrentTracer();
		try {
			// 小程序appId和secret
			String APP_ID = "wx9d63191eb6312b0e";
			String APP_SECRET = "";
			String baseUrl = "https://office.kwaidoo.com/wxmp/app";
			// 测试参数与场景值转换
			Map<String, Object> testParams = new LinkedHashMap<>();
			testParams.put("url", "wss://kwaidoo.com/digitalOM_ws");
			testParams.put("cell", IAppFeLoginPage.class.getName());
			Map<String, Object> initParam = new LinkedHashMap<>();
			initParam.put("appCode", "DOS");
			initParam.put("appSessionKey", "");
			initParam.put("systemUuid", "27cb79b5_a96d_4471_94f2_0b26b1eb98b7");
			testParams.put("initParam", initParam);
			//设置是否忽略当前配置的缓存并清理已有的缓存
			testParams.put("ignoreSave",true);
			testParams.put("clean",true);

//			String scene = getSenceOfParams(baseUrl, testParams);
//			tracer.info("生成的scene值: " + scene);
//
//			String paramsJson = getParamsOfScene(baseUrl, scene);
//			tracer.info("根据scene获取的参数: " + paramsJson);
//
//			// 获取access_token（内部会处理缓存）
//			String accessToken = getAccessToken(APP_ID, APP_SECRET);
//			tracer.info("获取到的access_token: " + accessToken);
//
//			if (accessToken == null) {
//				tracer.info("获取access_token失败");
//				return;
//			}
//
//			// 生成不限制的小程序码
//			String page = "pages/index/index"; // 小程序页面路径
//			int width = 430; // 小程序码宽度
//			boolean autoColor = false; // 是否自动配置线条颜色
			String envVersion = "trial";
			// 生成小程序码并获取字节数据
			byte[] codeData = generateUnlimitedCodeByProjectConfig(baseUrl, testParams, APP_ID, APP_SECRET, envVersion);
//			byte[] codeData = generateUnlimitedCodeWithTokenRetry(scene, page, width, autoColor, APP_ID, APP_SECRET,
//					envVersion);

			if (codeData != null && codeData.length > 0) {
				tracer.info("小程序码生成成功，数据长度: " + codeData.length + "字节");
				// 保存到文件
				Utils.writeFile("D:/小程序码.png", codeData);
			} else {
				tracer.info("小程序码生成失败");
			}
		} catch (TokenInvalidException e) {
			tracer.info("Token无效，需要重新获取: " + e.getMessage());
			// 实现重试逻辑
		} catch (SceneConvertException e) {
			tracer.info("场景值转换失败: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将参数转换为scene值（核心修改：按HTTP状态码判定结果，500时返回错误堆栈）
	 * 
	 * @param params 要转换的参数映射
	 * @return 生成的scene值（正常场景）
	 * @throws SceneConvertException 转换过程中发生错误时抛出（含状态码异常、500堆栈信息）
	 */
	private static String getSenceOfParams(String baseUrl, Map<String, Object> params) throws SceneConvertException {
		Tracer tracer = TraceUtil.getCurrentTracer();
		// 1. 入参合法性校验
		if (params == null || params.isEmpty()) {
			throw new SceneConvertException("参数不能为空", null);
		}

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			// 2. 构建请求参数与URL
			String paramsJson = JsonUtil.toJson(params);
			String encodedParams = URLEncoder.encode(paramsJson, StandardCharsets.UTF_8.name());
			String requestUrl = baseUrl + String.format(BUILD_SCENE_URL, encodedParams);
			tracer.info("构建scene的请求URL: " + requestUrl);

			// 3. 发送HTTP GET请求
			httpClient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(requestUrl);
			response = httpClient.execute(httpGet);

			// 4. 获取HTTP响应状态码，核心判定逻辑
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity responseEntity = response.getEntity();
			String responseContent = responseEntity != null
					? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
					: "无响应内容";
			System.out.printf("构建scene的响应 - 状态码: %d, 内容: %s%n", statusCode, responseContent);

			// 5. 按状态码分支处理
			if (statusCode == 200) {
				return responseContent;
			} else if (statusCode == 500) {
				// 5.2 状态码500：捕获错误堆栈，封装到异常中
				Throwable stackTraceThrowable = new Throwable("服务端500错误堆栈");
				// 拼接完整错误信息（状态码+响应内容+堆栈）
				String errorMsg = String.format("构建scene失败 - HTTP状态码: 500, 服务端响应: %s, 错误堆栈: %s", responseContent,
						getStackTraceAsString(stackTraceThrowable));
				throw new SceneConvertException(errorMsg, stackTraceThrowable);
			} else {
				// 5.3 其他非200/500状态码：直接抛出状态码异常
				throw new SceneConvertException(
						String.format("构建scene失败 - HTTP状态码: %d, 响应内容: %s", statusCode, responseContent), null);
			}
		} catch (SceneConvertException e) {
			// 捕获自定义异常，直接抛出（保持异常链完整）
			throw e;
		} catch (Exception e) {
			// 捕获网络/编码/解析等其他异常，封装为SceneConvertException
			throw new SceneConvertException(String.format("构建scene时发生非预期异常: %s", e.getMessage()), e);
		} finally {
			// 6. 关闭HTTP资源（避免连接泄漏）
			try {
				if (response != null)
					response.close();
				if (httpClient != null)
					httpClient.close();
			} catch (Exception e) {
				System.err.println("关闭HTTP资源时发生异常: " + e.getMessage());
			}
		}
	}

	/**
	 * 根据scene值获取原始参数（核心修改：按HTTP状态码判定结果，500时返回错误堆栈）
	 * 
	 * @param scene 场景值
	 * @return 原始参数的JSON字符串（正常场景）
	 * @throws SceneConvertException 转换过程中发生错误时抛出（含状态码异常、500堆栈信息）
	 */
	public static String getParamsOfScene(String baseUrl, String scene) throws SceneConvertException {
		Tracer tracer = TraceUtil.getCurrentTracer();
		// 1. 入参合法性校验
		if (scene == null || scene.trim().isEmpty()) {
			throw new SceneConvertException("scene值不能为空", null);
		}

		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			// 2. 构建请求URL（对scene进行URL编码，避免特殊字符问题）
			String encodedScene = URLEncoder.encode(scene, StandardCharsets.UTF_8.name());
			String requestUrl = baseUrl + String.format(GET_SCENE_URL, encodedScene);
			tracer.info("获取参数的请求URL: " + requestUrl);

			// 3. 发送HTTP GET请求
			httpClient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(requestUrl);
			response = httpClient.execute(httpGet);

			// 4. 获取HTTP响应状态码，核心判定逻辑
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity responseEntity = response.getEntity();
			String responseContent = responseEntity != null
					? EntityUtils.toString(responseEntity, StandardCharsets.UTF_8)
					: "无响应内容";
			System.out.printf("获取参数的响应 - 状态码: %d, 内容: %s%n", statusCode, responseContent);

			// 5. 按状态码分支处理
			if (statusCode == 200) {
				// 5.1 状态码200：正常解析响应（按业务约定：success=true为成功，data为原始参数）
				// 判定业务成功标识（success字段为true）
				return responseContent;
			} else if (statusCode == 500) {
				// 5.2 状态码500：捕获错误堆栈，封装到异常中
				Throwable stackTraceThrowable = new Throwable("服务端500错误堆栈");
				// 拼接完整错误信息（状态码+响应内容+堆栈）
				String errorMsg = String.format("获取参数失败 - HTTP状态码: 500, 服务端响应: %s, 错误堆栈: %s", responseContent,
						getStackTraceAsString(stackTraceThrowable));
				throw new SceneConvertException(errorMsg, stackTraceThrowable);
			} else {
				// 5.3 其他非200/500状态码：直接抛出状态码异常
				throw new SceneConvertException(
						String.format("获取参数失败 - HTTP状态码: %d, 响应内容: %s", statusCode, responseContent), null);
			}
		} catch (SceneConvertException e) {
			// 捕获自定义异常，直接抛出（保持异常链完整）
			throw e;
		} catch (Exception e) {
			// 捕获网络/编码/解析等其他异常，封装为SceneConvertException
			throw new SceneConvertException(String.format("获取参数时发生非预期异常: %s", e.getMessage()), e);
		} finally {
			// 6. 关闭HTTP资源（避免连接泄漏）
			try {
				if (response != null)
					response.close();
				if (httpClient != null)
					httpClient.close();
			} catch (Exception e) {
				System.err.println("关闭HTTP资源时发生异常: " + e.getMessage());
			}
		}
	}

	/**
	 * 工具方法：将Throwable的堆栈信息转为字符串
	 * 
	 * @param throwable 异常对象
	 * @return 堆栈信息字符串（含行号、类名）
	 */
	private static String getStackTraceAsString(Throwable throwable) {
		if (throwable == null) {
			return "无异常堆栈信息";
		}
		// 拼接堆栈信息（含异常类型、消息、每一行堆栈）
		StringBuilder stackTrace = new StringBuilder();
		stackTrace.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage()).append("\n");
		for (StackTraceElement element : throwable.getStackTrace()) {
			stackTrace.append("\tat ").append(element.toString()).append("\n");
		}
		return stackTrace.toString();
	}

	/**
	 * 获取access_token，带缓存和自动刷新功能
	 */
	private static String getAccessToken(String appId, String appSecret) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		// 先检查缓存
		String token = TOKEN_CACHE.get(appId + "_access_token");
		String expireTimeStr = TOKEN_CACHE.get(appId + "_expire_time");

		// 判断缓存是否有效
		if (token != null && expireTimeStr != null) {
			try {
				long expireTime = Long.parseLong(expireTimeStr);
				// 如果当前时间在过期时间之前，且留有提前量，则使用缓存
				if (System.currentTimeMillis() < expireTime - REFRESH_ADVANCE) {
					tracer.info("使用缓存的access_token");
					return token;
				}
			} catch (NumberFormatException e) {
				tracer.info("缓存的过期时间格式错误，将重新获取");
			}
		}

		// 缓存无效，需要重新获取，使用锁保证线程安全
		LOCK.lock();
		try {
			// 双重检查，防止多线程并发时重复请求
			token = TOKEN_CACHE.get(appId + "_access_token");
			expireTimeStr = TOKEN_CACHE.get(appId + "_expire_time");

			if (token != null && expireTimeStr != null) {
				try {
					long expireTime = Long.parseLong(expireTimeStr);
					if (System.currentTimeMillis() < expireTime - REFRESH_ADVANCE) {
						tracer.info("使用缓存的access_token");
						return token;
					}
				} catch (NumberFormatException e) {
					// 格式错误，继续执行获取新token
				}
			}

			tracer.info("缓存失效，重新获取access_token");
			return fetchNewAccessToken(appId, appSecret);
		} finally {
			LOCK.unlock();
		}
	}

	/**
	 * 从微信服务器获取新的access_token并更新缓存
	 */
	private static String fetchNewAccessToken(String appId, String appSecret) throws Exception {
	    Tracer tracer = TraceUtil.getCurrentTracer();
	    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
	        String url = String.format(ACCESS_TOKEN_URL, appId, appSecret);
	        tracer.info("获取access_token的URL: " + url);
	        HttpGet httpGet = new HttpGet(url);

	        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
	            HttpEntity entity = response.getEntity();
	            if (entity != null) {
	                String result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
	                Map<String, Object> jsonObject = JsonUtil.fromJson(result, Map.class);

	                // 检查是否获取成功
	                if (jsonObject.containsKey("access_token")) {
	                    String token = (String) jsonObject.get("access_token");
	                    // 计算过期时间（当前时间 + 有效期(秒) - 提前刷新时间）
	                    int expiresIn = Integer.parseInt(jsonObject.get("expires_in").toString());
	                    long expireTime = System.currentTimeMillis() + expiresIn * 1000;

	                    // 更新缓存
	                    TOKEN_CACHE.put(appId + "_access_token", token);
	                    TOKEN_CACHE.put(appId + "_expire_time", String.valueOf(expireTime));

	                    tracer.info("获取新的access_token成功，有效期至: " + expireTime);
	                    return token;
	                } else {
	                    // 解析错误信息
	                    String errorMsg = "获取access_token失败";
	                    if (jsonObject.containsKey("errcode") && jsonObject.containsKey("errmsg")) {
	                        int errorCode = Integer.parseInt(jsonObject.get("errcode").toString());
	                        String errorDetail = jsonObject.get("errmsg").toString();
	                        
	                        // 常见错误码解释
	                        String errorExplanation = getErrorExplanation(errorCode);
	                        
	                        errorMsg = String.format("获取access_token失败: [错误码: %d] %s。%s", 
	                                               errorCode, errorDetail, errorExplanation);
	                    } else {
	                        errorMsg += ": " + result;
	                    }
	                    
	                    tracer.error(errorMsg);
	                    throw new Exception(errorMsg);
	                }
	            }
	        }
	    }
	    return null;
	}

	/**
	 * 解析微信接口常见错误码含义
	 */
	private static String getErrorExplanation(int errorCode) {
	    switch (errorCode) {
	        case 40001: return "原因：获取 access_token 时 AppSecret 错误，或者 access_token 无效。请开发者认真比对 AppSecret 的正确性，或查看是否正在为恰当的公众号调用接口";
	        case 40002: return "原因：不合法的凭证类型";
	        case 40003: return "原因：page页面不存在，请到小程序开发者工具确认页面是否存在";
	        case 40013: return "原因：不合法的AppID，请检查AppID是否正确";
			case 40129: return "最大32个可见字符，只支持数字，大小写英文以及部分特殊字符：!#$&'()*+,/:;=?@-._~，其它字符请自行编码为合法字符（因不支持%，中文无法使用 urlencode 处理，请使用其他编码方式）";
			case 40159: return "path 不能为空，且长度不能大于 128 字节";
			case 40169: return "scene 不合法";
			case 40164: return "原因：调用接口的IP地址不在白名单中，请在微信公众平台将当前IP加入白名单";
	        case 41030: return "原因：page路径不正确：根路径前不要填加 /，不能携带参数（参数请放在scene字段里），需要保证在现网版本小程序中存在，与app.json保持一致。";
	        case 42001: return "原因：access_token已过期，请重新获取";
	        case 45009: return "原因：API调用太频繁，请降低调用频率";
	        case 45029: return "原因：生成码个数总和到达最大个数限制";
	        case 47001: return "原因：POST参数格式错误，请检查JSON格式是否正确";
	        case 50001: return "原因：没有该小程序的权限，请确认小程序已发布或已配置正确的权限";
			case 85096: return "原因：scene参数包含敏感字符";
	        default: return "请参考微信官方文档查看错误码说明";
	    }
	}
	
	/**
	 * 生成不限制的小程序码（带access_token过期重试逻辑：首次失败且为token问题时，重新获取token再试一次）
	 * 
	 * @param shortLinkBaseUrl 生成scene的短链服务器地址
	 * @param projectConfig    访问应用的配置参数
	 * @param appId            小程序AppID（用于重试时重新获取token）
	 * @param appSecret        小程序AppSecret（用于重试时重新获取token）
	 * @return 小程序码字节数组（null表示生成失败，含重试后仍失败）
	 * @throws Exception 网络请求异常、参数校验异常等（非token类异常直接抛出）
	 */
	public static byte[] generateUnlimitedCodeByProjectConfig(String shortLinkBaseUrl,
			Map<String, Object> projectConfig, String appId, String appSecret, String envVersion) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		// 生成不限制的小程序码
		String page = "pages/index/index"; // 小程序页面路径
		int width = 430; // 小程序码宽度
		boolean autoColor = false; // 是否自动配置线条颜色
		String scene = getSenceOfParams(shortLinkBaseUrl, projectConfig);
		tracer.info("生成的scene值: " + scene);
		return generateUnlimitedCodeWithTokenRetry(scene, page, width, autoColor, appId, appSecret, envVersion);
	}

	/**
	 * 生成不限制的小程序码（带access_token过期重试逻辑：首次失败且为token问题时，重新获取token再试一次）
	 * 
	 * @param scene     场景值（长度≤32，仅允许数字、字母、_ - = &）
	 * @param page      小程序页面路径（如pages/index/index，根路径无需/开头）
	 * @param width     小程序码宽度（建议280-1280，默认430）
	 * @param autoColor 是否自动配置线条颜色（true：跟随主题色；false：使用自定义line_color）
	 * @param appId     小程序AppID（用于重试时重新获取token）
	 * @param appSecret 小程序AppSecret（用于重试时重新获取token）
	 * @return 小程序码字节数组（null表示生成失败，含重试后仍失败）
	 * @throws Exception 网络请求异常、参数校验异常等（非token类异常直接抛出）
	 */
	public static byte[] generateUnlimitedCodeWithTokenRetry(String scene, String page, int width, boolean autoColor,
			String appId, String appSecret, String envVersion) throws Exception {
		Tracer tracer = TraceUtil.getCurrentTracer();
		// 1. 先执行首次小程序码生成
		try {
			String accessToken = getAccessToken(appId, appSecret);
			return generateUnlimitedCode(accessToken, scene, page, width, autoColor, envVersion);
		} catch (TokenInvalidException e) {
			tracer.info("首次生成小程序码失败：" + e.getMessage() + "，将尝试重新获取token后重试1次");

			// 2. token无效/过期，重新获取新token（内部含缓存更新逻辑）
			String newAccessToken = getAccessToken(appId, appSecret);
			if (newAccessToken == null || newAccessToken.trim().isEmpty()) {
				tracer.info("重试失败：重新获取access_token返回空值");
				return null;
			}
			tracer.info("重试前获取到新的access_token：" + newAccessToken);

			// 3. 使用新token执行第二次小程序码生成（仅重试1次，避免无限循环）
			try {
				return generateUnlimitedCode(newAccessToken, scene, page, width, autoColor, envVersion);
			} catch (TokenInvalidException retryE) {
				tracer.info("重试生成小程序码仍失败：" + retryE.getMessage() + "（已达到最大重试次数1次）");
				// 重试后仍token无效，抛出异常提示上游处理
				throw new TokenInvalidException("连续2次获取token后生成小程序码失败：" + retryE.getMessage());
			}
		}
	}

	/**
	 * 生成不限制的小程序码
	 * 
	 * @param accessToken 访问令牌
	 * @param scene       场景值
	 * @param page        小程序页面路径
	 * @param width       小程序码宽度
	 * @param autoColor   是否自动配置线条颜色
	 * @return 小程序码的字节数组
	 * @throws TokenInvalidException 当token无效或过期时抛出
	 */
	private static byte[] generateUnlimitedCode(String accessToken, String scene, String page, int width,
	        boolean autoColor, String envVersion) throws Exception {
	    Tracer tracer = TraceUtil.getCurrentTracer();
	    // 校验scene参数
	    if (scene == null || scene.length() > 32) {
	        String errorMsg = "scene参数长度不能超过32个字符，当前长度: " + (scene == null ? 0 : scene.length());
	        tracer.error(errorMsg);
	        throw new IllegalArgumentException(errorMsg);
	    }
	    String regex = "^[a-zA-Z0-9_\\-=&]+$";
	    if (!scene.matches(regex)) {
	        String errorMsg = "scene参数包含非法字符，仅允许数字、字母、_ - = &，当前值: " + scene;
	        tracer.error(errorMsg);
	        throw new IllegalArgumentException(errorMsg);
	    }

	    // 校验width参数
	    if (width <= 0 || width > 430) {
	        String errorMsg = "小程序码宽度必须在1-430之间，当前值: " + width;
	        tracer.error(errorMsg);
	        throw new IllegalArgumentException(errorMsg);
	    }

	    // 校验page参数
	    if (page != null && (page.length() > 128 || page.startsWith("/"))) {
	        String errorMsg = "小程序页面路径格式错误，根路径前不要填加 /，且长度不超过128，当前值: " + page;
	        tracer.error(errorMsg);
	        throw new IllegalArgumentException(errorMsg);
	    }

	    // 校验环境版本参数
	    if (envVersion != null && !Arrays.asList("release", "trial", "develop").contains(envVersion)) {
	        String errorMsg = "环境版本参数错误，必须是release、trial或develop，当前值: " + envVersion;
	        tracer.error(errorMsg);
	        throw new IllegalArgumentException(errorMsg);
	    }

	    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
	        String url = String.format(UNLIMITED_CODE_URL, accessToken);
	        HttpPost httpPost = new HttpPost(url);
	        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");

	        Map<String, Object> params = new LinkedHashMap<>();
	        params.put("scene", scene);
	        params.put("page", page);
	        params.put("width", width);
	        params.put("auto_color", autoColor);
	        params.put("env_version", envVersion); // 指定环境版本，体验版需设置为"trial"

	        if (!autoColor) {
	            Map<String, Integer> lineColor = new LinkedHashMap<>();
	            lineColor.put("r", 0);
	            lineColor.put("g", 0);
	            lineColor.put("b", 0);
	            params.put("line_color", lineColor);
	        }

	        params.put("is_hyaline", false);

	        StringEntity entity = new StringEntity(JsonUtil.toJson(params), StandardCharsets.UTF_8);
	        httpPost.setEntity(entity);

	        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
	            int statusCode = response.getStatusLine().getStatusCode();
	            if (statusCode != HttpStatus.OK.value()) {
	                String errorMsg = "生成小程序码请求失败，HTTP状态码: " + statusCode;
	                tracer.error(errorMsg);
	                throw new IOException(errorMsg);
	            }

	            HttpEntity responseEntity = response.getEntity();
	            if (responseEntity != null) {
	                String contentType = responseEntity.getContentType().getValue();
	                if (contentType.contains("application/json")) {
	                    String errorResult = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
	                    Map<String, Object> errorJson = JsonUtil.fromJson(errorResult, Map.class);
	                    
	                    // 解析错误信息
	                    String errorMsg = parseWxCodeError(errorJson);
	                    tracer.error(errorMsg);
	                    
	                    // 处理token无效情况
	                    if (errorJson.containsKey("errcode")) {
	                        int errorCode = Integer.parseInt(errorJson.get("errcode").toString());
	                        if (errorCode == 40001 || errorCode == 42001) {
	                            TOKEN_CACHE.clear();
	                            throw new TokenInvalidException("access_token无效或过期: " + errorMsg);
	                        }
	                    }
	                    
	                    throw new Exception(errorMsg);
	                }

	                // 将响应流转换为字节数组
	                try (InputStream in = responseEntity.getContent();
	                        ByteArrayOutputStream out = new ByteArrayOutputStream()) {

	                    byte[] buffer = new byte[1024];
	                    int len;
	                    while ((len = in.read(buffer)) != -1) {
	                        out.write(buffer, 0, len);
	                    }
	                    return out.toByteArray();
	                }
	            } else {
	                String errorMsg = "生成小程序码响应内容为空";
	                tracer.error(errorMsg);
	                throw new Exception(errorMsg);
	            }
	        }
	    }
	}

	/**
	 * 解析微信小程序码接口错误信息
	 */
	private static String parseWxCodeError(Map<String, Object> errorJson) {
	    if (errorJson == null) {
	        return "微信接口返回未知错误";
	    }
	    
	    StringBuilder errorMsg = new StringBuilder("生成小程序码失败");
	    
	    if (errorJson.containsKey("errcode") && errorJson.containsKey("errmsg")) {
	        try {
	            int errorCode = Integer.parseInt(errorJson.get("errcode").toString());
	            String errorDetail = errorJson.get("errmsg").toString();
	            
	            // 添加错误码和错误详情
	            errorMsg.append(String.format(": [错误码: %d] %s", errorCode, errorDetail));
	            
	            // 添加错误解释
	            errorMsg.append("。").append(getErrorExplanation(errorCode));
	        } catch (Exception e) {
	            errorMsg.append(": ").append(errorJson.toString());
	        }
	    } else {
	        errorMsg.append(": ").append(errorJson.toString());
	    }
	    
	    return errorMsg.toString();
	}


}
