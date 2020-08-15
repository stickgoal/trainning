package me.maiz.trainning.framework.common;

public enum ShipperEnum {

    SF("顺丰速运","SF"),
    HTKY("百世快递","HTKY"),
    ZTO("中通快递","ZTO"),
    STO("申通快递","STO"),
    YTO("圆通速递","YTO"),
    YD("韵达速递","YD"),
    YZPY("邮政快递包裹","YZPY"),
    EMS("EMS","EMS"),
    HHTT("天天快递","HHTT"),
    JD("京东快递","JD"),
    UC("优速快递","UC"),
    DBL("德邦快递","DBL"),
    ZJS("宅急送","ZJS");

    private String name;
    private String code;

    ShipperEnum(String name,String code){
        this.name=name;
        this.code=code;
    }

}
