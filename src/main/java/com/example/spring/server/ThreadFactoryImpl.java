package com.example.spring.server;

import java.text.MessageFormat;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gimbyeongsu
 * 
 */
public final class ThreadFactoryImpl implements ThreadFactory {
	private final AtomicInteger number = new AtomicInteger(1);
	private String name;
	private boolean daemon;
	private int priority;

	public ThreadFactoryImpl(String name, boolean daemon, int priority) {
		this.name = name;
		this.daemon = daemon;
		this.priority = priority;
	}

	@Override
	public Thread newThread(final Runnable runnable) {
		MessageFormat messageFormat = new MessageFormat("{0}-{1}");
		Thread t = new Thread(runnable, messageFormat.format(new Object[] {
				name, String.format("%1$03d", number.getAndIncrement()) }));
		t.setDaemon(daemon);
		t.setPriority(priority);
		return t;
	}
}