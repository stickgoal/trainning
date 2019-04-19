package me.maiz.se.mini.io;

import java.io.*;

public class CharFileCopy {
    public static final String INPUT_FILE_PATH="d:\\tmp\\HelloWorld.java";
    public static final String OUTPUT_FILE_PATH="d:\\tmp\\HelloWorldaaa.java";

    public static void main(String[] args) {
        FileReader reader = null;//文件输入流
        FileWriter writer = null;//文件输出流
        try{
            reader = new FileReader(INPUT_FILE_PATH);
            writer = new FileWriter(OUTPUT_FILE_PATH);
            char[] buf = new char[1024];//缓冲数组
            while(reader.read(buf)!=-1){//读
                writer.write(buf);//写
            }
            writer.flush();//flush确保内存中的数据都写入了硬盘
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
