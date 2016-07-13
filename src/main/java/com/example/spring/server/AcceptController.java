package com.example.spring.server;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AcceptController {
	private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock(false);
	private final Lock readLock = reentrantReadWriteLock.readLock();
	private final Lock writeLock = reentrantReadWriteLock.writeLock();
	private int[] idx;

	public AcceptController() {
	}

	public void createIdx(int size) {
		idx = new int[size];
	}

	/**
	 * 해당 수신 스레드의 접속자수 증가
	 * 
	 * @param i
	 */
	public void add(int i) {
		writeLock.lock();
		try {
			++idx[i];
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * 해당 수신 스레드의 접속자수 감소
	 * 
	 * @param i
	 */
	public void sub(int i) {
		writeLock.lock();
		try {
			--idx[i];
		} finally {
			writeLock.unlock();
		}
	}

	public int minPos() {
		readLock.lock();
		int min = idx[0];
		int index = 0;
		try {
			for (int size = idx.length, i = 1; i < size; i++) {
				if (idx[i] < min) {
					// min = idx[ i ];
					index = i;
					break;
				}
			}
		} finally {
			readLock.unlock();
		}
		return index;
	}
}