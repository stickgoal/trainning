package me.maiz.framework.web;

import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {

    @RequestMapping("hello")
    public String hello(ModelMap modelMap){
        ImmutableList<String> list = ImmutableList.<String>of("Heelo", "Hlllo", "helo");
        modelMap.put("data","list");
        return "hello";
    }


}
