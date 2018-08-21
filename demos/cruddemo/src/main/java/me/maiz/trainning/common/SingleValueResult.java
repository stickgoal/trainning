package me.maiz.trainning.common;

public class SingleValueResult<T> extends Result {

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
