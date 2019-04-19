package me.maiz.se.mini.deignpattern.adapter.interfaces;

public class Adapter implements HDMI{
    private USBTypeC typeC;
    public Adapter(USBTypeC typec){
        this.typeC =typec;
    }
    //转接方法
    public void inputHDMI(String data){
        typeC.inputUSBC(data);
    }
    public String outputHDMI(){
        System.out.println("HDMI转为TypeC");
        return typeC.outputUSBC();
    }
}
