package me.maiz.tool.customstartertest;

import me.maiz.tool.KuaiDiNiaoQueryAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private KuaiDiNiaoQueryAPI kuaiDiNiaoQueryAPI;

    //http://localhost:8080/query?expCode=ZTO&sn=546766706195
    @GetMapping("query")
    public String query(String expCode,String sn) throws Exception {
        return kuaiDiNiaoQueryAPI.getOrderTracesByJson(expCode,sn);
    }

}
