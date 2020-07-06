package me.maiz.se.mini.fx;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class EventDemo  extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        //使用Hbox布局
        HBox hBox = new HBox();
        //一个按钮，绑定事件处理
        Button aButton = new Button("点我呀");
        aButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(event.getEventType()+":"+event.getSource()+":"+event.getTarget());
                System.out.println("有人点了我！");
            }
        });
        //加入到hBox
        hBox.getChildren().add(aButton);

        //加入到场景舞台，启动
        Scene scene = new Scene(hBox, 300, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
