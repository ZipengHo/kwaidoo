package cmn.speedtest;

import java.io.InputStream;
import java.net.Socket;

import com.kwaidoo.ms.tool.ToolUtilities;
/**
 * 网络测试客户端
 * @author chenxb
 *
 */
public class SpeedTestClient {

	public static void main(String[] args) throws Exception {
		if(args.length == 0) {
			System.out.println("Test Download : cmn.speedtest.SpeedTestClient [ip] [port]");
			return ;
		}
		String ip = args[0];
		int port = 6666;
		if(args.length > 1) {
			port = Integer.valueOf(args[1]);
		}
		System.out.println("Connect Server : " + ip + "[" + port+"]");
		Socket socket = new Socket(ip, port);
		InputStream input = socket.getInputStream();
		long total = 0;
		long start = System.currentTimeMillis();

		byte[] bytes = new byte[10240]; // 10K
		while (true) {
			int read = input.read(bytes);
			total += read;
			long cost = System.currentTimeMillis() - start;
			if (cost > 0 && System.currentTimeMillis() % 10 == 0) {
				String totalSize = ToolUtilities.memSize2String(total);
				String size = ToolUtilities.memSize2String(total / cost);
				System.out.println("Read " + totalSize + ", speed: " + size + "/s");
			}
		}
	}

}