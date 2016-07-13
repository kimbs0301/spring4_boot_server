package com.example.spring.server;

import java.nio.channels.SocketChannel;

/**
 * @author gimbyeongsu
 * 
 */
public final class SessionCentext {
	private final SocketChannel sc;
	private final int threadNum;
	private final int handlerId;
	private final String remoteSocketAddress;
	//
	private final long startTime = System.currentTimeMillis();
	private long aliveTime = startTime;

	public SessionCentext(int threadNum, int handlerId, SocketChannel sc) {
		this.threadNum = threadNum;
		String rsa = sc.socket().getRemoteSocketAddress().toString();
		this.remoteSocketAddress = rsa.substring(1, rsa.length());
		this.handlerId = handlerId;
		this.sc = sc;
	}

	public SocketChannel getSocketChannel() {
		return sc;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public int getHandlerId() {
		return handlerId;
	}

	public long getAliveTime() {
		return aliveTime;
	}

	public void setAliveTime(long aliveTime) {
		this.aliveTime = aliveTime;
	}

	public String getRemoteSocketAddress() {
		return remoteSocketAddress;
	}
}