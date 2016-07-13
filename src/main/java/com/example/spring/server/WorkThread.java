package com.example.spring.server;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gimbyeongsu
 * 
 */
public final class WorkThread implements Runnable {
	public final ConcurrentLinkedQueue<WorkHandler> queue = new ConcurrentLinkedQueue<WorkHandler>();
	public final AtomicInteger state = new AtomicInteger(WorkerState.WAIT);
	public final static String WORK_THREAD_NAME = "Work";
	private ByteBuffer sendBuf = null;

	public WorkThread() {
		sendBuf = ByteBuffer.allocate(2048);
	}

	@Override
	public void run() {
		// LOGGER.info( "{} run ({})" , new Object[] { getName( ) , getPriority( ) } );

		while (true) {
			state.set(WorkerState.PROCESSED); // 처리중으로 세팅한다.
			WorkHandler h = queue.poll();
			if (h != null) {
				sendBuf.clear();
				try {
					h.work(sendBuf);
				} catch (NullPointerException ne) {
				}
				continue; // 다음 Command 처리를 위해
			}

			if (state.compareAndSet(WorkerState.PROCESSED, WorkerState.WAIT)) // 처리중에서 대기중으로 바꾸려고 시도한다.
			{
				break; // 성공하면 종료한다.
			}
		}
	}
}