package me.maiz.se.mini.oo.device;

/**
 * 扫地机器人
 */
public class FloorSweepingRobot extends Device {
    /*
     清扫模式：扫地、拖地
     */
    public String sweepingMode;

    public void sweep(){
        System.out.println("扫地");
    }

}
