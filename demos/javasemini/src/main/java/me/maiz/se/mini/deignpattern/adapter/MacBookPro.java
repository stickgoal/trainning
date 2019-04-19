package me.maiz.se.mini.deignpattern.adapter;

import me.maiz.se.mini.deignpattern.adapter.interfaces.Adapter;
import me.maiz.se.mini.deignpattern.adapter.interfaces.MacUSBTypeC;

public class MacBookPro {

    private MacUSBTypeC usbTypeC = new MacUSBTypeC();

    void  projection(){
        usbTypeC.inputUSBC(" 清晰的画面");
        Projector projector = new Projector();
        projector.accept(new Adapter(usbTypeC));
        projector.display();
    }

}
