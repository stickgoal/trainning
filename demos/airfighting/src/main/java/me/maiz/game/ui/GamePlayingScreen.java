package me.maiz.game.ui;

import me.maiz.game.config.Config;
import me.maiz.game.model.Bullet;
import me.maiz.game.model.EnemyPlane;
import me.maiz.game.model.MyPlane;
import me.maiz.game.util.AudioStore;
import me.maiz.game.util.ImageConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static me.maiz.game.config.EnemyPlaneType.BIG_ENEMY_PLANE;
import static me.maiz.game.config.EnemyPlaneType.SMALL_ENEMY_PLANE;

public class GamePlayingScreen extends JPanel implements MouseMotionListener {


    /**
     * 我机
     */
    private MyPlane myPlane;

    /**
     * 敌机
     */
    private List<EnemyPlane> enemyPlaneList ;

    /**
     * 子弹
     */
    private List<Bullet> bullets ;

    /**
     * 绘制线程，用于异步绘制画面
     * @param b
     */
    private PaintThread paintThread;

    /**
     * 倒数器
     */
    private CountdownTimer countdownTimer;

    /**
     * 分数
     */
    private int score;

    public void addBullet(Bullet b){
        bullets.add(b);
    }

    public GamePlayingScreen(){
        AudioStore.loadAudio();
        AudioStore.gameMusicSoundPlayer.loop();
        AudioStore.fireBulletSoundPlayer.loop();

        this.addMouseMotionListener(this);




    }

    public void start(){
        this.score=0;
        this.enemyPlaneList=new ArrayList<>();
        this.bullets=new ArrayList<>();
        myPlane=new MyPlane(this);
        this.myPlane.setPosX((Config.MAIN_FRAME_WIDTH - ImageConstants.MY_PLANE_WIDTH) / 2);
        this.myPlane.setPosY(Config.MAIN_FRAME_HEIGHT - ImageConstants.MY_PLANE_HEIGHT);
        this.setSize(Config.MAIN_FRAME_WIDTH, Config.MAIN_FRAME_HEIGHT);
        this.setDoubleBuffered(true);
        this.setOpaque(false);
        countdownTimer = new CountdownTimer();


        paintThread = new PaintThread();
        paintThread.start();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        myPlane.draw(g);

        drawScore(g);

        //重新将飞机和子弹画出来
        for (int i = 0; i < this.bullets.size(); i++) {
            bullets.get(i).draw(g);
        }
        for (int i = 0; i < this.enemyPlaneList.size(); i++) {
            enemyPlaneList.get(i).draw(g);
        }

    }

    private void drawScore(Graphics g) {
        g.setFont(new Font("微软雅黑",Font.BOLD,30));
        g.setColor(Color.pink);
        g.drawString(""+score,30,30);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(myPlane!=null&&myPlane.isAlive()){
            myPlane.mouseMoved(e);
            this.repaint();
        }
    }

    public List<EnemyPlane> getEnemyPlaneList() {
        return enemyPlaneList;
    }

    public void setEnemyPlaneList(List<EnemyPlane> enemyPlaneList) {
        this.enemyPlaneList = enemyPlaneList;
    }

