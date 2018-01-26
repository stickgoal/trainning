package me.maiz.app.dailycost.exception;

import me.maiz.app.dailycost.enums.DailyCostResultCode;

/**
 * Created by Lucas on 2017-02-20.
 */
public class AppException extends RuntimeException {

    private DailyCostResultCode resultCode;


    public AppException() {
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(DailyCostResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode=resultCode;
    }

    public AppException(DailyCostResultCode resultCode,String message) {
        super(message);
        this.resultCode=resultCode;
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public AppException(DailyCostResultCode resultCode,String message, Throwable cause) {
        super(message, cause);
        this.resultCode=resultCode;
    }

    public AppException(Throwable cause) {
        super(cause);
    }

    public AppException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public DailyCostResultCode getResultCode() {
        return resultCode;
    }

}
