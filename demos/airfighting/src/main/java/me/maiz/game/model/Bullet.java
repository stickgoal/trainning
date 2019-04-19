package me.maiz.game.model;

import me.maiz.game.config.Config;
import me.maiz.game.ui.GamePlayingScreen;
import me.maiz.game.util.ImageConstants;
import me.maiz.game.util.ImageStore;

import java.awt.*;
import java.util.List;



/**
 * 子弹类
 */
public class Bullet {
    private int  posX;
    private int  posY;
    private int width;
    private int height;
    private int speed;
    private Image bulletImage;

    private GamePlayingScreen screen;

    public Bullet(GamePlayingScreen screen){
        this.screen=screen;
        bulletImage=ImageStore.YELLOW_BULLET_IMG;
        width=ImageConstants.YELLOW_BULLET_WIDTH;
        height=ImageConstants.YELLOW_BULLET_HEIGHT;
        speed= Config.YELLOW_BULLET_MOVE_SPEED;
    }

    public Rectangle getRectangle() {
        return new Rectangle(posX, posY, width, height);
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(bulletImage, posX, posY, width, height, screen);

    }

    public EnemyPlane hitEnemyPlane(){
       List<EnemyPlane> enemyPlanes = screen.getEnemyPlaneList();
        for (int i=0;i<enemyPlanes.size();i++) {
            EnemyPlane e=enemyPlanes.get(i);
            if(e.getRectangle().intersects(getRectangle())){
                e.addHittedCount();
                return e;
            }
        }
        return null;
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

    public Image getBulletImage() {
        return bulletImage;
    }

    public void setBulletImage(Image bulletImage) {
        this.bulletImage = bulletImage;
    }

    public GamePlayingScreen getScreen() {
        return screen;
    }

    public void setScreen(GamePlayingScreen screen) {
        this.screen = screen;
    }
}
