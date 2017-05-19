package com.chinasofti.se.bookstore;

import java.util.List;

/**
 * Created by Lucas on 2017-03-30.
 */
public interface FileOperation {

    /**
     * 保存内容到指定文件
     * @param filename
     * @param books
     */
    void save(String filename, List<Book> books);

    /**
     * 保存内容到文件，初始化时使用
     * @param filename
     * @param books
     */
    void initSave(String filename, List<Book> books);

    /**
     * 从文件中加载内容
     * @param filename
     */
    List<Book> load(String filename);
}
