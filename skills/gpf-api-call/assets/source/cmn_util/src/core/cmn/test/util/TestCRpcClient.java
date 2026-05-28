package cmn.test.util;

import java.net.URI;

import com.kwaidoo.ms.tool.CmnUtil;
import com.leavay.common.nio.ws.CWsUtil;
import com.leavay.nio.crpc.CRpcAdapter;
import com.leavay.nio.crpc.CRpcClient;
import com.leavay.nio.crpc.CRpcClientFactory;

import cell.cmn.ICmnService;

public class TestCRpcClient {

	public static void main(String[] args) throws Exception
    {
        try
        {
        	String wsUrl = "wss://saas-wxmp.jungong56.com:443/wxmp_sj_ws";
            int loop = 10000;
            
            if (args != null && args.length >= 1)
            {
            	wsUrl = args[0].trim();
            }

            if (args != null && args.length >= 2)
                loop = CmnUtil.getInteger(args[2]);
            

            CmnUtil.out("Parameters : [wsUrl] [loop times]");
            CmnUtil.out("Example 1 : ws://192.168.0.1:8088 10000");
            CmnUtil.out("Example 2 : wss://kwaidoo.com/digitalOM_ws:443 10000");
            
            CmnUtil.out("Try to connect : "+ wsUrl+"[loop="+loop+"]");
            URI uri = CWsUtil.buildURI(wsUrl);
            CRpcClient cli = CRpcClientFactory.getInstance().connectClient(uri, 5000);
            CRpcAdapter adp = new CRpcAdapter();

            ICmnService intf = adp.openService(cli, ICmnService.class);
            Double[] numbers = new Double[loop];
            long time = System.currentTimeMillis();
            long errorCnt = 0;
            for (int i=0;i<loop;i++)
            {
            	long start = System.currentTimeMillis();
            	try {
            		intf.ping();
            		long cost = System.currentTimeMillis() - start;
            		numbers[i] = (double) cost;
            		CmnUtil.out("["+(i+1)+"]ping cost : "+cost+"<ms>");
            	}catch (Exception e) {
            		e.printStackTrace();
            		errorCnt++;
            		long cost = System.currentTimeMillis() - start;
            		numbers[i] = (double) cost;
    				CmnUtil.out("["+(i+1)+"]ping failed : "+cost+"<ms>");
				}
            }
            CmnUtil.out("Speed TPS="+loop*1000/(System.currentTimeMillis()-time));
            double[] result = calculateMaxMinAverage(numbers);
            CmnUtil.out("success： "+(loop-errorCnt)+"，failed:" + errorCnt, true);
            CmnUtil.out("min = "+result[1]+"ms，max = "+result[0]+"ms，avg = "+result[2]+"ms", true);
        } catch(Throwable err)
        {
            err.printStackTrace();
        }
        finally
        {
            System.exit(0);
        }

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
			if(number == null)
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
		return new double[]{max, min, average};
	}
}
