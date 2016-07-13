package com.example.spring.server;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gimbyeongsu
 * 
 */
public final class AcceptThread implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(AcceptThread.class);

	private ReadThreadPool readThreadPool;
	private final int readPoolSize;
	private final ServerSocketChannel ssc;
	private boolean isRun = true;

	public AcceptThread(ServerSocketChannel ssc, BootConfigFactory bootConfigFactory, ReadThreadPool readThreadPool) {
		this.ssc = ssc;
		this.readPoolSize = bootConfigFactory.getReadThreadSize();
		this.readThreadPool = readThreadPool;
	}

	@Override
	public void run() {
		Selector s = null;
		try {
			s = Selector.open();
			ssc.register(s, SelectionKey.OP_ACCEPT);
		} catch (NullPointerException | IOException e) {
			LOGGER.error("", e);
			System.exit(0);
		}
		//
		int ix = -1;
		//
		while (isRun) {
			try {
				if (s.select(1000L) > 0) {
					s.selectedKeys().clear();
				}

				final SocketChannel sc = ssc.accept();
				if (sc != null) {
					sc.configureBlocking(false);
					sc.setOption(StandardSocketOptions.SO_KEEPALIVE, false);
					sc.setOption(StandardSocketOptions.SO_REUSEADDR, false);
					sc.setOption(StandardSocketOptions.TCP_NODELAY, false);
					sc.setOption(StandardSocketOptions.SO_LINGER, 0);
					sc.setOption(StandardSocketOptions.SO_SNDBUF, 1024 * 8);
					sc.setOption(StandardSocketOptions.SO_RCVBUF, 1024 * 8);
					final int readThreadPos = ix = (++ix) % readPoolSize;
					readThreadPool.accept(readThreadPos, sc);
				}
			} catch (ClosedChannelException e) {
				LOGGER.info("ClosedChannelException");
			} catch (Exception e) {
				LOGGER.error("", e);
			}
		}
	}

	public void shutdown() {
		LOGGER.debug("");
		isRun = false;
		try {
			ssc.close();
		} catch (IOException e) {
			LOGGER.error("", e);
		}
	}
}