package com.chinasofti.ee;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class EchartsAjaxServlet
 */
@WebServlet("/echartsAjax")
public class EchartsAjaxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String direction = request.getParameter("direction");
		EChartsVO vo = new EChartsVO();
		if ("in".equals(direction)) {
			vo.setTitleText("资金收入情况");
			vo.setTitleSubText("月度统计");
			vo.setTitleX("center");
			vo.setSeriesName("收入");
			vo.addData("工资", 3000);
			vo.addData("兼职", 1000);
			vo.addData("其他", 700);
			vo.setLegendData(Arrays.asList("工资","兼职","其他"));
		}else{
			vo.setTitleText("资金支出情况");
			vo.setTitleSubText("月度统计");
			vo.setTitleX("center");
			vo.setSeriesName("支出");
			vo.addData("通讯", 200, "{a} <br/>{b} : {c} ({d}%) 200元");
			vo.addData("餐饮", 500, "{a} <br/>{b} : {c} ({d}%) 500元");
			vo.addData("房租", 700);
			vo.setLegendData(Arrays.asList("通讯","餐饮","房租"));

		}
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		String json = gson.toJson(vo);
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
