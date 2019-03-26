package me.maiz.se.mini.io;

import java.io.*;

public class BufferedFileCopy {
    public static final String INPUT_FILE_PATH="d:\\tmp\\HelloWorld.java";
    public static final String OUTPUT_FILE_PATH="d:\\tmp\\HelloWorldaaa.java";

    public static void main(String[] args) {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try{
            reader = new BufferedReader(new FileReader(INPUT_FILE_PATH));
            writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH));
            String line =null;
            int num = 1;
            while((line = reader.readLine())!=null){
                System.out.println(line);
                writer.write(num+" : "+line);
                writer.newLine();
                num++;
            }
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //finally关闭流处理，确保数据流任何情况下都会关闭
            //避免内存泄漏
            try {
                reader.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}
