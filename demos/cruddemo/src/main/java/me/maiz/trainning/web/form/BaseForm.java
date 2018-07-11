package me.maiz.trainning.web.form;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class BaseForm {
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
