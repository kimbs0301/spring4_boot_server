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
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gimbyeongsu
 * 
 */
public final class AcceptThread implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(AcceptThread.class);

	private ReadThreadPool readThreadPool;
	private final ServerSocketChannel ssc;
	@Autowired
	private SessionChannelManager sessionChannelManager;
	private boolean isRun = true;

	public AcceptThread(ServerSocketChannel ssc, ReadThreadPool readThreadPool) {
		this.ssc = ssc;
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
					final int readThreadPos = sessionChannelManager.minPos();
					readThreadPool.accept(readThreadPos, sc);
					sessionChannelManager.add(readThreadPos);
				}
			} catch (ClosedChannelException e) {
				LOGGER.info("ClosedChannelException");
			} catch (Exception e) {
				LOGGER.error("", e);
			}
		}
		
		try {
			s.close();
		} catch (IOException e) {
			LOGGER.error("", e);
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