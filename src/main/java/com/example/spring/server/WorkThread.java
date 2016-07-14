package com.example.spring.server;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gimbyeongsu
 * 
 */
public final class WorkThread implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkThread.class);

	public final ConcurrentLinkedQueue<WorkHandler> queue = new ConcurrentLinkedQueue<WorkHandler>();
	public final AtomicInteger state = new AtomicInteger(WorkerState.WAIT);
	private ByteBuffer sendBuf = null;

	public WorkThread(BootConfigFactory config) {
		if (config.getSendThreadBufferType() == 0) {
			sendBuf = ByteBuffer.allocateDirect(config.getSendThreadBufferSize());
		} else {
			sendBuf = ByteBuffer.allocate(config.getSendThreadBufferSize());
		}
	}

	@Override
	public void run() {
		while (true) {
			// 처리중으로 세팅한다.
			state.set(WorkerState.PROCESSED);
			WorkHandler h = queue.poll();
			if (h != null) {
				sendBuf.clear();
				try {
					h.work(sendBuf);
				} catch (NullPointerException npe) {
					LOGGER.error("", npe);
				}
				// 다음 Command 처리를 위해
				continue;
			}

			// 처리중에서 대기중으로 바꾸려고 시도한다.
			if (state.compareAndSet(WorkerState.PROCESSED, WorkerState.WAIT)) {
				// 성공하면 종료한다.
				break;
			}
		}
	}
}