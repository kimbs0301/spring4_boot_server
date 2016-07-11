package com.example.daemon;

//import io.undertow.Undertow.Builder;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.autoconfigure.AutoConfigureOrder;
//import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration.EmbeddedServletContainerCustomizerBeanPostProcessorRegistrar;
//import org.springframework.boot.context.embedded.ServletContextInitializer;
//import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer;
//import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.core.Ordered;
//
//@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
//@Configuration
//@Import(EmbeddedServletContainerCustomizerBeanPostProcessorRegistrar.class)
public class UndertowEmbeddedConfig {
//	private static final Logger LOGGER = LoggerFactory.getLogger(DaemonConfig.class);
//	@Bean
//	public UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory() {
//		UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory();
//		factory.addBuilderCustomizers(new UndertowBuilderCustomizer() {
//
//			@Override
//			public void customize(Builder builder) {
//				builder.addHttpListener(8080, "127.0.0.1");
//			}
//		});
//		
//		List<ServletContextInitializer> servletContextInitializers = new ArrayList<>();
//		servletContextInitializers.add(new EmbeddedWebInitalizer());
//		factory.setInitializers(servletContextInitializers);
//		
//		return factory;
//	}
}
