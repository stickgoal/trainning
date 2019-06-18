package me.maiz.se.mini.oo.weapon;

public interface AirWeapon extends AirCraft,Weapon {

    default void cancel(){
        System.out.println("取消攻击");
    }

}
