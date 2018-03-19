package com.chinasofti.framework.springmvcdemo.web;

import com.chinasofti.framework.springmvcdemo.web.form.RegForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by Lucas on 2018-03-19.
 */
@Controller
public class JSONController {

    //	直接返回json
    @RequestMapping("/json")
    @ResponseBody
    public Map<String,Object> getJson(){
        Map<String,Object> map = new HashMap<>();
        map.put("a",12);
        map.put("b", new Date());
        RegForm rf = new RegForm();
        rf.setAge(12);
        rf.setPassword("xxxxasdfasdf");
        rf.setUsername("asdfasdf@abc.com");
        map.put("c", rf);
        List<RegForm> form = Arrays.asList(rf);
        map.put("d", form);
        return map;
    }
}
