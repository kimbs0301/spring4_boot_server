package com.example.spring.server;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.spring.server.protocol.Packet;

/**
 * @author gimbyeongsu
 * 
 */
public final class ReadHandlerImpl implements ReadHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadHandlerImpl.class);

	private final ExecutorService workThreadExecutorService;
	private final WorkThread worker;
	private final SessionChannelManager sessionChannelManager;
	private SocketChannel sc;
	private ByteBuffer rBuf = ByteBuffer.allocate(Packet.BUFFER_SIZE_DEFAULT);
	// private final ByteBuffer rBuf = ByteBuffer.allocateDirect( Packet.BUFFER_SIZE_DEFAULT );
	private SessionCentext session;
	private int rb; // 읽어들인 바이트수
	private int pb; // 패킷으로부터 최종적으로 읽어들여야 하는 바이트수

	public ReadHandlerImpl(ExecutorService workThreadExecutorService, WorkThread work,
			SessionChannelManager sessionChannelManager, SocketChannel sc) {
		this.workThreadExecutorService = workThreadExecutorService;
		this.worker = work;
		this.sessionChannelManager = sessionChannelManager;
		this.sc = sc;
	}

	@Override
	public void connect(int threadNumber, int handlerID) {
		session = sessionChannelManager.addSession(threadNumber, handlerID, sc);
	}

	@Override
	public void received(SelectionKey sk) {
		int ret = 0;
		/**
		 * 패킷 처음 진입
		 */
		// LOGGER.debug( "rb:{}" , new Object[] { rb } );
		if (rb == 0) // 패킷 처음 진입
		{
			// if ( ( time + TERM_TIME ) > System.currentTimeMillis( ) )
			// {
			// LOGGER.warn( "packet fast" );
			// }
			// time = System.currentTimeMillis( );
			//
			// LOGGER.debug( "패킷 처음 진입" );
			rBuf.limit(Packet.HEAD_A_SIZE); // 해더 읽어들일 사이즈만큼 설정
			try {
				ret = sc.read(rBuf); // 해더 읽어 들임
				rb += ret; // 수신패킷 합치기
				if (ret == Packet.HEAD_A_SIZE) {
					rBuf.flip();
					// LOGGER.debug( "remaining : {}" , rBuf.remaining( ) );
					final byte sop = rBuf.get();
					switch (sop) {
					case 0x07:
						break;
					default:
						LOGGER.error("bad sop : {}", sop);
						errorPacketClear(sk);
						return;
					}
					final int length = rBuf.getInt();
					// LOGGER.debug( "{} {}" , new Object[] { sop , length } );
					// 읽어야할 패킷수 계산
					pb = Packet.HEAD_FULL_SIZE + length;
					if (length > Packet.BUFFER_SIZE_DEFAULT) {
						LOGGER.warn("잘못된 패킷 길이로 인한 수신 패킷 버리기 A");
						errorPacketClear(sk);
						return;
					}
					rBuf.limit(pb); // 바디 읽어 들일 사이즈 만큼 설정
					ret = sc.read(rBuf); // 바디 읽어 들임
					rb += ret; // 수신패킷 합치기
					// LOGGER.debug( "rBytes:{} pBytes:{}" , new Object[] { rBytes , pBytes } );
					if (pb == rb) // 정상
					{
						addWork();
						// if ( rBuf.remaining( ) == pb )
						// {
						// addWork( );
						// //logger.debug( "패킷처리완료" );
						// }
						// else
						// {
						// LOGGER.warn( "잘못된 패킷 으로 인한 수신 패킷 버리기 B" );
						// errorPacketClear( sk );
						// }
					} else if (ret == -1) {
						sk.cancel();
						close();
						return;
					}
				} else if (ret == -1) {
					sk.cancel();
					close();
					return;
				} else {
					LOGGER.warn("패킷해더 다 못읽어 들임 remaining:{} pos:{} rBytes:{} pBytes:{}",
							new Object[] { rBuf.remaining(), rBuf.position(), rb, pb });
				}
			} catch (BufferUnderflowException e) {
				LOGGER.error("{}", e.getMessage());
				return;
			} catch (NotYetConnectedException e) {
				LOGGER.error("{}", e.getMessage());
				return;
			} catch (ClosedChannelException e) {
				sk.cancel();
				LOGGER.error("{}", e.getMessage());
				return;
			} catch (IOException e) {
				sk.cancel();
				close();
				LOGGER.error("READ1:{}", e.getMessage());
				return;
			}

		}
		/**
		 * 해더 다 못 읽어들임
		 */
		else if (rb < Packet.HEAD_A_SIZE) // 해더 다 못 읽어들임
		{
			LOGGER.warn("패킷해더 다 못읽어 들임");
			// rBuf.limit( Packet.HEAD_SIZE ); // 해더 읽어들일 사이즈만큼 설정
			try {
				ret = sc.read(rBuf); // 해더 읽어 들임
				rb += ret; // 수신패킷 합치기
				// LOGGER.debug( "rBytes:{} ret:{}" , new Object[] { rBytes , ret } );
				if (rb == Packet.HEAD_A_SIZE) {
					rBuf.flip();
					// LOGGER.debug( "remaining : {}" , rBuf.remaining( ) );
					final byte sop = rBuf.get();
					switch (sop) {
					case 7:
						break;
					default:
						LOGGER.error("bad sop : {}", sop);
						errorPacketClear(sk);
						return;
					}
					final int length = rBuf.getInt();
					// LOGGER.debug( "{} {}" , new Object[] { sop , length } );
					// 읽어야할 패킷수 계산
					pb = Packet.HEAD_FULL_SIZE + length;
					if (length > Packet.BUFFER_SIZE_DEFAULT) {
						LOGGER.warn("잘못된 패킷 길이로 인한 수신 패킷 버리기 C");
						errorPacketClear(sk);
						return;
					}
					rBuf.limit(pb); // 바디 읽어 들일 사이즈 만큼 설정
					ret = sc.read(rBuf); // 바디 읽어 들임
					rb += ret; // 수신패킷 합치기
					// LOGGER.debug( "rBytes:{} pBytes:{}" , new Object[] { rBytes , pBytes } );
					if (pb == rb) // 정상
					{
						addWork();
						// if ( rBuf.remaining( ) == pb )
						// {
						// addWork( );
						// //logger.debug( "패킷처리완료" );
						// }
						// else
						// {
						// LOGGER.warn( "잘못된 패킷 으로 인한 수신 패킷 버리기 D" );
						// errorPacketClear( sk );
						// }
					} else if (ret == -1) {
						sk.cancel();
						close();
						return;
					}
				} else if (ret == -1) {
					sk.cancel();
					close();
					return;
				} else {
					LOGGER.warn("패킷해더 다 못읽어 들임");
				}

			} catch (BufferUnderflowException e) {
				LOGGER.error("{}", e.getMessage());
				return;
			} catch (NotYetConnectedException e) {
				LOGGER.error("{}", e.getMessage());
				return;
			} catch (ClosedChannelException e) {
				sk.cancel();
				LOGGER.error("{}", e.getMessage());
				return;
			} catch (IOException e) {
				sk.cancel();
				close();
				LOGGER.error("READ2:{}", e.getMessage());
				return;
			}
		}
		/**
		 * 다음 패킷 진입
		 */
		else // 다음 패킷 진입
		{
			LOGGER.warn("다음 패킷 진입 position:{} limit:{}", new Object[] { rBuf.position(), rBuf.limit() });
			try {
				ret = sc.read(rBuf); // 해더 읽어 들임
				rb += ret; // 수신패킷 합치기
				// LOGGER.debug( "rBytes:{} pBytes:{}" , new Object[] { rBytes , pBytes } );
				if (pb == rb) // 정상
				{
					addWork();
					// if ( rBuf.remaining( ) == pb )
					// {
					// addWork( );
					// //LOGGER.debug( "패킷처리완료" );
					// }
					// else
					// {
					// LOGGER.warn( "잘못된 패킷 으로 인한 수신 패킷 버리기 E" );
					// errorPacketClear( sk );
					// }
				} else if (ret == -1) {
					sk.cancel();
					close();
					return;
				} else // if ( rBuf.position( ) == rBuf.limit( ) && ret == 0 )
				{
					LOGGER.warn("잘못된 패킷 수신 pos:{} limit:{}", new Object[] { rBuf.position(), rBuf.limit() });
					sk.cancel();
					close();
					return;
				}
			} catch (NotYetConnectedException e) {
				LOGGER.error("{}", e.getMessage());
				return;
			} catch (ClosedChannelException e) {
				sk.cancel();
				LOGGER.error("{}", e.getMessage());
				return;
			} catch (IOException e) {
				sk.cancel();
				close();
				LOGGER.error("READ3:{}", e.getMessage());
				return;
			}
		}
	}

	@Override
	public void addWork() {
		rBuf.flip();
		// LOGGER.debug( "패킷수신성공함" );
		// LOGGER.debug( "rBuf.limit : {}" , rBuf.limit( ) );
		WorkHandlerImpl pool = new WorkHandlerImpl(sc);
		try {
			final byte sop = rBuf.get(); // sop
			pool.bodyLength = rBuf.getInt(); // length
			// LOGGER.debug( "length : {}" , length );
			pool.time = rBuf.getLong();
			pool.body = new byte[pool.bodyLength];
			rBuf.get(pool.body, 0, pool.bodyLength - 8);
			//
			switch (sop) {
			case 0x07:
				worker.queue.add(pool); // 작업등록
				int state = worker.state.getAndSet(WorkerState.NEW_CMD); // 새 Command를 추가했다고 통보한다.
				if (state == WorkerState.WAIT) // 만약 이전 상태가 대기 상태였다면
				{
					workThreadExecutorService.execute(worker);
				}

				session.setAliveTime(System.currentTimeMillis()); // TEST
				// LOGGER.debug( "{}" , new String( pool.body , 0 , pool.bodyLength - 8 ) );
				break;
			default:
				LOGGER.warn("default sop:{}", sop);
				break;
			}
		} catch (BufferUnderflowException e) {
			LOGGER.error("{}", e.getMessage());
		} catch (IndexOutOfBoundsException e) {
			LOGGER.error("{}", e.getMessage());
		}
		// 패킷수신 초기화
		rBuf.position(0); // 패킷초기상태로
		rb = 0;
		pb = 0;
	}

	@Override
	public void close() {
		sessionChannelManager.removeSession(session.getThreadNum(), session.getHandlerId());
	}

	/**
	 * 비정상 패킷을 버림
	 * 
	 * @param sk
	 */
	private void errorPacketClear(SelectionKey sk) {
		rBuf.clear();
		try {
			int ret = sc.read(rBuf); // 해당 패킷 비우기
			if (ret == -1) {
				sk.cancel();
				close();
			}
		} catch (NotYetConnectedException e) {
			LOGGER.error("{}", e.getMessage());
		} catch (ClosedChannelException e) {
			sk.cancel();
			LOGGER.error("{}", e.getMessage());
		} catch (IOException e) {
			sk.cancel();
			close();
			LOGGER.error("{}", e.getMessage());
		} catch (Exception e) {
			sk.cancel();
			close();
			LOGGER.error("", e);
		}
		// 패킷수신 초기화
		rBuf.position(0); // 패킷초기상태로
		rb = 0;
		pb = 0;
	}
}