package com.example.spring.server;

/**
 * @author gimbyeongsu
 * 
 */
public class BootConfigFactory {
	private String ip = "127.0.0.1";
	private int port = 8000;
	private int acceptThreadPriority = Thread.MAX_PRIORITY;
	private String acceptThreadName = "Accept";
	private int readThreadPriority = Thread.MAX_PRIORITY;
	private String readThreadName = "Read";
	private int readThreadSize = 2;
	private int workThreadPriority = Thread.MAX_PRIORITY;
	private String workThreadName = "Work";
	private int workThreadSize = Runtime.getRuntime().availableProcessors();
	private int sendThreadBufferType = 0;
	private int sendThreadBufferSize = 1024;

	public BootConfigFactory() {
	}

	public String getIp() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getAcceptThreadPriority() {
		return acceptThreadPriority;
	}
	
	public String getAcceptThreadName() {
		return acceptThreadName;
	}
	
	public int getReadThreadPriority() {
		return readThreadPriority;
	}
	
	public String getReadThreadName() {
		return readThreadName;
	}
	
	public int getReadThreadSize() {
		return readThreadSize;
	}
	
	public int getWorkThreadPriority() {
		return workThreadPriority;
	}
	
	public String getWorkThreadName() {
		return workThreadName;
	}
	
	public int getWorkThreadSize() {
		return workThreadSize;
	}
	
	public int getSendThreadBufferType() {
		return sendThreadBufferType;
	}
	
	public int getSendThreadBufferSize() {
		return sendThreadBufferSize;
	}
	
	
	
	

	public void setIp(String ip) {
		this.ip = ip;
	}

	

	public void setPort(int port) {
		this.port = port;
	}

	

	public void setAcceptThreadPriority(int acceptThreadPriority) {
		this.acceptThreadPriority = acceptThreadPriority;
	}

	

	public void setAcceptThreadName(String acceptThreadName) {
		this.acceptThreadName = acceptThreadName;
	}

	

	public void setReadThreadPriority(int readThreadPriority) {
		this.readThreadPriority = readThreadPriority;
	}

	

	public void setReadThreadName(String readThreadName) {
		this.readThreadName = readThreadName;
	}

	

	public void setReadThreadSize(int readThreadSize) {
		this.readThreadSize = readThreadSize;
	}

	

	public void setWorkThreadPriority(int workThreadPriority) {
		this.workThreadPriority = workThreadPriority;
	}

	

	public void setWorkThreadName(String workThreadName) {
		this.workThreadName = workThreadName;
	}

	

	public void setWorkThreadSize(int workThreadSize) {
		this.workThreadSize = workThreadSize;
	}

	

	/**
	 * 0:DIRECT_BUFFER 1:BUFFER
	 * 
	 * @param sendThreadBufferType
	 */
	public void setSendThreadBufferType(int sendThreadBufferType) {
		this.sendThreadBufferType = sendThreadBufferType;
	}

	

	public void setSendThreadBufferSize(int sendThreadBufferSize) {
		this.sendThreadBufferSize = sendThreadBufferSize;
	}
}
