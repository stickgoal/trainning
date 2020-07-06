package me.maiz.se.mini.fx.control;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Login extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX登录");
        //网格面板，用网格布局
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        //文本
        Text scenetitle = new Text("欢迎您登录");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);
        //用户名标签
        Label userName = new Label("用户名:");
        grid.add(userName, 0, 1);
        //用户名输入控件
        TextField userTextField = new TextField();
        userTextField.setPromptText("请输入用户名");
        grid.add(userTextField, 1, 1);

        //密码标签
        Label pw = new Label("密 码:");
        grid.add(pw, 0, 2);
        //密码输入控件
        PasswordField pwBox = new PasswordField();
        pwBox.setPromptText("请输入密码");

        grid.add(pwBox, 1, 2);

        //登录按钮
        Button btn = new Button("登录");
        //设为默认按钮
        btn.setDefaultButton(true);

        //HBox布局，把按钮放在右下角
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        //提示文字
//        final Text actiontarget = new Text();
//        grid.add(actiontarget, 0, 6);
//        grid.setColumnSpan(actiontarget, 2);
//        grid.setHalignment(actiontarget, RIGHT);
//        actiontarget.setId("actiontarget");

        //按钮点击事件
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
//                actiontarget.setFill(Color.FIREBRICK);
//                actiontarget.setText("点击了登录按钮");
                //后台输出用户录入的内容
                System.out.println(userTextField.getText());
                System.out.println(pwBox.getText());
            }
        });

        //加入到场景舞台，启动
        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
