package me.maiz.se.mini.fx;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

public class GraphicDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(300, 250);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawShapes(gc);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void drawShapes(GraphicsContext gc) {
        //填充为绿色
        gc.setFill(Color.GREEN);
        //线条为蓝色
        gc.setStroke(Color.BLUE);
        //线宽度为5
        gc.setLineWidth(5);
        //画根线，四个参数分别为 起点和终点的坐标位置
        gc.strokeLine(40, 10, 10, 40);
        //填充椭圆形，四个参数分别为起始点位置的x、y、宽、高
        gc.fillOval(10, 60, 30, 30);
        //画个椭圆形，四个参数分别为起始点位置的x、y、宽、高
        gc.strokeOval(60, 60, 30, 30);
        //填充圆角矩形，参数分别为起始点位置的x、y、宽、高、圆角宽度、圆角高度
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        //画个圆角矩形，参数分别为起始点位置的x、y、宽、高、圆角宽度、圆角高度
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        //填充弧形，参数分别为起始点位置的x、y、宽、高、起始角度、打开的弧度、弧度类型
        gc.fillArc(10, 110, 30, 30, 45, 200, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        //画个弧形，参数分别为起始点位置的x、y、宽、高、起始角度、打开的弧度、弧度类型
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        //填充多边形
        gc.fillPolygon(new double[]{10, 40, 10, 40},
                new double[]{210, 210, 240, 240}, 4);
        //画个多边形，参数分别为 所有四个点的x轴位置，所有四个点的y轴位置，点的个数
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                new double[]{210, 210, 240, 240}, 4);
    }
}