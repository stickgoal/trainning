package me.maiz.app.dailycost.web;

import me.maiz.app.dailycost.common.Constants;
import me.maiz.app.dailycost.dal.model.Record;
import me.maiz.app.dailycost.dal.model.User;
import me.maiz.app.dailycost.service.CategoryService;
import me.maiz.app.dailycost.service.RecordService;
import me.maiz.app.dailycost.web.form.RecordForm;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * Created by Lucas on 2017-03-13.
 */
@Controller
public class RecordController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RecordService recordService;

    @RequestMapping("account")
    public String toAccount(ModelMap modelMap, HttpSession session) {
        User user = (User)session.getAttribute(Constants.USER_SESSION_KEY);
        modelMap.put("categories",categoryService.prepareCategories(user.getUserId()));
        return "account";
    }

    @RequestMapping("keep")
    public String keepAccount(RecordForm form,HttpSession session){

        Record r = new Record();
        BeanUtils.copyProperties(form,r);
        r.setKeepTime(new Date());
        r.setUserId(getUserId(session));
        recordService.save(r);

        return "redirect:account";
    }



}
