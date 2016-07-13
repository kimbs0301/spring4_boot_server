package com.example.spring.server;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gimbyeongsu
 * 
 */
public final class ReadThread implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadThread.class);

	private int I = - 1;
	private final ConcurrentLinkedQueue<SocketChannel> accept = new ConcurrentLinkedQueue<SocketChannel>();
	private final int threadNumber;
	private final WorkThread[] workThread;
	private Selector s;
	private final AtomicInteger handlerID;
	private SessionChannelManager sessionChannelManager;
	private int workThreadSize;
	private ExecutorService workThreadExecutorService;

	public ReadThread(AtomicInteger handlerID, SessionChannelManager sessionChannelManager, String name, int number) {
		this.handlerID = handlerID;
		this.threadNumber = number;
		this.sessionChannelManager = sessionChannelManager;
		this.workThreadSize = 10;
		this.workThread = new WorkThread[workThreadSize];
		this.workThreadExecutorService = Executors.newFixedThreadPool(workThread.length, new ThreadFactoryImpl("("
				+ number + ")" + WorkThread.WORK_THREAD_NAME, false, Thread.NORM_PRIORITY));
		for (int i = 0, size = workThread.length; i < size; ++i) {
			this.workThread[i] = new WorkThread();
		}
	}

	public void setAccept(SocketChannel a) {
		accept.add(a);
		s.wakeup();
	}

	private void register(Selector s) {
		SocketChannel sc = accept.poll();
		if (sc != null) {
			final int idx = I = ( ++I ) % workThreadSize;
			final int handlerID = this.handlerID.getAndIncrement();
			try {
				final ReadHandler att = new ReadHandlerImpl(workThreadExecutorService , workThread[ idx ] , sessionChannelManager , sc);
				sc.register(s, SelectionKey.OP_READ, att);
				att.connect(threadNumber, handlerID);
			} catch (Exception e) {
				LOGGER.error("", e);
				if (sc != null) {
					try {
						sc.close();
					} catch (IOException ie) {
						LOGGER.error("", ie);
					}
				}
			}
		}
	}

	@Override
	public void run() {
		try {
			s = Selector.open();
		} catch (IOException e) {
			LOGGER.error("", e);
			System.exit(0);
		}

		while (true) {
			try {
				if (s.select(10L) != 0) {
					if (s.selectedKeys().size() > 10) {
						System.out.println(s.selectedKeys().size());
					}
					for (Iterator<SelectionKey> it = s.selectedKeys().iterator(); it.hasNext(); it.remove()) {
						try {
							final SelectionKey sk = it.next();
							if (sk.isValid()) {
								final ReadHandler r = (ReadHandler) sk.attachment();
								if (r != null) {
									r.received(sk);
								}
							}
						} catch (Exception e) {
							LOGGER.error("", e);
						}
					}
				}
			} catch (ClosedSelectorException e) {
				LOGGER.info("ClosedSelectorException");
				break;
			} catch (Exception e) {
				LOGGER.error("", e);
			}
			if (accept.isEmpty() == false) {
				register(s);
			}
		}
	}

	public void shutdown() {
		LOGGER.debug("");
		try {
			s.close();
		} catch (IOException e) {
			LOGGER.error("", e);
		}
		workThreadExecutorService.shutdown();
	}
}