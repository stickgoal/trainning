package com.chinasofti.framework.springmvcdemo.web;

import com.chinasofti.framework.springmvcdemo.web.form.RegForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Date;

/**
 * 数据绑定和转换
 * Created by Lucas on 2018-03-19.
 */
@Controller
public class DataBindingConversionController extends ControllerBase {


    //普通参数类型绑定
    //http://localhost:8080/springmvcdemo/param/common?name=Lucas&age=12
    @RequestMapping("common")
    public String common(String name, int age, ModelMap modelMap) {
        modelMap.put("name", name);
        modelMap.put("age", age);
        return "param";
    }

    //	http://localhost:8080/springmvcdemo/param/object?username=Lucas&age=12
    //	参数绑定到对象，并且自动存到modelmap中，key是类型的首字母小写
    @RequestMapping("object")
    public String object(RegForm regForm) {
        System.out.println(regForm);
        return "param";
    }

    // Date的绑定通过ControllerBase自定义
    //	http://localhost:8080/springmvcdemo/param/date?date=2017-08-01%2012:01:34
    @RequestMapping("date")
    public String date(Date date, ModelMap modelMap) {
        modelMap.put("date", date);
        return "param";
    }

    //通过JSR303验证数据，数据结果封装到bindingResult中
    @RequestMapping("validation")
    public String reg(@Valid RegForm regForm, BindingResult result){
        extractErrorMsg(result);

        return "result";
    }
}
