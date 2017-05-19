package com.chinasofti.se.bookstore;


import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lucas on 2017-03-30.
 */
public class FileOperationImpl implements FileOperation {

    private static final String STORAGE_PATH="C:/tmp/";
    public static final String FIELD_SEPERATOR="_";

    public void save(String filename,List<Book> books){
        save(filename,books,false);
    }

    @Override
    public void initSave(String filename, List<Book> books) {
        save(filename,books,true);
    }

    private void save(String filename, List<Book> books, boolean init) {
        if(books==null||books.isEmpty()){
            throw new IllegalArgumentException("对象不能为空");
        }

        BufferedWriter bufferedWriter = null;
        try {
            File file = new File(STORAGE_PATH+filename);
            if(!file.exists()){
                file.createNewFile();
            }
            bufferedWriter = new BufferedWriter(new FileWriter(file,!init));
            for(Book b: books) {
                bufferedWriter.write(serialize(b));
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(bufferedWriter!=null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String serialize(Book b) {
        return b.getName()+FIELD_SEPERATOR+b.getISBN()+FIELD_SEPERATOR+b.getPrice().toPlainString()+FIELD_SEPERATOR+b.getStock();
    }

    public List<Book> load(String filename) {
        List<Book> objects = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(STORAGE_PATH+filename));
            String line = null;
            while((line=bufferedReader.readLine())!=null){
                if(line!=null&& !line.isEmpty())
                objects.add(deserialize(line));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return objects;
    }

    private Book deserialize(String line) {
        String[] fields = line.split(FIELD_SEPERATOR);
        Book book = new Book();
        book.setName(fields[0]);
        book.setISBN(fields[1]);
        book.setPrice(new BigDecimal(fields[2]));
        book.setStock(Integer.parseInt(fields[3]));
        return book;
    }

}
