package me.maiz.se.mini.deignpattern.adapter;

import me.maiz.se.mini.deignpattern.adapter.interfaces.HDMI;

public class Projector {

    private HDMI hdmi;

    void accept(HDMI hdmi){
        this.hdmi=hdmi;
    }

    void display(){
        System.out.println("投影仪输出 : "+hdmi.outputHDMI());
    }

}
