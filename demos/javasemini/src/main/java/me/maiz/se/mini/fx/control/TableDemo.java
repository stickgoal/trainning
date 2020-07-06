package me.maiz.se.mini.fx.control;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TableDemo extends Application {
    //表格视图
    private TableView<Student> table = new TableView<>();
    //数据列表，使用ObservableList以支持在修改发生时做一些操作
    private final ObservableList<Student> data =
            FXCollections.observableArrayList(
                    new Student("张丽萍", 19, "20200101"),
                    new Student("王一梓", 15, "20200102"),
                    new Student("李宏亮", 21, "20200103"),
                    new Student("郭飞玲", 20, "20200104"),
                    new Student("柯得平", 21, "20200105")
            );

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("TableView 例子");
        stage.setWidth(450);
        stage.setHeight(500);

        final Label label = new Label("学生信息");
        label.setFont(new Font("Arial", 20));

        table.setEditable(true);
        //创建表格列，指定名称
        TableColumn nameCol = new TableColumn("姓名");
        //设置宽度
        nameCol.setMinWidth(100);
        //指定对象与字段的绑定关系，nameCol <-> student#name
        nameCol.setCellValueFactory(
                new PropertyValueFactory<Student, String>("name"));

        TableColumn ageCol = new TableColumn("年龄");
        ageCol.setMinWidth(100);
        ageCol.setCellValueFactory(
                new PropertyValueFactory<Student, Integer>("age"));

        TableColumn stuNoCol = new TableColumn("学号");
        stuNoCol.setMinWidth(200);
        stuNoCol.setCellValueFactory(
                new PropertyValueFactory<Student, String>("stuNo"));

        //将数据放入
        table.setItems(data);
        //添加列到表格
        table.getColumns().addAll(nameCol, ageCol, stuNoCol);

        //放入VBox容器
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);
        //放入sence中的Group
        ((Group) scene.getRoot()).getChildren().addAll(vbox);
        //放入sence启动
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

public static class Student {

    private String name;

    private int age;

    private String stuNo;

    public Student() {
    }

    public Student(String name, int age, String stuNo) {
        this.name = name;
        this.age = age;
        this.stuNo = stuNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }
}
}