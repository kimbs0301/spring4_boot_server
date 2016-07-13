package com.example.spring.server;

/**
 * @author gimbyeongsu
 * 
 */
public final class WorkerState {
	/**
	 * 대기
	 */
	public final static int WAIT = 0;
	/**
	 * 처리중
	 */
	public final static int PROCESSED = 1;
	/**
	 * new commande
	 */
	public final static int NEW_CMD = 2;

	private WorkerState() {
	}
}