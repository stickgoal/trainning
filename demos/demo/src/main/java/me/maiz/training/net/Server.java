package me.maiz.training.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Lucas on 2017-11-13.
 */
public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(4000);
        System.out.println("server运行中...");
        Socket socket = server.accept();
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        String line = "";
        while(!line.equals("shutdown")) {
            System.out.println("client:"+line);
            line= br.readLine();
            pw.println(getResponse(line));
            pw.flush();
        }

    }

    private static String getResponse(String line) {
        String resp = null;
        switch (line){
            case "你好":
                resp = "你好呀";
                break;
            case "你在干嘛呀":
                resp = "我在监听你的请求啊";
                break;
            default:
                resp="没听清楚呢";
        }

        return resp;
    }


}
