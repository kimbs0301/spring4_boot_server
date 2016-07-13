package com.example.spring.server;

/**
 * @author gimbyeongsu
 * 
 */
public final class PacketBuffer {
	public final long time;
	public final int bodyLength;
	public final byte[] body;

	public PacketBuffer(long time, int bodyLength, byte[] body) {
		this.time = time;
		this.bodyLength = bodyLength;
		this.body = body;
	}
}