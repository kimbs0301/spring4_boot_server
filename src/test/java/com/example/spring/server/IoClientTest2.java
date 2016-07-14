package com.example.spring.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gimbyeongsu
 * 
 */
public class IoClientTest2 {
	private static final Logger LOGGER = LoggerFactory.getLogger(IoClientTest.class);

	@Ignore
	@Test
	public void test() throws Exception {
		byte[] packet = { 65, 65, 65, 65, 65, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68, 65, 65, 65, 65, 65, 66, 66, 66, 66, 66, 67, 67, 67,
				67, 67, 68, 68, 68, 68, 68, 65, 65, 65, 65, 65, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68, 65, 65, 65, 65, 65, 66, 66, 66, 66,
				66, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68, 65, 65, 65, 65, 65, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68, 65, 65, 65, 65, 65,
				66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68, 65, 65, 65, 65, 65, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68, 65,
				65, 65, 65, 65, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68, 65, 65, 65, 65, 65, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 68, 68,
				68, 68, 68, 65, 65, 65, 65, 65, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68 };
		for (int x = 0; x < 10000; ++x) {
			InetAddress addr = InetAddress.getByName("127.0.0.1");
			InetSocketAddress isa = new InetSocketAddress(addr, 8080);
			long start = System.currentTimeMillis();
			for (int i = 0; i < 10; ++i) {
				Socket socket = new Socket();
				socket.setReuseAddress(false);
				socket.setSoTimeout(5000);
				socket.setKeepAlive(false);
				socket.setSoLinger(false, 0);
				socket.setTcpNoDelay(false);
				socket.connect(isa, 3000);

				OutputStream os = socket.getOutputStream();
				InputStream is = socket.getInputStream();

				ByteBuffer bb = ByteBuffer.allocate(5 + 8 + packet.length);
				bb.put((byte) 0x07);
				bb.putInt(packet.length + 8);
				bb.putLong(System.currentTimeMillis());
				bb.put(packet);

				byte[] arr = bb.array();
				os.write(arr);
				os.flush();

				byte[] msgId = new byte[1];
				byte[] length = new byte[4];
				byte[] time = new byte[8];
				byte[] body = new byte[packet.length];
				is.read(msgId);
				is.read(length);
				is.read(time);
				is.read(body);

				LOGGER.info("{}", new String(body));
				LOGGER.info("{} {}", body.length, i);

				os.close();
				is.close();
				socket.close();
			}
			long end = System.currentTimeMillis() - start;
			LOGGER.info("{}", end);
		}
	}
}
