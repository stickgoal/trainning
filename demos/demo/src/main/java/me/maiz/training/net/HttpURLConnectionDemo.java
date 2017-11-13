package me.maiz.training.net;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Lucas on 2017-11-13.
 */
public class HttpURLConnectionDemo {

    public static void main(String[] args) throws IOException {
        //连接到地址
        URL url = new URL("http://static.cnbetacdn.com/thumb/article/2017/0610/c0da5b5a4e003ed.jpg");
        //打开访问
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //状态正常
        if(conn.getResponseCode()== HttpURLConnection.HTTP_OK){
            //IO 写入
            InputStream is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream("c:/tmp/a.jpg");
            byte[] buf = new byte[1024];
            int c = 0 ;
            while((c = (is.read(buf,0,buf.length)))!=-1){
                fos.write(buf,0,c);
            }
            fos.close();
            is.close();
        }

    }

}
