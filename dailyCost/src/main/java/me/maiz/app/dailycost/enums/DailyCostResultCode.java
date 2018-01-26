package me.maiz.app.dailycost.enums;

import me.maiz.app.dailycost.common.Messageable;

/**
 * Created by Lucas on 2017-02-20.
 */
public enum DailyCostResultCode  implements Messageable {

    SUCCESS("SUCCESS","处理成功"),

    DATA_ACCESS_FAIL("DATA_ACCESS_FAIL","数据访问失败"),

    USER_NOT_FOUND_OR_PASSWORD_ERROR("USER_NOT_FOUND_OR_PASSWORD_ERROR","用户不存在或者密码错误"),

    INVALID_ARGUMENT("INVALID_ARGUMENT","非法参数")

    ;

    private String code;

    private String message;

    DailyCostResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
