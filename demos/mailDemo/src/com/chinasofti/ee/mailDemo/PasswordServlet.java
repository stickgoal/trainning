package com.chinasofti.ee.mailDemo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.chinasofti.ee.mailDemo.service.MailService;
import com.chinasofti.ee.mailDemo.service.impl.MailServiceImpl;
import com.chinasofti.ee.mailDemo.util.RandomUtil;

/**
 * Servlet implementation class PasswordServlet
 */
@WebServlet("/password")
public class PasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private MailService mailService = new MailServiceImpl();
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String code = RandomUtil.randomString(4);
		mailService.sendResetPasswordMail("373034560@qq.com", code);
		request.getSession().setAttribute("resetPasswordValidationCode", code);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
