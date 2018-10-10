package me.maiz.trainning.demo.helloboot.web;

import me.maiz.trainning.demo.helloboot.dal.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HelloController {

    @Autowired
    private BookMapper bookMapper;


    @RequestMapping("hello")
    @ResponseBody
    public Map<String,Object> sayHello(int id){
        Map<String,Object> data = new HashMap<>();
        data.put("hello","boot");
        data.put("book",bookMapper.findById(id));
        return data;
    }

    @RequestMapping("hello2")
    public String sayHello2(ModelMap modelMap){
        modelMap.put("hello","boot");
        return "hello";
    }

}
