package me.maiz.demo.moderntech.thymeleaf;

import com.google.common.collect.Lists;
import me.maiz.demo.moderntech.thymeleaf.model.Book;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class PageController {

    @RequestMapping("page")
    public String page(Integer pageIdx, Integer pageSize, String keyword, ModelMap mm){
        List<Book> bookList = Lists.newArrayList();
        bookList.add(new Book(1,"aaa","bbb","ccc",20,30));
        bookList.add(new Book(2,"aaa1","bbb1","ccc1",22,40));
        mm.put("pageIdx",3);
        mm.put("totalPage",3);
        mm.put("books",bookList);
        mm.put("bookName","123");
        return "page";
    }

}
