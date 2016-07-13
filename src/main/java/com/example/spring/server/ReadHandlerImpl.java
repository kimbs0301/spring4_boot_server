package com.example.spring.server;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.spring.server.protocol.Packet;

/**
 * @author gimbyeongsu
 * 
 */
public final class ReadHandlerImpl implements ReadHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadHandlerImpl.class);
	private SocketChannel sc;
	private ByteBuffer buffer = ByteBuffer.allocate(Packet.BUFFER_SIZE_DEFAULT);
	private static final int SEND_LIMIT = 500;

	public ReadHandlerImpl(SocketChannel sc) {
		this.sc = sc;
	}

	@Override
	public void connect(int threadNumber, int handlerID) {
		LOGGER.debug("connect threadNumber:{} handlerID:{}", threadNumber, handlerID);
	}

	/**
	 * 전송 과정에서 생기는 패킷 단편화 처리 필요
	 * +----+-------+---+---+
	 * | AB | CDEFG | H | I |
	 * +----+-------+---+---+
	 */
	@Override
	public void received(SelectionKey sk) {
		int ret = 0;

		try {
			buffer.clear();
			ret = sc.read(buffer);
			if (ret == -1) {
				sk.cancel();
				close();
				return;
			}
			buffer.flip();

			// byte startPacket = buffer.get(); // 시작 패킷 알림
			buffer.get();
			int length = buffer.getInt();
			byte[] body = new byte[length];
			buffer.get(body);

			// sk.interestOps(SelectionKey.OP_WRITE);

			ByteBuffer byteBuffer = ByteBuffer.allocate(Packet.BUFFER_SIZE_DEFAULT);
			byteBuffer.put((byte) 0x07);
			byteBuffer.putInt(length);
			byteBuffer.put(body, 0, length);
			byteBuffer.flip();

			int wc = 0;
			do {
				if (sc.isConnected() == false) {
					break;
				}
				sc.write(byteBuffer);
				if (++wc > SEND_LIMIT) {
					LOGGER.info("SEND LIMIT:{}", sc.toString());
					break;
				}
			} while (byteBuffer.hasRemaining());
			LOGGER.debug("write ok {}", wc);
		} catch (BufferUnderflowException | NotYetConnectedException e) {
			LOGGER.error("", e);
		} catch (ClosedChannelException e) {
			sk.cancel();
			LOGGER.error("", e);
		} catch (IOException e) {
			sk.cancel();
			close();
			LOGGER.error("", e);
		}
	}

	@Override
	public void close() {
		LOGGER.info("close");
		try {
			sc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sc = null;
		buffer.clear();
		buffer = null;
	}
}