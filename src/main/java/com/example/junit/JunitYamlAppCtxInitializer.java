package com.example.junit;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

/**
 * @author gimbyeongsu
 * 
 */
public class JunitYamlAppCtxInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	private static final Logger LOGGER = LoggerFactory.getLogger(JunitYamlAppCtxInitializer.class);

	public JunitYamlAppCtxInitializer() {
		LOGGER.debug("생성자 JunitYamlAppCtxInitializer()");
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		try {
			Resource resource = applicationContext.getResource("classpath:application-junit.yml");
			YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
			PropertySource<?> yamlTestProperties = sourceLoader.load("yamlTestProperties", resource, null);
			applicationContext.getEnvironment().getPropertySources().addFirst(yamlTestProperties);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}