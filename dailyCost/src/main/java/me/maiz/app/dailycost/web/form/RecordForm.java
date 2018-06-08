package me.maiz.app.dailycost.web.form;

import me.maiz.app.dailycost.common.web.BaseForm;
import me.maiz.app.dailycost.enums.CategoryEnum;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Lucas on 2017-03-13.
 */
public class RecordForm extends BaseForm {
    @NotNull
    private double amount;

    @NotBlank
    private String category;

    private String direction;

    private String memo;

    private Date occurTime;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }

    @Override
    public String toString() {
        return "RecordForm{" +
                "amount=" + amount +
                ", category='" + category + '\'' +
                ", direction='" + direction + '\'' +
                ", memo='" + memo + '\'' +
                ", occurTime=" + occurTime +
                '}';
    }
}
