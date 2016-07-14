package com.example.spring.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.junit.JunitSpringAnnotation;

/**
 * @author gimbyeongsu
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@JunitSpringAnnotation
public class IoClientTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(IoClientTest.class);

	@Test
	public void test() throws Exception {
		byte[] packet = { 65, 65, 65, 65, 65, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68, 65, 65, 65,
				65, 65, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68, 65, 65, 65, 65, 65, 66, 66, 66, 66,
				66, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68, 65, 65, 65, 65, 65, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67,
				68, 68, 68, 68, 68, 65, 65, 65, 65, 65, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68, 65,
				65, 65, 65, 65, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68, 65, 65, 65, 65, 65, 66, 66,
				66, 66, 66, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68, 65, 65, 65, 65, 65, 66, 66, 66, 66, 66, 67, 67, 67,
				67, 67, 68, 68, 68, 68, 68, 65, 65, 65, 65, 65, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 68, 68, 68, 68,
				68, 65, 65, 65, 65, 65, 66, 66, 66, 66, 66, 67, 67, 67, 67, 67, 68, 68, 68, 68, 68 };

		int size = 6;
		ExecutorService executor = Executors.newFixedThreadPool(size + 1, new ThreadFactoryImpl("TEST", false, 5));
		List<Future<Void>> futureList = new ArrayList<>();
		for (int i = 0; i < size; ++i) {
			Callable<Void> task = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					long start = System.currentTimeMillis();
					try {
						for (int i = 0; i < 1000; ++i) {
							InetAddress addr = InetAddress.getByName("127.0.0.1");
							InetSocketAddress isa = new InetSocketAddress(addr, 8080);
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
					} catch (Exception e) {
						LOGGER.error("", e);
					}
					long end = System.currentTimeMillis() - start;
					LOGGER.info("{}", end);
					return null;
				}
			};
			Future<Void> future = executor.submit(task);
			futureList.add(future);
			Thread.sleep(1L);
		}

		for (Future<Void> each : futureList) {
			each.get();
		}
	}
}
