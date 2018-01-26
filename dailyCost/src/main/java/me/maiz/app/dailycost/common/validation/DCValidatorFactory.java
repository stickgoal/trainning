package me.maiz.app.dailycost.common.validation;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Created by Lucas on 2017-03-03.
 */
public class DCValidatorFactory {

    private static final Validator validator ;

    static{
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private DCValidatorFactory(){
    }

    public static Validator getValidator() {
        return validator;
    }
}
