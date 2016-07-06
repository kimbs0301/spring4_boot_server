package com.example.spring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;

import com.example.spring.logic.TestObj;

@Configurable
public class AfterConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(AfterConfig.class);
	
	public AfterConfig() {
		LOGGER.debug("생성자 AfterConfig()");
	}
	
	@Bean
	public TestObj testObj() {
		return new TestObj();
	}
	
	@Bean
	public RequiredAnnotationBeanPostProcessor requiredAnnotationBeanPostProcessor() {
		return new RequiredAnnotationBeanPostProcessor();
	}
}
