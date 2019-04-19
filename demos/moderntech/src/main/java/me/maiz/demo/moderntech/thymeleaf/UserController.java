package me.maiz.demo.moderntech.thymeleaf;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.maiz.demo.moderntech.thymeleaf.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class UserController {


    @RequestMapping("home")
    public String toHome(ModelMap mm){
        mm.put("user",new User("Lucas","http://maiz.me"));
        List<User> users = ImmutableList.of(new User("A","http://a.b.com"),new User("B","http://cc.com"),new User("C","http://aa.com"),new User("D","http://aa.ff.com"));
        mm.put("list",users);

        return "home";
    }

}
