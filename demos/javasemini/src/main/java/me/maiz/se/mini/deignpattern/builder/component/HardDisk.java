package me.maiz.se.mini.deignpattern.builder.component;

public class HardDisk implements Component {

    private int capacity;

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "HardDisk{" +
                "capacity=" + capacity +
                '}';
    }
}
