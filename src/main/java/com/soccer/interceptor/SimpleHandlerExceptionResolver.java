package com.soccer.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class SimpleHandlerExceptionResolver extends
		SimpleMappingExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request,HttpServletResponse response, Object handler, Exception ex) {
		/**
		ModelAndView mv = new ModelAndView();
		// 设置状态码,注意这里不能设置成500，设成500JQuery不会出错误提示     
		//并且不会有任何反应 
		
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache,must-revalidate");
			try {
				ex.getMessage();
				PrintWriter writer = response.getWriter();
				JSONObject ret = new JSONObject();
				if(ex instanceof MaxUploadSizeExceededException){
					ret.put("message", "Maximum_upload_size_exceeded");
					writer.print(JSONObject.toJSONString(ret));
				}
			//	writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		    return mv;
		    */
		if(ex instanceof MaxUploadSizeExceededException){  
            return new ModelAndView("error_fileupload");  
        }
        return null;  
	}
}
