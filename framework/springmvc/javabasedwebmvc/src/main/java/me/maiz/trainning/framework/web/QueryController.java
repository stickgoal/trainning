package me.maiz.trainning.framework.web;

import me.maiz.trainning.framework.common.KuaiDiQueryAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class QueryController {

    @Autowired
    private KuaiDiQueryAPI kuaiDiQueryAPI;

    // http://localhost:8080/kuaidi?expCode=ZTO&sn=546766706195
    @GetMapping("kuaidi")
    @ResponseBody
    public String query(String expCode,String sn) throws Exception {
        return kuaiDiQueryAPI.getOrderTracesByJson(expCode,sn);
    }




}
