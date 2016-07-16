package com.example.spring.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gimbyeongsu
 * 
 */
public final class SessionChannelManager extends AcceptController {
	private final static Logger LOGGER = LoggerFactory.getLogger(SessionChannelManager.class);

	private final ConcurrentHashMap<Integer, SessionCentext> sContextMap = new ConcurrentHashMap<Integer, SessionCentext>();
	private boolean isOne = true;

	public SessionChannelManager(int poolSize) {
		if (isOne) {
			createIdx(poolSize);
			isOne = false;
		}
	}

	/**
	 * 핸들러 소켓 채널 가져오기
	 * 
	 * @param handler
	 * @return
	 */
	public SessionCentext getSocketChannel(int handler) {
		return sContextMap.get(handler);
	}

	/**
	 * 핸들러 소켓 채널 등록
	 * 
	 * @param handler
	 * @param sChannel
	 */
	public SessionCentext addSession(int threadNumber, int handler, SocketChannel sChannel) {
		add(threadNumber);
		SessionCentext sc = new SessionCentext(threadNumber, handler, sChannel);
		sContextMap.put(handler, sc);
		LOGGER.info("{}]Connection handlerID : {}", threadNumber, handler);
		return sc;
	}

	/**
	 * 핸들러 소켓 채널 삭제
	 * 
	 * @param handler
	 */
	public void removeSession(int threadNumber, int handler) {
		sub(threadNumber);
		SessionCentext sc = sContextMap.remove(handler);
		try {
			sc.getSocketChannel().close();
		} catch (IOException e) {
			LOGGER.error("", e);
		}
		LOGGER.info("{}]close from handlerID : {}", threadNumber, handler);
	}

	/**
	 * 현제 등록된 세션 리스트 가져오기
	 * 
	 * @return
	 */
	public ArrayList<SessionCentext> getSessionList() {
		Set<Integer> set = sContextMap.keySet();
		Iterator<Integer> iterator = set.iterator();
		ArrayList<SessionCentext> list = new ArrayList<SessionCentext>();
		while (iterator.hasNext()) {
			Integer key = iterator.next();
			list.add(sContextMap.get(key));
		}
		return list;
	}

	/**
	 * 현제 등록된 세션
	 */
	public Iterator<Entry<Integer, SessionCentext>> iterator() {
		return sContextMap.entrySet().iterator();
	}

	/**
	 * 현제 등록된 세션 콜렉션
	 */
	public Collection<SessionCentext> values() {
		return sContextMap.values();
	}

	public void shutdown() {

	}

	public int sessionContextMapSize() {
		return sContextMap.size();
	}

}