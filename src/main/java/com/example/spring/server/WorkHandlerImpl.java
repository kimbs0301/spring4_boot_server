package com.example.spring.server;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.example.spring.server.protocol.Packet;

/**
 * @author gimbyeongsu
 * 
 */
public final class WorkHandlerImpl implements WorkHandler {
	private SocketChannel sc;
	private ByteBuffer buffer = ByteBuffer.allocate(Packet.BUFFER_SIZE_DEFAULT);
	private static final int SEND_LIMIT = 500;

	public WorkHandlerImpl(SocketChannel sc) {
		this.sc = sc;
	}

	@Override
	public void connect(int threadNumber, int handlerID) {
		//System.out.println("connect threadNumber:" + threadNumber + " handlerID:" + handlerID);
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
			
			//sk.interestOps(SelectionKey.OP_WRITE);
			
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
				if (++wc > SEND_LIMIT)
				{
					System.out.println("SEND LIMIT:" + sc.toString());
					break;
				}
			} while (byteBuffer.hasRemaining());
//			System.out.println("write ok " + wc);

		} catch (BufferUnderflowException | NotYetConnectedException e) {
			e.printStackTrace();
		} catch (ClosedChannelException e) {
			e.printStackTrace();
			sk.cancel();
		} catch (IOException e) {
			e.printStackTrace();
			sk.cancel();
			close();
		}
	}

	@Override
	public void close() {
		System.out.println("close");
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