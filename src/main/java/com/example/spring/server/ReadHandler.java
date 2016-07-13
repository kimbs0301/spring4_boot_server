package com.example.spring.server;

import java.nio.channels.SelectionKey;

/**
 * @author gimbyeongsu
 * 
 */
public interface ReadHandler {
	public void connect(int threadNumber, int handlerID);

	public void received(SelectionKey selKey);

	public void close();
}