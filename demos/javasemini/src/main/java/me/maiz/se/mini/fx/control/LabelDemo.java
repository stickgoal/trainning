package me.maiz.se.mini.fx.control;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;

public class LabelDemo extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        InputStream is;
        Image image = new Image("./horn.png");

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        //通过构造器设置文本内容及图片
        Label l1 = new Label("高调标签",imageView);
        //通过css设置样式
        l1.setStyle("-fx-background-color: yellow;");

        Label l2 = new Label();
        //通过setter设置内容
        l2.setText("低调标签");
        l2.setStyle("-fx-background-color: #eee;");
        l2.setMinHeight(50);
        l2.setMinWidth(100);
        VBox vBox = new VBox();
        vBox.getChildren().addAll(l1,l2);

        //放入场景及舞台，启动
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
