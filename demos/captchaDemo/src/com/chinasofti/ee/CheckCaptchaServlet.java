package com.chinasofti.ee;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CheckCaptchaServlet
 */
@WebServlet("/checkCaptcha")
public class CheckCaptchaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String captcha = request.getParameter("captcha");
		String sessionCaptcha = (String) request.getSession().getAttribute("sessionCaptcha");
		if(captcha.equals(sessionCaptcha)){
			//验证码正确
			response.getWriter().println("{\"success\":true}");
		}else{
			//不相等
			response.getWriter().println("{\"success\":false}");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