    class PaintThread extends Thread{
        @Override
        public void run() {
            while(myPlane.isAlive()){
                //循环改变所有子弹的坐标位置，并触发重绘，子弹就会前进
                for (int i = 0; i < bullets.size(); i++) {
                    Bullet b = bullets.get(i);
                    changeBulletLocation(b);
                }
                //循环改变所有飞机的坐标位置，并触发重绘，敌机前进
                for(int i=0;i<enemyPlaneList.size();i++){
                    EnemyPlane e  = enemyPlaneList.get(i);
                    changeEnemyLocation(e);
                }

                //释放敌机
                if(countdownTimer.remainTimeToPopSmallPlane>=0){
                    countdownTimer.remainTimeToPopSmallPlane-=Config.GAME_PANEL_REPAINT_INTERVAL;;
                }else{
                    popUpASmallPlane();
                    countdownTimer.remainTimeToPopSmallPlane=Config.POP_SMALL_ENEMY_PLANE_INTERVAL;
                }

                if(countdownTimer.remainTimeToPopBigPlane>=0){
                    countdownTimer.remainTimeToPopBigPlane-=Config.GAME_PANEL_REPAINT_INTERVAL;
                }else{
                    popUpABigPlane();
                    countdownTimer.remainTimeToPopBigPlane=Config.POP_BIG_ENEMY_PLANE_INTERVAL;
                }


                GamePlayingScreen.this.repaint();

                try {
                    Thread.sleep(Config.GAME_PANEL_REPAINT_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

        }


    }


    private void popUpASmallPlane() {
        EnemyPlane enemyPlane = new EnemyPlane(this,SMALL_ENEMY_PLANE);
        enemyPlane.setPosX(new Random().nextInt(getWidth() - enemyPlane.getWidth()));
        synchronized (this) {
            enemyPlaneList.add(enemyPlane);
        }
    }
    private void popUpABigPlane() {
        EnemyPlane enemyPlane = new EnemyPlane(this,BIG_ENEMY_PLANE);
        enemyPlane.setPosX(new Random().nextInt(getWidth() - enemyPlane.getWidth()));
        synchronized (this) {
            enemyPlaneList.add(enemyPlane);
        }
    }

    private void changeEnemyLocation(EnemyPlane e) {
        if(e!=null&&!e.isKilled()){
            e.setPosY(e.getPosY() + e.getSpeed());
            if (e.getPosY() >= this.getHeight()) {
                synchronized (this.enemyPlaneList) {
                    enemyPlaneList.remove(e);
                }
            }else{
                if (e.getRectangle().intersects(myPlane.getRectange())) {
                    // game ends
                    synchronized (myPlane) {
                        if (myPlane.isAlive()) {
                            stopGame();
                        }
                    }
                }
            }
        }

    }

    private void stopGame() {
        this.myPlane.setAlive(false);
        AudioStore.gameMusicSoundPlayer.stop();
        AudioStore.fireBulletSoundPlayer.stop();
        AudioStore.gameOverSoundPlayer.play();
    }


    private void changeBulletLocation(Bullet b) {
        if(b!=null){
            //改变子弹位置
            b.setPosY(b.getPosY()-b.getSpeed());
            if(b.getPosY()<=0){
                synchronized (this.bullets){
                    this.bullets.remove(b);
                }
            }
            //确认子弹是否击中飞机✈️
            EnemyPlane enemyPlane = b.hitEnemyPlane();
            if(enemyPlane!=null&&enemyPlane.isKilled()){
                switch (enemyPlane.getEnemyPlaneType()) {
                    case SMALL_ENEMY_PLANE:
                        AudioStore.SMALL_PLANE_KILLED_SOUND_PLAYER.play();
                        break;
                    case BIG_ENEMY_PLANE:
                        AudioStore.BIG_PLANE_KILLED_SOUND_PLAYER.play();
                        break;
                    case BOSS_ENEMY_PLANE:
                        AudioStore.bossPlaneKilledSoundPlayer.play();
                        break;
                }
                enemyPlane.drawKilled(getComponentGraphics(getGraphics()));

                synchronized (this) {
                    this.score += enemyPlane.getKilledScore();
                }

                synchronized (this.bullets) {
                    this.bullets.remove(b);
                }
            }

        }
    }

    /**
     *
     * @author Lucas
     *
     * @Date 2018年10月17日 下午5:32:27
     *
     */
    private class CountdownTimer {

        // 释放小飞机剩余时间
        public int remainTimeToPopSmallPlane;
        // 释放大飞机剩余时间
        public int remainTimeToPopBigPlane;
        // 释放大Boss剩余时间
        public int remainTimeToPopBossPlane;

        // 释放炸弹剩余时间
        public int remainTimeToPopBomb;

        // 释放激光剩余时间
        public int remainTimeToPopDoubleLaser;

        // 激光结束剩余时间
        public int remainTimeDoubleLaserRunOut;

        public CountdownTimer() {
            this.remainTimeToPopSmallPlane = Config.POP_SMALL_ENEMY_PLANE_INTERVAL;
            this.remainTimeToPopBigPlane = Config.POP_BIG_ENEMY_PLANE_INTERVAL;
            this.remainTimeToPopBossPlane = Config.POP_BOSS_ENEMY_PLANE_INTERVAL;
            this.remainTimeToPopBomb = Config.POP_BOMBO_INTERVAL;
            this.remainTimeToPopDoubleLaser = Config.POP_DOUBLE_LASER_INTERVAL;
            this.remainTimeDoubleLaserRunOut = Config.DOUBLE_LASER_LAST_TIME;
        }

    }
    public MyPlane getMyPlane() {
        return myPlane;
    }

    public void setMyPlane(MyPlane myPlane) {
        this.myPlane = myPlane;
    }
}
