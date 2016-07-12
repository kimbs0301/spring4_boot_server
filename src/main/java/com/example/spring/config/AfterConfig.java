package com.example.spring.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import com.example.spring.server.ReadThreadPool;

/**
 * @author gimbyeongsu
 * 
 */
@Configuration
@DependsOn(value = { "rootConfig", "serverConfig" })
public class AfterConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(AfterConfig.class);

	@Autowired
	private Environment environment;
	@Autowired
	private ReadThreadPool readThreadPool;

	public AfterConfig() {
		LOGGER.debug("생성자 AfterConfig()");
	}

	@PostConstruct
	public void init() {
		LOGGER.debug("");
		
		readThreadPool.startPool();
		
		String[] activeProfiles = environment.getActiveProfiles();
		for (String activeProfile : activeProfiles) {
			LOGGER.debug("{}", activeProfile);
		}

		LOGGER.debug("{}", environment.getRequiredProperty("lang.ko"));
	}
}
