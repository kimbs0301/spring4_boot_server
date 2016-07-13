package com.example.spring.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gimbyeongsu
 * 
 */
public final class SocketChannelGroup {
	private final static Logger LOGGER = LoggerFactory.getLogger(SocketChannelGroup.class);

	private final static int SEND_LIMIT = 200;
	private final static AtomicInteger NEXT_NUM = new AtomicInteger();
	private final String name;
	private final ConcurrentHashMap<Integer, SocketChannel> channelMap = new ConcurrentHashMap<Integer, SocketChannel>();

	public SocketChannelGroup() {
		this("GROUP-" + NEXT_NUM.incrementAndGet());
	}

	public SocketChannelGroup(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void put(int key, SocketChannel value) {
		channelMap.put(Integer.valueOf(key), value);
	}

	public void remove(int key) {
		channelMap.remove(Integer.valueOf(key));
	}

	public boolean channelMapIsEmpty() {
		return channelMap.isEmpty();
	}

	public void write(ByteBuffer buffer) {
		Collection<SocketChannel> channelList = channelMap.values();
		for (SocketChannel sc : channelList) {
			if (sc != null) {
				buffer.flip();
				try {
					sc.write(buffer);
				} catch (IOException e) {
				}
			}
		}
	}

	public void write(byte[] array) {
		ByteBuffer buffer = ByteBuffer.wrap(array);
		Collection<SocketChannel> channelList = channelMap.values();
		for (SocketChannel sc : channelList) {
			if (sc != null) {
				buffer.flip();
				try {
					sc.write(buffer);
				} catch (IOException e) {
				}
			}
		}
	}

	public void writeLoop(ByteBuffer buffer) {
		Collection<SocketChannel> channelList = channelMap.values();
		for (SocketChannel sc : channelList) {
			if (sc != null) {
				buffer.flip();
				int wc = 0;
				try {
					do {
						if (sc.isConnected() == false) {
							break;
						}
						sc.write(buffer);
						if (++wc > SEND_LIMIT) // 보낼 패킷이 루프제한만큼 돌았는데도 못보낼시 루프 빠져나오기
						{
							LOGGER.warn("SEND LIMIT:{}", sc.toString());
							break;
						}
					} while (buffer.hasRemaining());
				} catch (IOException e) {
					LOGGER.error("WRITE:{}", e.getMessage());
				}
			}
		}
	}

	public void writeLoop(byte[] array) {
		ByteBuffer buffer = ByteBuffer.wrap(array);
		Collection<SocketChannel> channelList = channelMap.values();
		for (SocketChannel sc : channelList) {
			if (sc != null) {
				buffer.flip();
				int wc = 0;
				try {
					do {
						if (sc.isConnected() == false) {
							break;
						}
						sc.write(buffer);
						if (++wc > SEND_LIMIT) // 보낼 패킷이 루프제한만큼 돌았는데도 못보낼시 루프 빠져나오기
						{
							LOGGER.warn("SEND LIMIT:{}", sc.toString());
							break;
						}
					} while (buffer.hasRemaining());
				} catch (IOException e) {
					LOGGER.error("WRITE:{}", e.getMessage());
				}
			}
		}
	}
}