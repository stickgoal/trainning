package me.maiz.app.dailycost.common.validation;

import com.google.common.collect.Maps;
import me.maiz.app.dailycost.common.web.BaseForm;
import me.maiz.app.dailycost.enums.DailyCostResultCode;
import me.maiz.app.dailycost.exception.AppException;
import me.maiz.app.dailycost.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import java.util.Map;
import java.util.Set;

/**
 * Created by Lucas on 2017-03-03.
 */
public class ValidateUtil {

    /**
     * 验证Form
     *
     * @param form
     * @return
     */
    public static void validate(BaseForm form) {
        Map<String, String> result = Maps.newHashMap();
        Set<ConstraintViolation<BaseForm>> constraintViolations = DCValidatorFactory.getValidator().validate(form);
        if(!constraintViolations.isEmpty()) {
            for (ConstraintViolation v : constraintViolations) {
                result.put(v.getPropertyPath().toString(), v.getMessage());
            }
            throw new ValidationException(result);
        }
    }

}
