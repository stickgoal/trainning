package me.maiz.trainning.web;

import me.maiz.trainning.common.Result;
import me.maiz.trainning.dal.model.Book;
import me.maiz.trainning.service.BookService;
import me.maiz.trainning.web.form.BookForm;
import me.maiz.trainning.web.form.BookQueryForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("book")
public class BookController extends  BaseController{

    private Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private BookService bookService;

    @RequestMapping("list")
    public String query(BookQueryForm form,ModelMap modelMap){
        int bookCount = bookService.countBook(form.getBookName(),form.getUptimeHigh(),form.getUptimeLow());
        int pageCount = bookCount%form.getPageSize()==0?bookCount/form.getPageSize():bookCount/form.getPageSize()+1;
        modelMap.addAttribute("pageCount",pageCount);

        List<Book> books = bookService.queryBook(form.getBookName(),form.getUptimeHigh(),form.getUptimeLow(),form.getStart(),form.getPageSize());
        modelMap.addAttribute("books",books);
        return "book/list";
    }

    @RequestMapping(value="add",method = RequestMethod.GET)
    public String toAdd(){

        return "book/add";
    }

    @RequestMapping(value="add",method = RequestMethod.POST)
    public String add(BookForm bookForm){
        Book book = new Book();
        book.setStatus("ON");
        book.setCreateDate(new Date());
        if(bookForm.isUp()) {
            book.setUpTime(new Date());
        }
        BeanUtils.copyProperties(bookForm,book);
        book.setPrice(new BigDecimal(bookForm.getPrice()));
        bookService.add(book);
        return "redirect:list";
    }


    @RequestMapping("delete")
    @ResponseBody
    public Result delete(int bookId){
        try {
            bookService.deleteBook(bookId);
        }catch (Exception e){
            logger.error("删除出错",e);
            return Result.fail("删除出错");
        }
        return Result.success();
    }

    @RequestMapping(value="edit",method = RequestMethod.GET)
    public String toEdit(int bookId,ModelMap modelMap){
        Book book = bookService.loadBook(bookId);
        modelMap.put("book",book);
        return "book/edit";
    }

    @RequestMapping(value="edit",method = RequestMethod.POST)
    public String edit(BookForm bookForm){
        Book book = new Book();
        BeanUtils.copyProperties(bookForm,book);
        book.setId(bookForm.getBookId());
        book.setPrice(new BigDecimal(bookForm.getPrice()));
        bookService.updateBook(book);
        return "redirect:list";
    }



}
