package me.maiz.app.dailycost.exception;

import com.google.common.collect.Maps;
import me.maiz.app.dailycost.enums.DailyCostResultCode;

import java.util.Map;

/**
 * Created by Lucas on 2017-03-03.
 */
public class ValidationException extends AppException {

    /**
     * 校验出错记录
     */
    private Map<String,String> violations = Maps.newHashMap();

    public void addViolation(String path,String msg){
        violations.put(path,msg);
    }

    public ValidationException(String path,String message) {
        super(DailyCostResultCode.INVALID_ARGUMENT);
        addViolation(path,message);
    }

    public ValidationException(Map<String,String> violations) {
        super(DailyCostResultCode.INVALID_ARGUMENT);
        this.violations.putAll(violations);
    }

    @Override
    public String getMessage() {
        return violations.toString();
    }
}

