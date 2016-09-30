package com.s;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class Log4jlistener implements ServletContextListener {
	public static final String log4jdirkey = "log4jdir";
	public void contextDestroyed(ServletContextEvent servletcontextevent) {
		System.getProperties().remove(log4jdirkey);
	}
	public void contextInitialized(ServletContextEvent servletcontextevent) {
	String log4jdir = servletcontextevent.getServletContext().getRealPath("/");
	System.setProperty(log4jdirkey, log4jdir);
	}
}