package com.sunflower.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfigServlet extends HttpServlet {

	private static final long serialVersionUID = -4643337988926519418L;
	
	
	@Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
	private void getContextParams() {
		ServletContext servletContext = this.getServletContext();
	}
}
