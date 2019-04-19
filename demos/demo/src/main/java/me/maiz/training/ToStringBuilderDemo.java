package me.maiz.training;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by Lucas on 2017-08-02.
 */
public class ToStringBuilderDemo {
    public static void main(String[] args) {
        ToStringBuilderDemo t = new ToStringBuilderDemo();
        t.name="xxx";
        t.age=12;
        t.address="sdfasdf";
        System.out.println(t);
    }
   private  String  name ;

    private  int age;

    private  String  address;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
