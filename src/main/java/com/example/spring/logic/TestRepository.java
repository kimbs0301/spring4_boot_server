package com.example.spring.logic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * @author gimbyeongsu
 * 
 */
@Repository
public class TestRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestRepository.class);

	public TestRepository() {
		LOGGER.debug("생성자 TestRepository()");
	}
}
