package com.example.spring.config;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.example.spring.server.ReadThreadPool;
import com.example.spring.server.SessionCentext;
import com.example.spring.server.SessionChannelManager;

/**
 * @author gimbyeongsu
 * 
 */
@Configuration
@DependsOn(value = { "rootConfig", "serverConfig", "schedulingConfig" })
public class AfterConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(AfterConfig.class);

	@Autowired
	private Environment environment;
	@Autowired
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;
	@Autowired
	private ReadThreadPool readThreadPool;
	@Autowired
	private SessionChannelManager sessionChannelManager;

	public AfterConfig() {
		LOGGER.debug("생성자 AfterConfig()");
	}

	@Bean
	public Map<String, Object> info(ApplicationContext applicationContext) throws Exception {
		Map<String, Object> map = applicationContext.getBeansWithAnnotation(Configuration.class);
		for (String key : map.keySet()) {
			LOGGER.debug("{} {}", key, map.get(key));
		}
		return map;
	}

	@PostConstruct
	public void init() {
		LOGGER.debug("");
		readThreadPool.startPool();

		Runnable task = new Runnable() {

			@Override
			public void run() {
				LOGGER.debug("");
				ArrayList<SessionCentext> list = sessionChannelManager.getSessionList();
				for (SessionCentext each : list) {
					LOGGER.debug("{}", each);
				}
			}
		};

		threadPoolTaskScheduler.schedule(task, new CronTrigger("* * * * * ?"));
	}
}
