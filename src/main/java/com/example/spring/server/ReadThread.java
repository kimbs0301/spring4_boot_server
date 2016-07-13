package com.example.spring.server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gimbyeongsu
 * 
 */
public final class ReadThread implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadThread.class);

	private final ConcurrentLinkedQueue<SocketChannel> accept = new ConcurrentLinkedQueue<SocketChannel>();
	private final int threadNumber;
	private Selector s;
	private final AtomicInteger handlerID;

	public ReadThread(AtomicInteger handlerID, String name, int number) {
		this.handlerID = handlerID;
		this.threadNumber = number;
	}

	public void wakeup() {
		s.wakeup();
	}

	public void setAccept(SocketChannel a) {
		accept.add(a);
	}

	private void register(Selector s) {
		SocketChannel sc = accept.poll();
		if (sc != null) {
			final int handlerID = this.handlerID.getAndIncrement();
			try {
				final WorkHandler att = new WorkHandlerImpl(sc);
				sc.register(s, SelectionKey.OP_READ, att);
				att.connect(threadNumber, handlerID);
			} catch (Exception e) {
				e.printStackTrace();
				if (sc != null) {
					try {
						sc.close();
					} catch (IOException ie) {
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
			e.printStackTrace();
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
								final WorkHandler r = (WorkHandler) sk.attachment();
								if (r != null) {
									r.received(sk);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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
	}
}