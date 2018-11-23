package me.maiz.se.mini.deignpattern.adapter.interfaces;

public class MacUSBTypeC implements USBTypeC {
    private  String data;
    @Override
    public void inputUSBC(String data) {
        System.out.println("MAC USBTypeC 输入"+data);
        this.data=data;
    }

    @Override
    public String outputUSBC() {
        System.out.println("MAC USBTypeC 输出"+data);
        return data;
    }
}
