package me.maiz.se.game;

import javax.swing.*;
import java.awt.*;

public class Game {

    private static JButton[] buttons = new JButton[25];

    private static final String COVER_IMG = "src/main/java/me/maiz/se/game/images/1.png";
    private static final String FACE_IMG = "src/main/java/me/maiz/se/game/images/2.png";

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame("翻牌游戏，全部翻完算结束");


        JPanel menu = new JPanel();
        menu.add(new JLabel("点击重置重新开始"));
        JButton resetBtn = new JButton("重置");
        resetBtn.addActionListener((e) -> {
            for (int i = 0; i < buttons.length; i++) {
                buttons[i].setIcon(new ImageIcon(COVER_IMG));
            }
        });
        menu.add(resetBtn);

        JPanel gameBoard = new JPanel();
        gameBoard.setLayout(new GridLayout(5, 5));
        for (int i = 0; i < buttons.length; i++) {
            JButton imageBtn = new JButton();
            imageBtn.setIcon(new ImageIcon(COVER_IMG));
            imageBtn.setActionCommand("" + i);
            imageBtn.addActionListener(e -> {
                String cmd = e.getActionCommand();
                int idx = Integer.valueOf(cmd);
                //左上角
                if (idx == 0) {
                    turnover(idx);
                    turnover(idx + 1);
                    turnover(idx + 5);
                } else if (idx == 4) {
                    //右上角
                    turnover(idx);
                    turnover(idx - 1);
                    turnover(idx + 5);
                } else if (idx == 20) {
                    //左下角
                    turnover(idx);
                    turnover(idx - 5);
                    turnover(idx + 1);
                } else if (idx == 24) {
                    //右下角
                    turnover(idx);
                    turnover(idx - 5);
                    turnover(idx - 1);
                } else if (idx > 0 && idx < 4) {
                    //第一排
                    turnover(idx);
                    turnover(idx - 1);
                    turnover(idx + 1);
                    turnover(idx + 5);
                } else if (idx % 5 == 0) {
                    //左边列
                    turnover(idx);
                    turnover(idx - 5);
                    turnover(idx + 1);
                    turnover(idx + 5);
                } else if (idx % 5 == 4) {
                    //右边列
                    turnover(idx);
                    turnover(idx - 5);
                    turnover(idx - 1);
                    turnover(idx + 5);
                } else if (idx > 20 && idx < 24) {
                    //最后一排
                    turnover(idx);
                    turnover(idx - 1);
                    turnover(idx + 1);
                    turnover(idx - 5);
                } else if (idx > 4 && idx < 20) {
                    //中间牌
                    turnover(idx);
                    turnover(idx - 5);
                    turnover(idx + 5);
                    turnover(idx - 1);
                    turnover(idx + 1);
                }
            });
            gameBoard.add(imageBtn);
            buttons[i] = imageBtn;
        }

        mainFrame.add("Center", gameBoard);
        mainFrame.add("South", menu);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setSize(400, 500);
        mainFrame.setVisible(true);
    }

    private static void turnover(int idx) {
        if (buttons[idx].getIcon().toString().equals(COVER_IMG)) {
            buttons[idx].setIcon(new ImageIcon(FACE_IMG));
        } else {
            buttons[idx].setIcon(new ImageIcon(COVER_IMG));
        }
    }

}
