package com.example.spring.logic;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author gimbyeongsu
 * 
 */
@Service
public class TestService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestService.class);

	@Value("#{configProperties['lang.ko']}")
	private String langKo;
	
	public TestService() {
		LOGGER.debug("생성자 TestService()");
	}
	
	@PostConstruct
	public void init() {
		LOGGER.debug("{}", langKo);
	}
}
