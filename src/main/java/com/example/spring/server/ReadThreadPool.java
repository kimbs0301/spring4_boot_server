package com.example.spring.server;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gimbyeongsu
 * 
 */
public final class ReadThreadPool {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadThreadPool.class);
	private BootConfigFactory bootConfigFactory;
	private final ReadThread[] readRead;
	private ExecutorService poolReadExecutorService;
	private int size = 0;

	public ReadThreadPool(BootConfigFactory bootConfigFactory, ReadThread[] readRead) {
		this.size = bootConfigFactory.getReadThreadSize();
		this.readRead = readRead;
		this.bootConfigFactory = bootConfigFactory;
	}

	public void startPool() {
		String name = bootConfigFactory.getReadThreadName();
		int priority = bootConfigFactory.getReadThreadPriority();
		poolReadExecutorService = Executors.newFixedThreadPool(size, new ThreadFactoryImpl(name, false, priority));
		for (int i = 0; i < size; ++i) {
			poolReadExecutorService.execute(readRead[i]);
		}
	}

	public void accept(int readNum, SocketChannel sc) {
		ReadThread r = readRead[readNum];
		r.setAccept(sc);
	}
	
	public void shutdown() {
		LOGGER.debug("");
		poolReadExecutorService.shutdown();
		for(ReadThread each : readRead) {
			each.shutdown();
		}
	}
}