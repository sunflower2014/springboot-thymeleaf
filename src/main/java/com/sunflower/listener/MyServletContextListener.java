package com.sunflower.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("ServletContextListener started");

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("ServletContextListener destroyed");
	}

}
