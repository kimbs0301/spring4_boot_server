package com.example.spring.server;

import java.nio.channels.SocketChannel;

/**
 * @author gimbyeongsu
 * 
 */
public final class AcceptSocketChannel {
	public final SocketChannel sc;
	public final int id;

	public AcceptSocketChannel(SocketChannel sc, int id) {
		this.sc = sc;
		this.id = id;
	}
}