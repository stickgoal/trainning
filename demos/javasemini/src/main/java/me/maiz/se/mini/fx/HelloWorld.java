package me.maiz.se.mini.fx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

//启动类继承Application
public class HelloWorld extends Application {

    //start方法是所有FX应用的入口，定义在Application中的抽象方法
    @Override
    public void start(Stage primaryStage) throws Exception {
        //创建一个label，显示一些文字
        Label label = new Label();
        label.setText("你好，世界!");
        //创建一个面板，用于展示场景
        StackPane root = new StackPane();
        root.getChildren().add(label);
        //创建场景
        Scene scene = new Scene(root, 300, 250);
        //为主舞台（参数传入）设置标题
        primaryStage.setTitle("Hello World!");
        //将场景加入到主舞台
        primaryStage.setScene(scene);
        //显示主舞台
        primaryStage.show();

        //第二舞台
        Stage   stage = new Stage();
        stage.setScene(new Scene(new Button("我是按钮"),180,100));
        stage.setAlwaysOnTop(true);
        stage.setTitle("第二舞台");
        stage.show();

    }

    //main方法中启动应用
    public static void main(String[] args) {
        launch(args);
    }
}
