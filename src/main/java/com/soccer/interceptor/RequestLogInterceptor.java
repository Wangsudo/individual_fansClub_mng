package com.soccer.interceptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class RequestLogInterceptor implements HandlerInterceptor {

	private static final Logger LOG = LoggerFactory.getLogger(RequestLogInterceptor.class);

	private static ThreadLocal<Long> requestTime = new ThreadLocal<Long>();
	

	private static final String URI_REG = "(/web/.+)|(/xxt)";

	private static final  Pattern pattern = Pattern.compile(URI_REG);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		/*requestTime.set(System.currentTimeMillis());
		String requestUri = request.getRequestURI();
		LOG.info("请求开始: " + requestUri);
		Matcher matcher = pattern.matcher(requestUri);
		if(!matcher.find() ){
			LOG.error("restrict methods only!");
			HttpSession session = request.getSession();
			Long id =  (Long) session.getAttribute("admin");
			String sessionId = session.getId();
			ServletContext applicationCtx = session.getServletContext();
			if(id == null ||applicationCtx.getAttribute(id.toString()) == null ){
				//session 过期
				response.setStatus(901);
				return false;
			}else if(!sessionId.equals(applicationCtx.getAttribute(id.toString()))){
				//其它地方登陆
				response.setStatus(902);
				return false;
			}
		}*/
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}

}