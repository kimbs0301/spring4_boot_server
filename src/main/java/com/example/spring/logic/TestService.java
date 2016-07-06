package com.example.spring.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TestService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestService.class);

	public TestService() {
		LOGGER.debug("생성자 TestService()");
	}
}
