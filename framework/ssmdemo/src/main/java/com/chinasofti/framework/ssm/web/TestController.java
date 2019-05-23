package com.chinasofti.framework.ssm.web;

import com.chinasofti.framework.ssm.dao.model.Movie;
import com.chinasofti.framework.ssm.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private static Logger logger = LoggerFactory.getLogger(MovieController.class);


    @Autowired
    private MovieService movieService;

    @RequestMapping("test")
    public String test(){
        try {
            movieService.test();
        }catch (Exception e){
            logger.error("ssss",e);
        }
        return "Y";
    }


    @RequestMapping("test2")
    public String test2(){
        try {
            movieService.test2();
        }catch (Exception e){
            logger.error("ssss",e);
        }
        return "Y";
    }

}
