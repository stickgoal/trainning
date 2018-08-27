package com.chinasofti.ee;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class EchartsServlet
 */
@WebServlet("/echarts")
public class EchartsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		EChartsVO vo = new EChartsVO();
		vo.setTitleText("资金支出情况");
		vo.setTitleSubText("月度统计");
		vo.setTitleX("center");
		vo.addData("通讯", 200, "{a} <br/>{b} : {c} ({d}%) "+200+"元");
		vo.addData("餐饮", 500, "{a} <br/>{b} : {c} ({d}%) 500元");
		vo.addData("房租", 700);
		vo.setSeriesName("支出");
		vo.setLegendData(Arrays.asList("通讯","餐饮","房租"));
		
		request.setAttribute("echartVO", vo);
		request.getRequestDispatcher("echarts.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
