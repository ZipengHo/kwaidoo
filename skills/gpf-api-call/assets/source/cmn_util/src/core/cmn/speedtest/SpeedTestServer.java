package cmn.speedtest;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * 网络测试服务端
 * @author chenxb
 *
 */
public class SpeedTestServer {
	public static void main(String[] args) throws Exception {
		System.out.println("SpeedTestServer : cmn.speedtest.SpeedTestServer [port]");
		int port = 6666;
		if(args.length > 0)
			port = Integer.valueOf(args[0]);
		System.out.println("SpeedTestServer port : " + port);
		ServerSocket server = new ServerSocket(port);
		Socket socket = server.accept();
		OutputStream output = socket.getOutputStream();

		byte[] bytes = new byte[10 * 1024]; // 10K
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = 12;
		}

		while (true) {
			output.write(bytes);
		}
	}
}