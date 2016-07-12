package com.example.spring.config;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.spring.server.AcceptThread;
import com.example.spring.server.BootConfigFactory;
import com.example.spring.server.ReadThread;
import com.example.spring.server.ReadThreadPool;
import com.example.spring.server.ThreadFactoryImpl;

/**
 * @author gimbyeongsu
 * 
 */
@Configuration
public class ServerConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfig.class);

	private final AtomicInteger handlerID = new AtomicInteger(1);

	public ServerConfig() {
		LOGGER.debug("생성자 ServerConfig()");
	}

	@Bean
	public BootConfigFactory bootConfigFactory() {
		BootConfigFactory bootConfigFactory = new BootConfigFactory();
		return bootConfigFactory;
	}

	@Bean
	public ReadThread[] readThreads() {
		BootConfigFactory bootConfigFactory = bootConfigFactory();
		int size = bootConfigFactory.getReadThreadSize();
		String name = bootConfigFactory.getReadThreadName();
		ReadThread[] poolRead = new ReadThread[size];
		for (int i = 0; i < size; ++i) {
			poolRead[i] = new ReadThread(handlerID, name + i, i);
		}
		return poolRead;
	}

	@Bean
	public ReadThreadPool readThreadPool() {
		ReadThread[] readThreads = readThreads();
		ReadThreadPool readThreadPool = new ReadThreadPool(bootConfigFactory(), readThreads);
		return readThreadPool;
	}

	@Bean
	public AcceptThread acceptThread() throws Exception {
		BootConfigFactory bootConfigFactory = bootConfigFactory();
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.setOption(StandardSocketOptions.SO_REUSEADDR, false);
		ssc.setOption(StandardSocketOptions.SO_RCVBUF, 0);
		ssc.bind(new InetSocketAddress(bootConfigFactory.getIp(), bootConfigFactory.getPort()), 100);

		ExecutorService acceptThreadExecutorService = Executors.newSingleThreadExecutor(new ThreadFactoryImpl(bootConfigFactory.getAcceptThreadName(), false,
				bootConfigFactory.getAcceptThreadPriority()));

		AcceptThread acceptThread = new AcceptThread(ssc, bootConfigFactory, readThreadPool());
		acceptThreadExecutorService.execute(acceptThread);
		return acceptThread;
	}
}
