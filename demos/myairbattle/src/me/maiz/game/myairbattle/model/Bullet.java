package me.maiz.game.myairbattle.model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.List;

import me.maiz.game.myairbattle.config.BulletType;
import me.maiz.game.myairbattle.config.Config;
import me.maiz.game.myairbattle.listener.BulletListener;
import me.maiz.game.myairbattle.ui.GamePlayingScreen;
import me.maiz.game.myairbattle.util.ImageConstants;
import me.maiz.game.myairbattle.util.ImageStore;

public class Bullet {
	private int posX;
	private int posY;
	private int width;
	private int height;
	private int speed;
	private BulletType bulletType;

	private GamePlayingScreen GamePlayingScreen;
	private BulletListener listener;
	private Image bulletImage;

	public Bullet(GamePlayingScreen GamePlayingScreen, BulletType bulletType) {
		this.GamePlayingScreen = GamePlayingScreen;
		this.bulletType = bulletType;
		switch (this.bulletType) {
		case YELLOW_BULLET:
			bulletImage = ImageStore.YELLOW_BULLET_IMG;
			width = ImageConstants.YELLOW_BULLET_WIDTH;
			height = ImageConstants.YELLOW_BULLET_HEIGHT;
			speed = Config.YELLOW_BULLET_MOVE_SPEED;
			break;
		case BLUE_BULLET:
			bulletImage = ImageStore.BLUE_BULLET_IMG;
			width = ImageConstants.YELLOW_BULLET_WIDTH;
			height = ImageConstants.YELLOW_BULLET_HEIGHT;
			speed = Config.BLUE_BULLET_MOVE_SPEED;
			break;
		}
	}

	public Rectangle getRectangle() {
		return new Rectangle(posX, posY, width, height);
	}

	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(bulletImage, posX, posY, width, height, GamePlayingScreen);

	}

	public EnemyPlane hitEnemyPlanes() {
		List<EnemyPlane> enmeyPlanes = this.GamePlayingScreen.getEnemyPlanes();
		for (int i = 0; i < enmeyPlanes.size(); i++) {
			EnemyPlane enemyPlane = enmeyPlanes.get(i);
			if (this.getRectangle().intersects(enemyPlane.getRectangle())) {
				enemyPlane.addHittedCount();
				return enemyPlane;
			}
		}
		return null;
	}

	public void addBulletListener(GamePlayingScreen GamePlayingScreen) {
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

	public BulletType getBulletType() {
		return bulletType;
	}

	public void setBulletType(BulletType bulletType) {
		this.bulletType = bulletType;
	}

	public GamePlayingScreen getGamePlayingScreen() {
		return GamePlayingScreen;
	}

	public void setGamePlayingScreen(GamePlayingScreen GamePlayingScreen) {
		this.GamePlayingScreen = GamePlayingScreen;
	}

	public BulletListener getListener() {
		return listener;
	}

	public void setListener(BulletListener listener) {
		this.listener = listener;
	}

	public Image getBulletImage() {
		return bulletImage;
	}

	public void setBulletImage(Image bulletImage) {
		this.bulletImage = bulletImage;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

}
