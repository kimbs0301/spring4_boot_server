package com.example.spring.server.protocol;

/**
 * @author gimbyeongsu
 * 
 */
public class Packet {
	// public final static int BUFFER_SIZE_DEFAULT = 1024 * 1024 * 2;
	public final static int BUFFER_SIZE_DEFAULT = 1024 * 2;
	// header a
	public final static int START_OF_PACKET_SIZE = 1;
	public final static int BODY_LENGTH_SIZE = 4;
	//
	public final static int HEAD_A_SIZE = START_OF_PACKET_SIZE + BODY_LENGTH_SIZE;
	public final static int HEAD_FULL_SIZE = HEAD_A_SIZE;
}