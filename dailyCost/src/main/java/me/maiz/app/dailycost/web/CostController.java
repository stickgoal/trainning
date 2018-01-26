package me.maiz.app.dailycost.web;

import me.maiz.app.dailycost.enums.CategoryEnum;
import me.maiz.app.dailycost.web.form.CostForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Lucas on 2017-03-13.
 */
@Controller
@RequestMapping("account")
public class CostController extends BaseController {

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String toAccount(ModelMap modelMap) {
        modelMap.put("categoryList", CategoryEnum.getAllEnums());
        return "account";
    }

    @RequestMapping("keep")
    public String keepAccount(CostForm form){


        return "";
    }



}
