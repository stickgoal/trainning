package com.chinasofti.ee;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CaptchaServlet
 */
@WebServlet("/captchaCreate")
public class CaptchaCreateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 生成随机验证码
		String captchaCode = RandomUtils.randomString(4);

		// 将验证码输出成图片
		BufferedImage img = createCaptchaImg(captchaCode);

		// 放入session便于后续校验
		request.getSession().setAttribute("sessionCaptcha", captchaCode);

		// 写入响应
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		ImageIO.write(img, "PNG", response.getOutputStream());

	}

	private BufferedImage createCaptchaImg(String captchaCode) {

		int width = 80, height = 30;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		// 设定背景色
		g.setColor(getRandColor(230, 255));
		g.fillRect(0, 0, 100, 30);

		g.setFont(new Font("Arial", Font.ITALIC, 20));
		for (int i = 0; i < captchaCode.length(); i++) {
			// 设定字符颜色
			g.setColor(getRandColor(100, 150));
			// 画字符
			g.drawString("" + captchaCode.charAt(i), 18 * i + 10, 20);
		}

		// 画干扰线
		Random random = new Random();
		for (int i = 0; i < (random.nextInt(5) + 5); i++) {
			g.setColor(new Color(random.nextInt(255) + 1, random.nextInt(255) + 1, random.nextInt(255) + 1));
			g.drawLine(random.nextInt(100), random.nextInt(30), random.nextInt(100), random.nextInt(30));
		}

		g.dispose();
		return image;
	}

	/**
	 * 给定范围获得随机颜色
	 * 
	 * @param fc
	 * @param bc
	 * @return
	 */
	private Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
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
