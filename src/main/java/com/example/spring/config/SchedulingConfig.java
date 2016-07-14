package com.example.spring.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @author gimbyeongsu
 * 
 */
@Configuration
public class SchedulingConfig implements SchedulingConfigurer {
	private static final Logger LOGGER = LoggerFactory.getLogger(SchedulingConfig.class);

	@Autowired
	private Environment environment;

	public SchedulingConfig() {
		LOGGER.debug("생성자 SchedulingConfig()");
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		TaskScheduler scheduler = threadPoolTaskScheduler();
		taskRegistrar.setTaskScheduler(scheduler);
	}

	@Bean
	public ThreadPoolExecutor.CallerRunsPolicy rejectedExecutionHandler() {
		// new ThreadPoolExecutor.AbortPolicy();
		// new ThreadPoolExecutor.DiscardOldestPolicy();
		// new ThreadPoolExecutor.DiscardPolicy();
		return new ThreadPoolExecutor.CallerRunsPolicy();
	}

	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		CustomizableThreadFactory threadFactory = new CustomizableThreadFactory("batch-");
		threadFactory.setThreadPriority(Thread.NORM_PRIORITY);
		threadFactory.setDaemon(false);
		scheduler.setThreadFactory(threadFactory);
		scheduler.setPoolSize(environment.getRequiredProperty("batch.pool.size", Integer.class));
		scheduler.setAwaitTerminationSeconds(3);
		scheduler.setWaitForTasksToCompleteOnShutdown(true);
		scheduler.setRejectedExecutionHandler(rejectedExecutionHandler());
		return scheduler;
	}
}