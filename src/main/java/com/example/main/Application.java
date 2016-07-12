package com.example.main;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpEncodingAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/**
 * @author gimbyeongsu
 * 
 */
@Configuration
@EnableAutoConfiguration(exclude = { PropertyPlaceholderAutoConfiguration.class, //
		SecurityAutoConfiguration.class, //
		JacksonAutoConfiguration.class, //
		DataSourceAutoConfiguration.class, //
		HttpMessageConvertersAutoConfiguration.class, //
		MetricRepositoryAutoConfiguration.class, //
		JmxAutoConfiguration.class, //
		SpringApplicationAdminJmxAutoConfiguration.class, //
		AopAutoConfiguration.class, //
		DataSourceTransactionManagerAutoConfiguration.class, //
		ConfigurationPropertiesAutoConfiguration.class, //
		HttpEncodingAutoConfiguration.class, //
		MetricExportAutoConfiguration.class, //
		MultipartAutoConfiguration.class })
@ComponentScan(basePackages = { "com.example.spring" }, excludeFilters = { @Filter(pattern = { "com.example.spring.*.model.*" }, type = FilterType.REGEX) })
public class Application {
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

	public Application() {
		LOGGER.debug("생성자 Application()");
	}

	public static void main(String[] args) throws IOException {
		LOGGER.debug("start");
		SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(Application.class);
		SpringApplication springApplication = springApplicationBuilder.build();
		springApplication.run();
	}
}
