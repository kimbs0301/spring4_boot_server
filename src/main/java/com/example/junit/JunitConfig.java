package com.example.junit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.FilterType;

/**
 * @author gimbyeongsu
 * 
 */
@Configuration
@ComponentScan(basePackages = { "com.example.spring" }, excludeFilters = { @Filter(pattern = { "com.example.spring.*.model.*" }, type = FilterType.REGEX) })
@DependsOn(value = { "rootConfig" })
public class JunitConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(JunitConfig.class);

	public JunitConfig() {
		LOGGER.debug("생성자 JunitConfig()");
	}
}
