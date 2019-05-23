package com.chinasofti.framework.ssm.web;

import com.chinasofti.framework.ssm.dao.model.Movie;
import com.chinasofti.framework.ssm.service.MovieService;
import com.chinasofti.framework.ssm.service.info.MovieInfo;
import com.chinasofti.framework.ssm.web.form.MovieForm;
import com.chinasofti.framework.ssm.web.form.MovieQueryForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Controller
public class MovieController {

    private static Logger  logger = LoggerFactory.getLogger(MovieController.class);

    @Autowired
    private MovieService movieService;

    @RequestMapping("movieList")
    public String movieList(MovieQueryForm form,String message, ModelMap modelMap){
        logger.debug(message);
        logger.info("查询电影，参数为{}",form);
        try {
            List<Movie> movieList = movieService.search(form.getDirector(), form.getProtagonist(), form.getReleaseDateBegin(), form.getReleaseDateEnd());
            modelMap.addAttribute("movieList", movieList);
            modelMap.addAttribute("message", message);
        }catch(Exception e){
            logger.info("异常",e);
        }
        return "movie/list";
    }

    @RequestMapping(value="detail",method = RequestMethod.GET)
    public String detail(int movieId,ModelMap modelMap){
        Movie movie = movieService.load(movieId);
        modelMap.addAttribute("movie",movie);
        return "movie/detail";
    }

    @RequestMapping(value="movieAdd",method = RequestMethod.GET)
    public String movieAdd(ModelMap modelMap){
        return "movie/add";
    }

    @RequestMapping(value="movieAdd",method = RequestMethod.POST)
    public String doMovieAdd(MovieForm form, ModelMap modelMap) throws UnsupportedEncodingException {
        MovieInfo info = new MovieInfo();
        BeanUtils.copyProperties(form,info);
        info.setDirect(form.getDirector());
        movieService.add(info);
        return redirectToList("添加成功");
    }

    @RequestMapping(value="modifyMovie",method = RequestMethod.GET)
    public String modifyMovie(int movieId,ModelMap modelMap){
        Movie movie = movieService.load(movieId);
        modelMap.addAttribute("movie",movie);
        return "movie/modify";
    }

    @RequestMapping(value="modifyMovie",method = RequestMethod.POST)
    public String doModifyMovie(MovieForm form,ModelMap modelMap){
        MovieInfo info = new MovieInfo();
        BeanUtils.copyProperties(form,info);
        info.setDirect(form.getDirector());
        movieService.modify(info);
        return redirectToList("修改电影["+form.getMovieName()+"]成功");
    }


    @RequestMapping("delMovie")
    public String deleteMovie(int movieId){
        movieService.delete(movieId);
        return redirectToList("删除成功");
    }

    private String redirectToList(String message){
        String result =null;
        try {
            result ="redirect:movieList?message="+ URLEncoder.encode(message,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

}
