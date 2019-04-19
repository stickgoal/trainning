package me.maiz.trainning.web.form;

import java.util.Date;

public class BookQueryForm extends BaseQueryForm {

    private String bookName;

    private Date uptimeHigh;

    private Date uptimeLow;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public Date getUptimeHigh() {
        return uptimeHigh;
    }

    public void setUptimeHigh(Date uptimeHigh) {
        this.uptimeHigh = uptimeHigh;
    }

    public Date getUptimeLow() {
        return uptimeLow;
    }

    public void setUptimeLow(Date uptimeLow) {
        this.uptimeLow = uptimeLow;
    }

    @Override
    public String toString() {
        return "BookQueryForm{" +
                "bookName='" + bookName + '\'' +
                ", uptimeHigh=" + uptimeHigh +
                ", uptimeLow=" + uptimeLow +
                '}';
    }
}
