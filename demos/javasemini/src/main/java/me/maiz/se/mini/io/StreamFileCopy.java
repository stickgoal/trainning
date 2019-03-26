package me.maiz.se.mini.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class StreamFileCopy {

    public static final String INPUT_FILE_PATH="d:\\tmp\\HelloWorld.java";
    public static final String OUTPUT_FILE_PATH="d:\\tmp\\HelloWorld1.java";

    public static final String INPUT_IMG_PATH="d:\\tmp\\GOF设计模式.pdf";
    public static final String OUTPUT_IMG_PATH="d:\\tmp\\GOF设计模式-copy.pdf";

    public static void main(String[] args) {
        FileInputStream fis = null;//文件输入流
        FileOutputStream fos = null;//文件输出流
        try{
            fis = new FileInputStream(INPUT_IMG_PATH);
            fos = new FileOutputStream(OUTPUT_IMG_PATH);
            byte[] buf = new byte[1024];//缓冲数组
            while(fis.read(buf)!=-1){//读
                fos.write(buf);//写
            }
            fos.flush();//flush确保内存中的数据都写入了硬盘
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //finally关闭流处理，确保数据流任何情况下都会关闭
            //避免内存泄漏
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }



}
