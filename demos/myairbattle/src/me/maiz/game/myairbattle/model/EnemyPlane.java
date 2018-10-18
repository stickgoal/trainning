package me.maiz.game.myairbattle.model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import me.maiz.game.myairbattle.config.EnemyPlaneType;
import me.maiz.game.myairbattle.listener.EnemyPlaneListener;
import me.maiz.game.myairbattle.ui.GamePlayingScreen;

public abstract class EnemyPlane {
	private int posX;
	private int posY;
	private int width;
	private int height;
	private int speed;
	private int hittedCount;
	private int killedCount;
	private int killedScore;
	private Image planeImage;
	private EnemyPlaneListener listener;
	private EnemyPlaneType enemyType;
	private GamePlayingScreen GamePlayingScreen;

	public EnemyPlane(GamePlayingScreen getPlayingPanel, EnemyPlaneType enemyType) {
		this.GamePlayingScreen = getPlayingPanel;
		this.enemyType = enemyType;
		this.hittedCount = 0;
	}

	public Rectangle getRectangle() {
		return new Rectangle(posX, posY, width, height);
	}

	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(planeImage, posX, posY, width, height, GamePlayingScreen);
	}

	public void addEnemyPlaneListener(EnemyPlaneListener listener) {
		this.listener = listener;
	}

	public void addHittedCount() {
		this.hittedCount++;
	}

	public boolean isKilled() {
		return this.hittedCount >= this.killedCount;
	}

	public abstract void drawFighting(Graphics g);

	public abstract void drawKilled(Graphics g);

	public EnemyPlaneType getEnemyType() {
		return enemyType;
	}

	public void setEnemyType(EnemyPlaneType enemyType) {
		this.enemyType = enemyType;
	}

	public GamePlayingScreen getGamePlayingScreen() {
		return GamePlayingScreen;
	}

	public void setGamePlayingScreen(GamePlayingScreen GamePlayingScreen) {
		this.GamePlayingScreen = GamePlayingScreen;
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

	public EnemyPlaneListener getListener() {
		return listener;
	}

	public void setListener(EnemyPlaneListener listener) {
		this.listener = listener;
	}

	public Image getPlaneImage() {
		return planeImage;
	}

	public void setPlaneImage(Image planeImage) {
		this.planeImage = planeImage;
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

	public int getKilledScore() {
		return killedScore;
	}

	public void setKilledScore(int killedScore) {
		this.killedScore = killedScore;
	}

}
