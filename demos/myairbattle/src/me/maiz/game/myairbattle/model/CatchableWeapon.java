package me.maiz.game.myairbattle.model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import me.maiz.game.myairbattle.config.CatchableWeaponType;
import me.maiz.game.myairbattle.ui.GamePlayingScreen;

public abstract class CatchableWeapon {
    private int posX;
    private int posY;

    private int width;
    private int height;
    private Image weaponImage;

    private GamePlayingScreen GamePlayingScreen;
    private CatchableWeaponType weaponType;
    private boolean useAnimation;
    private boolean useAnimationDone;
    private int speed;
    private boolean weaponDisappear;

    public CatchableWeapon(GamePlayingScreen GamePlayingScreen, CatchableWeaponType weaponType) {
	this.GamePlayingScreen = GamePlayingScreen;
	this.weaponType = weaponType;
	this.useAnimation = false;
	this.useAnimationDone = false;
	this.weaponDisappear = false;
    }

    public Rectangle getRectangle() {
	return new Rectangle(posX, posY, width, height);
    }

    public void draw(Graphics g) {
	Graphics2D g2d = (Graphics2D) g;
	g2d.drawImage(weaponImage, posX, posY, width, height, GamePlayingScreen);
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

    public Image getWeaponImage() {
	return weaponImage;
    }

    public void setWeaponImage(Image weaponImage) {
	this.weaponImage = weaponImage;
    }

    public GamePlayingScreen getGamePlayingScreen() {
	return GamePlayingScreen;
    }

    public void setGamePlayingScreen(GamePlayingScreen GamePlayingScreen) {
	this.GamePlayingScreen = GamePlayingScreen;
    }

    public CatchableWeaponType getWeaponType() {
	return weaponType;
    }

    public void setWeaponType(CatchableWeaponType weaponType) {
	this.weaponType = weaponType;
    }

    public int getSpeed() {
	return speed;
    }

    public void setSpeed(int speed) {
	this.speed = speed;
    }

    public boolean isUseAnimation() {
	return useAnimation;
    }

    public void setUseAnimation(boolean useAnimation) {
	this.useAnimation = useAnimation;
    }

    public boolean isUseAnimationDone() {
	return useAnimationDone;
    }

    public void setUseAnimationDone(boolean useAnimationDone) {
	this.useAnimationDone = useAnimationDone;
    }

    public boolean isWeaponDisappear() {
	return weaponDisappear;
    }

    public void setWeaponDisappear(boolean weaponDisappear) {
	this.weaponDisappear = weaponDisappear;
    }

}
