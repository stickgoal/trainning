package me.maiz.se.mini.fx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

//启动类继承Application
public class HelloWorld2 extends Application {

    //start方法是所有FX应用的入口，定义在Application中的抽象方法
    @Override
    public void start(Stage primaryStage) throws Exception {
        //创建一个按钮
        Button btn = new Button();
        //给按钮设置文字
        btn.setText("Say 'Hello World'");
        //设置点击按钮时的相应操作
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        //创建一个面板，用于展示场景
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        //创建场景
        Scene scene = new Scene(root, 300, 250);
        //为主舞台（参数传入）设置标题
        primaryStage.setTitle("Hello World!");
        //将场景加入到主舞台
        primaryStage.setScene(scene);
        //显示主舞台
        primaryStage.show();
    }

    //main方法中启动应用
    public static void main(String[] args) {
        launch(args);
    }
}
