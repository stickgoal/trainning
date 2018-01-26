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

    TRANSPORTATION("TRANSPORTATION","交通"),
    EATING_OUT("EATING_OUT","外出用餐"),
    COMMODITY("COMMODITY","日用品"),
    RENT("RENT","房租"),
    OTHER("OTHER","其他")

    ;

    private String code;

    private String message;

    CategoryEnum(String code,String message){
        this.code=code;
        this.message=message;
    }


    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }

    public static List<CategoryEnum> getAllEnums(){
        List<CategoryEnum> allEnums = Lists.newArrayList();
        Collections.addAll(allEnums, values());
        return allEnums;
    }



}
