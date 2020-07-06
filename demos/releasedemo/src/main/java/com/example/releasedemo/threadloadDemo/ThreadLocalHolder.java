package com.example.releasedemo.threadloadDemo;

public class ThreadLocalHolder {

    private static ThreadLocal<String>  holder = new ThreadLocal<String>(){
        @Override
        protected String initialValue() {
            return "abc";
        }
    };

    public static String get(){
        return holder.get();
    }

    public static void set(String data){
        holder.set(data);
    }

}
