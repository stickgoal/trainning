package me.maiz.game.ui;

import me.maiz.game.config.Config;
import me.maiz.game.util.ImageStore;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static javax.swing.JOptionPane.OK_OPTION;

/**
 * 游戏主界面
 */
public class MainFrame extends JFrame {

    private GamePlayingScreen gamePlayingScreen;

    private GameLoadingScreen gameLoadingScreen;

    public MainFrame() throws IOException {
        ImageStore.loadImage();
        initComponents();
    }

    private void initComponents() {
        // 设置背景图片
        setBackgroundImage();
        // 窗体标题
        this.setTitle("我的空战，不服来战~~唷");
        // 窗体尺寸
        this.setSize(Config.MAIN_FRAME_WIDTH, Config.MAIN_FRAME_HEIGHT);
        // 是否可以改变大小
        this.setResizable(false);
        // 是否点击叉叉可以关闭
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        // 设置icon
        this.setIconImage(new ImageIcon(Config.LOGO_IMG).getImage());
        // 使窗体在屏幕上居中
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((d.width - Config.MAIN_FRAME_WIDTH) / 2, (d.height - Config.MAIN_FRAME_HEIGHT) / 2,
                Config.MAIN_FRAME_WIDTH, Config.MAIN_FRAME_HEIGHT);
        // 使窗体可见
        this.setVisible(true);
    }

    private void setBackgroundImage() {
        ImageIcon bgImgIcon = new ImageIcon(ImageStore.SHOOT_BACKGROUND_IMG);
        JLabel bgLabel = new JLabel(bgImgIcon);
        this.getLayeredPane().add(bgLabel, new Integer(Integer.MIN_VALUE));
        bgLabel.setBounds(0, 0, bgImgIcon.getIconWidth(), bgImgIcon.getIconHeight());
        ((JPanel) this.getContentPane()).setOpaque(false);
    }

    public void load() throws InterruptedException {
        Container c = this.getContentPane();
        c.removeAll();
        this.repaint();

        if(gameLoadingScreen==null) {
            gameLoadingScreen = new GameLoadingScreen();
        }
        BoxLayout boxLayout = new BoxLayout(c, BoxLayout.Y_AXIS);
        c.setLayout(boxLayout);
        c.add(Box.createVerticalGlue());
        c.add(gameLoadingScreen);
        c.add(Box.createVerticalGlue());
        gameLoadingScreen.loading();


        start();
    }

    public void start() throws InterruptedException {
        Container c = this.getContentPane();
        c.removeAll();
        this.repaint();

        c.setLayout(new BorderLayout());
        if(gamePlayingScreen==null) {
            gamePlayingScreen = new GamePlayingScreen();
        }
        c.add(gamePlayingScreen,BorderLayout.CENTER);
        gamePlayingScreen.start();

        while(gamePlayingScreen.getMyPlane().isAlive()){
            try {
                Thread.sleep(Config.GAME_PANEL_REPAINT_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int option = JOptionPane.showConfirmDialog(this,"再来一局？","游戏结束",OK_OPTION);
        switch (option){
            case JOptionPane.YES_OPTION:
                load();
                break;
            case JOptionPane.NO_OPTION:
                System.exit(0);
                break;
        }

    }



}
