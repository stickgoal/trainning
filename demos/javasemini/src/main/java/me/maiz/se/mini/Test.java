package me.maiz.se.mini;

import me.maiz.se.mini.oo.weapon.AirCraft;
import me.maiz.se.mini.oo.weapon.Bomber;
import me.maiz.se.mini.oo.weapon.Missile;

import java.util.Arrays;
import java.util.Date;

public class Test {
    int x, y;
    Test(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public static void main(String[] args) {
        Test pt1, pt2;
        pt1 = new Test(3, 3);
        pt2 = new Test(4, 4);
        System.out.print(pt1.x + pt2.x);
    }
}



