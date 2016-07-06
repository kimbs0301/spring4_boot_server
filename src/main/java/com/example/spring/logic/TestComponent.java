package com.example.spring.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TestComponent {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestComponent.class);

	public TestComponent() {
		LOGGER.debug("생성자 Component()");
	}
}
