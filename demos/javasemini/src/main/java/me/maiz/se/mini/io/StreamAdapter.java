package me.maiz.se.mini.io;

import java.io.*;

public class StreamAdapter {
    public static void main(String[] args) {
        try {
            //将FileInputStream转换为Reader
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("d:\\tmp\\HelloWorld.java")));
            //将FileOutputStream转换为Writer
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("d:\\tmp\\ABC.java")));
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
