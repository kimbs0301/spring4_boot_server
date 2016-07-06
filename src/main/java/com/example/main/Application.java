package com.example.main;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.example.spring.config.AfterConfig;
import com.example.spring.config.RootConfig;

/**
 * @author gimbyeongsu
 * 
 */
@Configuration
@ComponentScan(basePackages = { "com.example.spring" }, excludeFilters = { @Filter(pattern = { "com.example.spring.*.model.*" }, type = FilterType.REGEX) })
public class Application {
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	private static final Object[] CONFIG_CLASS = { Application.class, RootConfig.class, AfterConfig.class };

	public Application() {
		LOGGER.debug("생성자 Application()");
	}

	public static void main(String[] args) throws IOException {
		LOGGER.debug("start");
		SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(CONFIG_CLASS);
		SpringApplication springApplication = springApplicationBuilder.build();
		springApplication.run();
		while(true){
			System.in.read();
		}
	}
}
