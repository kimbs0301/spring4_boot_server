package com.example.spring.server;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author gimbyeongsu
 * 
 */
public final class ReadThreadPool {
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

	public void stopPool() {
		poolReadExecutorService.shutdown();
	}

	public void accept(int readNum, SocketChannel a) {
		ReadThread r = readRead[readNum];
		r.setAccept(a);
		r.wakeup();
	}
}