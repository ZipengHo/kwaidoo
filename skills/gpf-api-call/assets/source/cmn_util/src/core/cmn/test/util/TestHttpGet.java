package cmn.test.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.kwaidoo.ms.tool.CmnUtil;

import web.dto.Pair;

public class TestHttpGet {

	private static final String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1";
	private static final int TIMEOUT = 30000; // 30 秒超时

	/**
	 * 发送 GET 请求
	 * 
	 * @param urlStr 目标 URL
	 * @return 响应内容（字符串）
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static Pair<String, Long> sendGet(String urlStr, boolean keepAlive, boolean parseResponse) throws Exception {
		HttpURLConnection conn = null;
		try {
			long start = System.currentTimeMillis();
			conn = (HttpURLConnection) new URL(urlStr).openConnection();
			// 配置请求
			conn.setRequestMethod("GET");
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			if (keepAlive)
				conn.setRequestProperty("Connection", "keep-alive"); // 显式保持连接

			// 检查响应状态
			int status = conn.getResponseCode();
			long cost = System.currentTimeMillis() - start;
			if (status != HttpURLConnection.HTTP_OK) {
				System.err.println("请求失败，状态码：" + status);
				return null;
			}
			String str = null;
			if (parseResponse) {
				// 读取响应内容
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					str = response.toString();
					System.out.println(str);
				}
			}
			return new Pair<>(str, cost);
		} finally {
			if (conn != null)
				conn.disconnect();
		}
	}

	/**
	 * 带查询参数的 GET 请求示例
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		HttpURLConnection conn = null;
		try {
			String httpUrl = "https://saas-wxmp.jungong56.com/sj/version.json";
			int loop = 100;
			boolean keepAlive = true;
			boolean parseResponse = false;
			if (args != null && args.length >= 1) {
				httpUrl = args[0].trim();
			}
			if (args != null && args.length >= 2)
				loop = CmnUtil.getInteger(args[1]);
			if (args != null && args.length >= 3)
				parseResponse = CmnUtil.getBoolean(args[2]);

			CmnUtil.out("Parameters : [url] [loop times] [keep alive]");
			CmnUtil.out("Example 1 : http://192.168.1.128:8090 10000 true");
			CmnUtil.out("Example 2 : https://saas-wxmp.jungong56.com/sj/ 10000 true");

			CmnUtil.out("url = " + httpUrl);
			CmnUtil.out("loop = " + loop);
			CmnUtil.out("Connection : keepAlive = " + keepAlive);
			Double[] numbers = new Double[loop];
			long errorCnt = 0;
			for (int i = 0; i < loop; i++) {
				long start = System.currentTimeMillis();
				try {
					Pair<String, Long> response = null;
					if (keepAlive) {
						conn = (HttpURLConnection) new URL(httpUrl).openConnection();
						configureConnection(conn); // 通用配置
						response = executeRequest(conn, parseResponse);
					} else {
						response = sendGet(httpUrl, keepAlive, parseResponse);
					}
					long cost = response.right;
					numbers[i] = (double) response.right;
					CmnUtil.out("[" + (i + 1) + "]get cost : " + cost + "<ms>");
				} catch (Exception e) {
					e.printStackTrace();
					errorCnt++;
					long cost = System.currentTimeMillis() - start;
					numbers[i] = (double) cost;
					CmnUtil.out("[" + (i + 1) + "]get failed : " + cost + "<ms>");
				}

			}
			double[] result = calculateMaxMinAverage(numbers);
			CmnUtil.out("success： " + (loop - errorCnt) + "，failed:" + errorCnt, true);
			CmnUtil.out("min = " + result[1] + "ms，max = " + result[0] + "ms，avg = " + result[2] + "ms", true);
		} finally {

			if (conn != null)
				conn.disconnect();
		}
	}

	private static void configureConnection(HttpURLConnection conn) throws ProtocolException {
		conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent", USER_AGENT);
		conn.setConnectTimeout(TIMEOUT);
		conn.setReadTimeout(TIMEOUT);
		conn.setRequestProperty("Connection", "keep-alive"); // 显式保持连接
	}

	private static Pair<String, Long> executeRequest(HttpURLConnection conn, boolean parseResponse) throws Exception {
		long start = System.currentTimeMillis();
		int status = conn.getResponseCode();
		if (status != HttpURLConnection.HTTP_OK) {
			throw new Exception("状态码错误：" + status);
		}
		// 必须读完流，否则连接无法复用
		String str = null;
		if (parseResponse) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				str = reader.lines().reduce("", String::concat);
			}
			System.out.println(str);
		}
		long cost = System.currentTimeMillis() - start;
		return new Pair<>(str, cost);
	}

	public static double[] calculateMaxMinAverage(Double[] numbers) {
		if (numbers == null || numbers.length == 0) {
			throw new IllegalArgumentException("数组不能为空");
		}

		// 初始化最大值和最小值为数组的第一个元素
		double max = numbers[0];
		double min = numbers[0];
		double sum = 0;

		// 遍历数组
		for (Double number : numbers) {
			if (number == null)
				continue;
			// 更新最大值
			if (number > max) {
				max = number;
			}
			// 更新最小值
			if (number < min) {
				min = number;
			}
			// 累加求和
			sum += number;
		}

		// 计算平均值
		double average = sum / numbers.length;

		// 返回结果
		return new double[] { max, min, average };
	}
}
