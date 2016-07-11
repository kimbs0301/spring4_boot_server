package com.example.daemon;

//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRegistration;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.actuate.autoconfigure.CrshAutoConfiguration;
//import org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration;
//import org.springframework.boot.actuate.autoconfigure.EndpointMBeanExportAutoConfiguration;
//import org.springframework.boot.context.embedded.ServletContextInitializer;
//import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
//import org.springframework.web.servlet.DispatcherServlet;
//
//import com.example.spring.config.AfterConfig;
//import com.example.spring.config.RootConfig;
//import com.example.spring.config.ServerConfig;
//import com.example.spring.config.WebAppContextConfig;

/**
 * @author gimbyeongsu
 * 
 */
public class EmbeddedWebInitalizer /**implements ServletContextInitializer**/ {
//	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedWebInitalizer.class);
//
//	public EmbeddedWebInitalizer() {
//		LOGGER.debug("생성자 EmbeddedWebInitalizer()");
//	}
//
//	@Override
//	public void onStartup(ServletContext container) throws ServletException {
//		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
//
//		context.register(WebAppContextConfig.class);
////		context.register(WebAppContextConfig.class, RootConfig.class, ServerConfig.class, CrshAutoConfiguration.class,
////				EndpointAutoConfiguration.class, EndpointMBeanExportAutoConfiguration.class, AfterConfig.class);
//		context.setServletContext(container);
//
//		DispatcherServlet dispatcher = new DispatcherServlet(context);
//		ServletRegistration.Dynamic servlet = container.addServlet("dispatcher", dispatcher);
//
//		servlet.setLoadOnStartup(1);
//		servlet.addMapping("/");
//	}
}