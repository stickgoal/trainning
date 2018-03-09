package com.chinasofti.framework.dao.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Lucas on 2018-03-05.
 */
@Entity
@Table(name = "PO_BOOK")
@SequenceGenerator(name="idSeq" ,sequenceName = "id_Seq")
public class Book {
    @Id
//
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "idSeq")
    private long bookId;

    @Column(name = "book_name")
    private String bookName;

    private String isbn;

    @Column(name = "pub_time")
    private Date pubTime;

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Date getPubTime() {
        return pubTime;
    }

    public void setPubTime(Date pubTime) {
        this.pubTime = pubTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Book{");
        sb.append("bookId=").append(bookId);
        sb.append(", bookName='").append(bookName).append('\'');
        sb.append(", isbn='").append(isbn).append('\'');
        sb.append(", pubTime=").append(pubTime);
        sb.append('}');
        return sb.toString();
    }
}
