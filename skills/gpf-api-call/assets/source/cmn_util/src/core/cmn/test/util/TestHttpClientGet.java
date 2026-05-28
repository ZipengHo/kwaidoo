package cmn.test.util;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import com.kwaidoo.ms.tool.CmnUtil;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

public class TestHttpClientGet {

	private static final String USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1";
	private static final int TIMEOUT = 30000; // 30 秒超时
	/**
	 * 带查询参数的 GET 请求示例
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		HttpURLConnection conn = null;
		try {
			String httpUrl = "https://saas-wxmp.jungong56.com/sj/version.json";
			int loop = 1000;
			boolean printResponse = false;
			if (args != null && args.length >= 1) {
				httpUrl = args[0].trim();
			}
			if (args != null && args.length >= 2)
				loop = CmnUtil.getInteger(args[1]);

			CmnUtil.out("Parameters : [url] [loop times] [keep alive]");
			CmnUtil.out("Example 1 : http://192.168.1.128:8090 10000 true");
			CmnUtil.out("Example 2 : https://saas-wxmp.jungong56.com/sj/ 10000 true");

			CmnUtil.out("url = " + httpUrl);
			CmnUtil.out("loop = " + loop);
			Double[] numbers = new Double[loop];
			long errorCnt = 0;
			HttpRequest request = getRequest(httpUrl);
			for (int i = 0; i < loop; i++) {
				long start = System.currentTimeMillis();
				try {
					String response = request.execute().body();
					if(printResponse)
						System.out.println(response);
					long cost = System.currentTimeMillis()-start;
					numbers[i] = (double) cost;
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
	
	private static HttpRequest getRequest(String url) {
		return HttpRequest.get(url)
				.header("User-Agent", USER_AGENT)
				.header("Connection", "keep-alive")
				.setConnectionTimeout(TIMEOUT)
				.setReadTimeout(TIMEOUT)
				;
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
