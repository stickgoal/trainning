package me.maiz.framework.springboot.bootdemo.web;

import lombok.extern.slf4j.Slf4j;
import me.maiz.framework.springboot.bootdemo.dao.MovieMapper;
import me.maiz.framework.springboot.bootdemo.dao.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class HelloController {
    @Autowired
    private MovieMapper movieMapper;

    @RequestMapping("h")
    public String hello(ModelMap modelMap){
        Movie movie = movieMapper.selectById(5);
        log.info("查询结果：{}",movie);
        modelMap.addAttribute("title","欢迎您");
        modelMap.addAttribute("movie",movie);
        return "h";
    }


    @RequestMapping("j")
    @ResponseBody
    public Movie getJson(int movieId){
        Movie movie = movieMapper.selectById(movieId);
        return movie;
    }
}
