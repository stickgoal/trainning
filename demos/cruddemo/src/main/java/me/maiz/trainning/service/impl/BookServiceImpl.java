package me.maiz.trainning.service.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import me.maiz.trainning.dal.mapper.BookMapper;
import me.maiz.trainning.dal.mapper.BookMapperCustom;
import me.maiz.trainning.dal.model.Book;
import me.maiz.trainning.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("bookService")
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapperCustom bookMapperCustom;

    @Autowired
    private BookMapper bookMapper;

    @Override
    public List<Book> queryBook(String bookName, Date upTimeMax, Date upTimeMin, int start, int pageSize) {
        Map<String, Object> params = getStringObjectMap(bookName, upTimeMax);
        start = start==0?1:start;
        params.put("start",(start-1)*pageSize);
        params.put("pageSize",pageSize);
        List<Book> books = bookMapperCustom.querySelective(params);
        return books;
    }

    @Override
    public int countBook(String bookName, Date upTimeMax, Date upTimeMin) {
        Map<String, Object> params = getStringObjectMap(bookName, upTimeMax);

        return bookMapperCustom.countSelective(params);
    }

    private Map<String, Object> getStringObjectMap(String bookName, Date upTimeMax) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("bookName",bookName);
        params.put("upTimeHigh",upTimeMax);

        return params;
    }

    @Override
    public void deleteBook(int bookId) {
        bookMapper.deleteByPrimaryKey(bookId);
    }

    @Override
    public void add(Book book) {
        bookMapper.insertSelective(book);
    }

    @Override
    public Book loadBook(int bookId) {
        return bookMapper.selectByPrimaryKey(bookId);
    }

    @Override
    public void updateBook(Book book) {
        bookMapper.updateByPrimaryKeySelective(book);
    }

}
