package me.maiz.se.mini.deignpattern.decorator;

/**
 * 风味奶茶
 */
public abstract class MilkTeaStylish implements MilkTea {

    private MilkTea milkTea;

    public MilkTeaStylish(MilkTea milkTea){
        this.milkTea=milkTea;
    }

    public void make(){
        this.milkTea.make();
    }


}
