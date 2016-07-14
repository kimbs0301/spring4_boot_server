package com.example.spring.config;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.example.spring.server.AcceptThread;
import com.example.spring.server.BootConfigFactory;
import com.example.spring.server.ReadThread;
import com.example.spring.server.ReadThreadPool;
import com.example.spring.server.SessionChannelManager;
import com.example.spring.server.ThreadFactoryImpl;
import com.example.spring.server.WorkThread;

/**
 * @author gimbyeongsu
 * 
 */
@Configuration
public class ServerConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfig.class);

	@Autowired
	private Environment environment;
	private AtomicInteger handlerID = new AtomicInteger(1);
	private ExecutorService acceptThreadExecutor;
	private ServerSocketChannel ssc;

	public ServerConfig() {
		LOGGER.debug("생성자 ServerConfig()");
	}

	@Bean
	public BootConfigFactory bootConfigFactory() {
		BootConfigFactory config = new BootConfigFactory();
		config.setReadThreadSize(3);
		return config;
	}

	@Bean
	public SessionChannelManager sessionChannelManager() {
		return new SessionChannelManager(bootConfigFactory().getReadThreadSize());
	}

	@Bean
	public ReadThread[] readThreads() {
		BootConfigFactory config = bootConfigFactory();
		int readThreadSize = config.getReadThreadSize();
		int workThreadSize = config.getWorkThreadSize();
		SessionChannelManager sessionChannelManager = sessionChannelManager();

		ReadThread[] poolRead = new ReadThread[readThreadSize];
		for (int i = 0; i < readThreadSize; ++i) {
			WorkThread[] workThread = new WorkThread[workThreadSize];
			for (int j = 0, size = workThread.length; j < size; ++j) {
				workThread[j] = new WorkThread(config);
			}
			poolRead[i] = new ReadThread(config, workThread, handlerID, sessionChannelManager, i);
		}
		return poolRead;
	}

	@Bean(destroyMethod = "shutdown")
	public ReadThreadPool readThreadPool() {
		ReadThread[] readThreads = readThreads();
		ReadThreadPool readThreadPool = new ReadThreadPool(bootConfigFactory(), readThreads);
		return readThreadPool;
	}

	@Bean(destroyMethod = "shutdown")
	public AcceptThread acceptThread() throws Exception {
		String ip = environment.getRequiredProperty("server.ip");
		int port = environment.getRequiredProperty("server.port", Integer.class);
		int backlog = environment.getRequiredProperty("server.backlog", Integer.class);
		BootConfigFactory config = bootConfigFactory();

		ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ssc.setOption(StandardSocketOptions.SO_REUSEADDR, false);
		ssc.setOption(StandardSocketOptions.SO_RCVBUF, 0);
		ssc.bind(new InetSocketAddress(ip, port), backlog);

		AcceptThread acceptThread = new AcceptThread(ssc, readThreadPool());
		acceptThreadExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryImpl(config.getAcceptThreadName(),
				false, config.getAcceptThreadPriority()));
		acceptThreadExecutor.execute(acceptThread);
		return acceptThread;
	}

	@PreDestroy
	public void shutdown() {
		LOGGER.debug("");
		acceptThreadExecutor.shutdown();
	}
}
