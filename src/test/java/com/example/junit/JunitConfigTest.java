package com.example.junit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.example.junit.JunitSpringAnnotation;

/**
 * @author gimbyeongsu
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@JunitSpringAnnotation
public class JunitConfigTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(JunitConfigTest.class);

	@Autowired
	private Environment environment;

	@Test
	public void test() throws Exception {
		LOGGER.debug("test");
		String[] activeProfiles = environment.getActiveProfiles();
		for (String activeProfile : activeProfiles) {
			LOGGER.debug("{}", activeProfile);
		}

		LOGGER.debug("{}", environment.getRequiredProperty("server.port"));
		LOGGER.debug("{}", environment.getRequiredProperty("lang.ko"));
	}
}
