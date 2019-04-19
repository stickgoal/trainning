package me.maiz.game.model;

import me.maiz.game.config.Config;
import me.maiz.game.config.EnemyPlaneType;
import me.maiz.game.ui.GamePlayingScreen;
import me.maiz.game.util.ImageConstants;
import me.maiz.game.util.ImageStore;

import java.awt.*;
import java.util.Random;

public class EnemyPlane {

    private int posX;
    private int posY;
    private int width;
    private int height;
    private int speed;
    private Image planeImage;
    private GamePlayingScreen screen;

    private int hittedCount;
    private int killedCount;
    private int killedScore;

    private EnemyPlaneType enemyPlaneType;


    public EnemyPlane(GamePlayingScreen screen, EnemyPlaneType enemyType) {
        this.screen = screen;
        this.enemyPlaneType = enemyType;
        this.hittedCount = 0;
        switch (enemyType){
            case SMALL_ENEMY_PLANE:
                this.planeImage=ImageStore.SMALL_PLANE_IMG;
                this.setWidth(ImageConstants.SMALL_PLANE_WIDTH);
                this.setHeight(ImageConstants.SMALL_PLANE_HEIGHT);
                this.setKilledCount(Config.BULLET_COUNT_TO_KILL_SMALL_PLANE);
                this.setKilledScore(Config.KILL_SMALL_PLANE_SCORE);
                break;
            case BIG_ENEMY_PLANE:
                this.planeImage=ImageStore.BIG_PLANE_IMG;
                this.setWidth(ImageConstants.BIG_PLANE_WIDTH);
                this.setHeight(ImageConstants.BIG_PLANE_HEIGHT);
                this.setKilledCount(Config.BULLET_COUNT_TO_KILL_BIG_PLANE);
                this.setKilledScore(Config.KILL_BIG_PLANE_SCORE);
                break;
            case BOSS_ENEMY_PLANE:
                this.planeImage=ImageStore.BOSS_PLANE_IMG;
                break;
        }
        int speed = new Random().nextInt(Config.ENEMY_PLANE_MOVE_SPEED_MAX - Config.ENEMY_PLANE_MOVE_SPEED_MIN)
                + Config.ENEMY_PLANE_MOVE_SPEED_MIN;
        this.speed=speed;
    }

    public void addHittedCount() {
        this.hittedCount++;
    }

    public boolean isKilled() {
        return this.hittedCount >= this.killedCount;
    }

    public Rectangle getRectangle() {
        return new Rectangle(posX, posY, width, height);
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(planeImage, posX, posY, width, height, screen);
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Image getPlaneImage() {
        return planeImage;
    }

    public void setPlaneImage(Image planeImage) {
        this.planeImage = planeImage;
    }

    public GamePlayingScreen getScreen() {
        return screen;
    }

    public void setScreen(GamePlayingScreen screen) {
        this.screen = screen;
    }

    public int getHittedCount() {
        return hittedCount;
    }

    public void setHittedCount(int hittedCount) {
        this.hittedCount = hittedCount;
    }

    public int getKilledCount() {
        return killedCount;
    }

    public void setKilledCount(int killedCount) {
        this.killedCount = killedCount;
    }

    public EnemyPlaneType getEnemyPlaneType() {
        return enemyPlaneType;
    }

    public void setEnemyPlaneType(EnemyPlaneType enemyPlaneType) {
        this.enemyPlaneType = enemyPlaneType;
    }

    public void drawKilled(Graphics g) {
        new Thread(() -> {
            Image killedImage = null;
            Image ashedImage = null;
            int killedWidth=0;
            int killedHeight=0;
            int ashedWidth=0;
            int ashedHeight=0;
            switch(enemyPlaneType){
                case SMALL_ENEMY_PLANE:
                    killedImage=ImageStore.SMALL_PLANE_KILLED_IMG;
                    ashedImage=ImageStore.SMALL_PLANE_ASHED_IMG;
                    killedWidth=ImageConstants.SMALL_PLANE_KILLED_WIDTH;
                    killedHeight=ImageConstants.SMALL_PLANE_KILLED_HEIGHT;
                    ashedWidth=ImageConstants.SMALL_PLANE_ASHED_WIDTH;
                    ashedHeight=ImageConstants.SMALL_PLANE_ASHED_HEIGHT;
                    break;
                case BIG_ENEMY_PLANE:
                    killedImage=ImageStore.BIG_PLANE_KILLED_IMG;
                    ashedImage=ImageStore.BIG_PLANE_ASHED_IMG;
                    killedWidth=ImageConstants.BIG_PLANE_KILLED_WIDTH;
                    killedHeight=ImageConstants.BIG_PLANE_KILLED_HEIGHT;
                    ashedWidth=ImageConstants.BIG_PLANE_ASHED_WIDTH;
                    ashedHeight=ImageConstants.BIG_PLANE_ASHED_HEIGHT;
                    break;
            }
            //爆炸
            this.setPlaneImage(killedImage);
            this.setWidth(killedWidth);
            this.setHeight(killedHeight);
            EnemyPlane.this.draw(g);
            try {
                Thread.sleep(Config.SMALL_PLANE_STATUS_CHANGE_INTERVAL);
            } catch (InterruptedException e) {

            }
            //化为灰烬
            this.setPlaneImage(ashedImage);
            this.setWidth(ashedWidth);
            this.setHeight(ashedHeight);
            EnemyPlane.this.draw(g);
            try {
                Thread.sleep(Config.SMALL_PLANE_STATUS_CHANGE_INTERVAL);
            } catch (InterruptedException e) {

            }
            synchronized ( screen.getEnemyPlaneList()) {
                screen.getEnemyPlaneList().remove(this);
            }

        }).start();
    }

    public int getKilledScore() {
        return killedScore;
    }

    public void setKilledScore(int killedScore) {
        this.killedScore = killedScore;
    }
}
