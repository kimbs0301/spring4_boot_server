package com.example.spring.config;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import com.google.common.collect.Lists;

/**
 * @author gimbyeongsu
 * 
 */
@Configuration
public class RootConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(RootConfig.class);

	@Autowired
	private Environment environment;

	public RootConfig() {
		LOGGER.debug("생성자 RootConfig()");
	}

	@Bean
	public static RequiredAnnotationBeanPostProcessor requiredAnnotationBeanPostProcessor() {
		return new RequiredAnnotationBeanPostProcessor();
	}

	// @Bean
	// public static PropertySource<?> yamlPropertySourceLoader() throws IOException {
	// YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
	// PropertySource<?> applicationYamlPropertySource = loader.load("application-local.yml", new ClassPathResource(
	// "application-local.yml"), "default");
	// return applicationYamlPropertySource;
	// }

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() throws IOException {
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		YamlPropertiesFactoryBean yamlProperties = new YamlPropertiesFactoryBean();
		yamlProperties.setResources(new ClassPathResource("application-junit.yml"));
		PropertiesFactoryBean properties = new PropertiesFactoryBean();
		properties.setLocations(new ClassPathResource("application-junit.properties"));
		propertySourcesPlaceholderConfigurer.setPropertiesArray(yamlProperties.getObject(), properties.getObject());
		return propertySourcesPlaceholderConfigurer;
	}

	@Bean(name = "configProperties")
	public PropertiesFactoryBean configProperties() throws IOException {
		PropertiesFactoryBean properties = new PropertiesFactoryBean();
		String[] activeProfiles = environment.getActiveProfiles();
		List<String> profiles = Lists.newArrayList(activeProfiles);
		ClassPathResource[] classPathResources = new ClassPathResource[profiles.size()];
		for (int i = 0; i < profiles.size(); ++i) {
			String profile = profiles.get(i);
			if ("junit".equals(profile) || "local".equals(profile)) {
				classPathResources[i] = new ClassPathResource("application-" + profile + ".properties");
			} else {
				classPathResources[i] = new ClassPathResource("config/application-" + profile + ".properties");
			}
		}
		properties.setLocations(classPathResources);

		YamlPropertiesFactoryBean yamlProperties = new YamlPropertiesFactoryBean();
		yamlProperties.setResources(new ClassPathResource("application-junit.yml"));
		PropertiesFactoryBean propertiesFactory = new PropertiesFactoryBean();
		propertiesFactory.setPropertiesArray(yamlProperties.getObject(), properties.getObject());
		return propertiesFactory;
	}
}
