package com.soccer.interceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class InitServlet extends HttpServlet{
	//应用根目录
	public static String APPLICATION_URL;
	
	@Override
	public void init() throws ServletException {
		super.init();
		APPLICATION_URL = getServletContext().getRealPath("/");
	}
	
}
