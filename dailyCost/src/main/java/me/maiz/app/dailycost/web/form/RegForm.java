package me.maiz.app.dailycost.web.form;

import me.maiz.app.dailycost.common.web.BaseForm;
import me.maiz.app.dailycost.exception.ValidationException;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Lucas on 2017-03-08.
 */
public class RegForm extends BaseForm {

    @NotBlank
    @Length(max = 256,min=6)
    private String username;

    @NotBlank
    @Length(max = 256,min=6)
    private String password;

    @NotBlank
    @Length(max = 256,min=6)
    private String passwordConfirm;

    @Email
    @Length(max=512,min = 6)
    private String email;

    @Override
    protected void validateMore() {
        if(!(password!=null&& password.equals(passwordConfirm))){
            throw new ValidationException("password,passwordConfirm","两次密码不相等");
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
