package com.chinasofti.framework.ssm.web.advice;

import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.number.AbstractNumberFormatter;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@ControllerAdvice
public class FormatterAdvice {

    @InitBinder
    public void bind(WebDataBinder binder){
        binder.addCustomFormatter(new DateFormatter("yyyy-MM-dd"));
        binder.addCustomFormatter(new NumberStyleFormatter() {
            @Override
            public Number parse(String text, Locale locale) throws ParseException {
                System.out.println("parse text:"+text);
                if(text==null||text.length()==0||text.trim().length()==0){
                    return 0;
                }

                return super.parse(text, locale);
            }


        });
    }

}
