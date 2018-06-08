package me.maiz.app.dailycost.web;

import me.maiz.app.dailycost.dal.model.Record;
import me.maiz.app.dailycost.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class QueryController  extends BaseController {

    @Autowired
    private RecordService recordService;

    @RequestMapping("list")
    public String list(String begin,String end,String direction){

       List<Record> recordList =  recordService.queryList(begin,end,direction);

       return "";

    }



}
