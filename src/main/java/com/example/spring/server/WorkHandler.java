package com.example.spring.server;

import java.nio.ByteBuffer;

/**
 * @author gimbyeongsu
 * 
 */
public interface WorkHandler {
	public void work(ByteBuffer sendBuf);
}
