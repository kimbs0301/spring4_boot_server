package com.example.junit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;

/**
 * @author gimbyeongsu
 * 
 */
@Profile({ "junit" })
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
public class JunitConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(JunitConfig.class);

	public JunitConfig() {
		LOGGER.debug("생성자 JunitConfig()");
	}
}
