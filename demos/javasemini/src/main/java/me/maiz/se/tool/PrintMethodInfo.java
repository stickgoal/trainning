package me.maiz.se.tool;

import javafx.scene.control.TextField;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Scanner;

public class PrintMethodInfo {

    public static void main(String[] args) {

            Class<?> aClass = TextField.class;

            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method m : declaredMethods) {
                System.out.println( (Modifier.isPublic(m.getModifiers())?"public ":" ")+ m.getReturnType().getSimpleName()+" "+m.getName()+" ");
            }




    }



}
