package me.maiz.training.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Lucas on 2017-11-13.
 */
public class Client {

    public static void main(String[] args) throws IOException {
        Socket client = new Socket("127.0.0.1",4000);
        System.out.println("client运行中...");
        BufferedReader sin = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader resp = new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter pw = new PrintWriter(client.getOutputStream());
        String line = sin.readLine();
        while(!line.equals("exit")&&line.length()>0) {
            pw.println(line);
            pw.flush();
            System.out.println("服务器回应 ： "+resp.readLine());
            line = sin.readLine();
        }


    }


}
