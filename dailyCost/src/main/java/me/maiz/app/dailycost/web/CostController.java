package me.maiz.app.dailycost.web;

import me.maiz.app.dailycost.common.Constants;
import me.maiz.app.dailycost.dal.model.User;
import me.maiz.app.dailycost.enums.CategoryEnum;
import me.maiz.app.dailycost.service.CategoryService;
import me.maiz.app.dailycost.web.form.CostForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * Created by Lucas on 2017-03-13.
 */
@Controller
public class CostController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("account")
    public String toAccount(ModelMap modelMap, HttpSession session) {
        User user = (User)session.getAttribute(Constants.USER_SESSION_KEY);
        modelMap.put("categories",categoryService.prepareCategories(user.getUserId()));
        return "account";
    }

    @RequestMapping("keep")
    public String keepAccount(CostForm form){


        return "";
    }



}
