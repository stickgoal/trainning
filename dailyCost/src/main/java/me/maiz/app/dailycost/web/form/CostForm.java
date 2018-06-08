package me.maiz.app.dailycost.web.form;

import me.maiz.app.dailycost.common.web.BaseForm;
import me.maiz.app.dailycost.enums.CategoryEnum;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by Lucas on 2017-03-13.
 */
public class CostForm extends BaseForm {
    @NotNull
    private BigDecimal account;

    @NotBlank
    private String category;

    private String memo;

    public BigDecimal getAccount() {
        return account;
    }

    public void setAccount(BigDecimal account) {
        this.account = account;
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
}
