package com.soccer.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminInterceptor implements HandlerInterceptor {

	private static final Logger LOG = LoggerFactory.getLogger(AdminInterceptor.class);

	private static ThreadLocal<Long> requestTime = new ThreadLocal<Long>();
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		requestTime.set(System.currentTimeMillis());
		String requestUri = request.getRequestURI();
		LOG.info("请求开始: " + requestUri);
		HttpSession session = request.getSession();
		Long id =  (Long) session.getAttribute("admin");
		String sessionId = session.getId();
		ServletContext applicationCtx = session.getServletContext();
		if(id == null ||!sessionId.equals(applicationCtx.getAttribute(id.toString())) ){
			request.getRequestDispatcher("/admin/login.html").forward(request, response);
			return false;
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		LOG.info("请求结束: " + (System.currentTimeMillis() - requestTime.get()) + "ms");
	}


}