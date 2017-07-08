package com.sunflower.listener;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		Enumeration<String> parameterNames = ctx.getInitParameterNames();
		while(parameterNames.hasMoreElements()) {
			System.out.println(parameterNames.nextElement());
		}
		System.out.println("ServletContextListener started");

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("ServletContextListener destroyed");
	}

}
