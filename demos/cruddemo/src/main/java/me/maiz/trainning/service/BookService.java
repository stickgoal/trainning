package me.maiz.trainning.service;

import me.maiz.trainning.dal.model.Book;

import java.util.Date;
import java.util.List;

public interface BookService {

    List<Book> queryBook(String bookName, Date upTimeMax,Date upTimeMin, int start, int pageSize);

    int countBook(String bookName, Date upTimeMax,Date upTimeMin);

    void deleteBook(int bookId);

    void add(Book book);

    Book loadBook(int bookId);

    void updateBook(Book book);
}
