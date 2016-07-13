package com.example.spring.server;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.spring.server.protocol.MsgID;

/**
 * @author gimbyeongsu
 * 
 */
public final class WorkHandlerImpl implements WorkHandler {
	private final static Logger LOGGER = LoggerFactory.getLogger(WorkHandlerImpl.class);

	private static final int SEND_LIMIT = 500;
	private SocketChannel sc;
	// packet
	public long time;
	public int bodyLength;
	public byte[] body;

	public WorkHandlerImpl(SocketChannel sc) {
		this.sc = sc;
	}

	// @Override
	// public void setSocketChannel( SocketChannel sc )
	// {
	// this.sc = sc;
	// }

	@Override
	public void work(ByteBuffer sendBuf) {
		try {
			sendBuf.put(MsgID.MSG_DATA);
			sendBuf.putInt(bodyLength);
			sendBuf.putLong(time);
			sendBuf.put(body, 0, bodyLength - 8);
		} catch (BufferOverflowException e) {
			LOGGER.error("{}", e.getMessage());
			return;
		} catch (Exception e) {
			LOGGER.error("", e);
			return;
		}
		writeLoop(sendBuf);
	}

	private void writeLoop(ByteBuffer sendBuf) {
		sendBuf.flip();
		try {
			int wc = 0;
			do {
				if (sc.isConnected() == false) {
					break;
				}
				sc.write(sendBuf);
				if (++wc > SEND_LIMIT) // 보낼 패킷이 루프제한만큼 돌았는데도 못보낼시 루프 빠져나오기
				{
					LOGGER.warn("SEND LIMIT:{}", sc.toString());
					break;
				}
				// LOGGER.info( "write" );
			} while (sendBuf.hasRemaining());
		} catch (ClosedChannelException e) {
			LOGGER.error("WRITE:{}", e.getMessage());
		} catch (IOException e) {
			LOGGER.error("WRITE:{}", e.getMessage());
		}
	}
}