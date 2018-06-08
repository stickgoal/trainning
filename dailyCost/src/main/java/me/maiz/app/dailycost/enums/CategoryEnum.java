package me.maiz.app.dailycost.enums;

import com.google.common.collect.Lists;
import me.maiz.app.dailycost.common.Messageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lucas on 2017-03-13.
 */
public enum CategoryEnum implements Messageable {

    TRANSPORTATION("TRANSPORTATION","OUT","交通"),
    EATING_OUT("EATING_OUT","OUT","外出用餐"),
    COMMODITY("COMMODITY","OUT","日用品"),
    RENT("RENT","OUT","房租"),
    OTHER("OTHER","OUT","其他"),
    SALARY("SALARY","IN","工资")
    ;

    private String code;

    private String direction;

    private String message;

    CategoryEnum(String code,String direction,String message){
        this.code=code;
        this.direction=direction;
        this.message=message;
    }


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public static List<CategoryEnum> getAllEnums(){
        List<CategoryEnum> allEnums = Lists.newArrayList();
        Collections.addAll(allEnums, values());
        return allEnums;
    }



}
