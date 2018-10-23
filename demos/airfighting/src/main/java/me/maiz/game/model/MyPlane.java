package me.maiz.game.model;

import me.maiz.game.config.Config;
import me.maiz.game.ui.GamePlayingScreen;
import me.maiz.game.util.ImageConstants;
import me.maiz.game.util.ImageStore;

import java.awt.*;
import java.awt.event.MouseEvent;

public class MyPlane {

    private final GamePlayingScreen screen;
    private int posX;
    private int posY;

    private int width;
    private int height;
    private Image planeImage;
    private Image planeFlyingImage;
    private boolean isAlive;
    private boolean flip;


    public MyPlane(GamePlayingScreen screen) {
        this.isAlive = true;
        this.flip = true;
        this.screen = screen;
        this.width = ImageConstants.MY_PLANE_WIDTH;
        this.height = ImageConstants.MY_PLANE_HEIGHT;
        this.planeImage = ImageStore.MY_PLANE_IMG;
        this.planeFlyingImage = ImageStore.MY_PLANE_FLYING_IMG;
        new Thread(new LaunchBulletThread()).start();
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        if (flip) {
            g2d.drawImage(planeImage, posX, posY, width, height, screen);
        } else {
            g2d.drawImage(planeFlyingImage, posX, posY, width, height, screen);
        }
        flip = !flip;
    }

    public Rectangle getRectange() {
        int fix = width / 3;
        return new Rectangle(posX + fix, posY, width / 3, height);
    }


    public GamePlayingScreen getScreen() {
        return screen;
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

    public Image getPlaneImage() {
        return planeImage;
    }

    public void setPlaneImage(Image planeImage) {
        this.planeImage = planeImage;
    }

    public Image getPlaneFlyingImage() {
        return planeFlyingImage;
    }

    public void setPlaneFlyingImage(Image planeFlyingImage) {
        this.planeFlyingImage = planeFlyingImage;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isFlip() {
        return flip;
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public void mouseMoved(MouseEvent e) {
        this.posX=e.getX()- width/2;
        this.posY=e.getY()-height/2;
    }


    class LaunchBulletThread implements Runnable{

        @Override
        public void run() {
            while(isAlive){
                try {
                    Thread.sleep(Config.BULLET_FIRE_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                launch();
            }
        }


    }
    private void launch() {
        if (isAlive) {

            int bulletPosX = posX + width / 2 - ImageConstants.YELLOW_BULLET_WIDTH / 2;
            int bulletPosY = posY + ImageConstants.YELLOW_BULLET_HEIGHT;


            Bullet b = new Bullet(this.screen);
            b.setPosX(bulletPosX);
            b.setPosY(bulletPosY);
            screen.addBullet(b);
        }
    }


}
