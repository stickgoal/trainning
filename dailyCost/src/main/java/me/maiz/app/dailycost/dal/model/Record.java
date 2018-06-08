package me.maiz.app.dailycost.dal.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Lucas on 2017-03-13.
 */
@Entity
@Table(name="dc_record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private int recordId;

    private int userId;

    private Date keepTime;

    private double amount;

    private String direction;

    private String category;

    private String memo;

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getKeepTime() {
        return keepTime;
    }

    public void setKeepTime(Date keepTime) {
        this.keepTime = keepTime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return "Record{" +
                "recordId=" + recordId +
                ", userId=" + userId +
                ", keepTime=" + keepTime +
                ", amount=" + amount +
                ", direction='" + direction + '\'' +
                ", category='" + category + '\'' +
                ", memo='" + memo + '\'' +
                '}';
    }
}
