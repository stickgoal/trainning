package com.woniuxy.servelet02;

import cn.hutool.json.JSONUtil;
import com.woniuxy.servelet02.entity.Product;
import com.woniuxy.servelet02.mapper.ProductMapper;
import com.woniuxy.servelet02.util.MybatisUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/search")
public class ProductSearchServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("search");
        String keyword = req.getParameter("keyword");

        ProductMapper productMapper = MybatisUtil.getSession(true,this.getServletContext().getRealPath("/")).getMapper(ProductMapper.class);
        List<Product> all = productMapper.findAll(keyword);
        System.out.println(all);
        String jsonStr = JSONUtil.toJsonStr(all);
        System.out.println(jsonStr);
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
        resp.getWriter().write(jsonStr);
    }
}
