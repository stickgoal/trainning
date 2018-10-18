package me.maiz.game.myairbattle.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import me.maiz.game.myairbattle.config.Config;
import me.maiz.game.myairbattle.config.EnemyPlaneType;
import me.maiz.game.myairbattle.factory.EnemyPlaneFactory;
import me.maiz.game.myairbattle.listener.BulletListener;
import me.maiz.game.myairbattle.listener.EnemyPlaneListener;
import me.maiz.game.myairbattle.model.Bullet;
import me.maiz.game.myairbattle.model.CatchableWeapon;
import me.maiz.game.myairbattle.model.EnemyPlane;
import me.maiz.game.myairbattle.model.MyPlane;
import me.maiz.game.myairbattle.util.AudioStore;
import me.maiz.game.myairbattle.util.ImageConstants;
import me.maiz.game.myairbattle.util.ImageStore;

/**
 * 游戏屏
 * 
 * @author Lucas
 *
 * @Date 2018年10月17日 下午4:02:09
 *
 */
public class GamePlayingScreen extends JPanel
		implements EnemyPlaneListener, BulletListener, MouseMotionListener, MouseListener {

	private static final long serialVersionUID = 3819756485963712630L;
	/*
	 * 本机
	 */
	private MyPlane myPlane;

	/*
	 * 敌机列表
	 */
	private List<EnemyPlane> enemyPlanes = new LinkedList<>();

	/*
	 * 子弹列表
	 */
	private List<Bullet> bullets = new LinkedList<>();

	/*
	 * 炸弹
	 */
	private CatchableWeapon popBomb;

	/*
	 * 激光
	 */
	private CatchableWeapon popDoubleLaser;

	/*
	 * 分数
	 */
	private int score = 0;

	/*
	 * 计时器，用于计算剩余多少时间释放飞机等
	 */
	private CountdownTimer countdownTimer = new CountdownTimer();

	/*
	 * UI绘制线程
	 */
	private PaintThread paintThread;

	/**
	 * 默认构造器
	 */
	public GamePlayingScreen() {
		AudioStore.loadAudio();
		initComponent();
	}

	/**
	 * 初始化
	 */
	private void initComponent() {
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.setSize(Config.MAIN_FRAME_WIDTH, Config.MAIN_FRAME_HEIGHT);
		this.setDoubleBuffered(true);
		this.setOpaque(false);
	}

	public void start() {
		myPlane = new MyPlane(this);
		myPlane.setAlive(true);
		myPlane.setPosX((Config.MAIN_FRAME_WIDTH - ImageConstants.MY_PLANE_WIDTH) / 2);
		myPlane.setPosY(Config.MAIN_FRAME_HEIGHT - ImageConstants.MY_PLANE_FLYING_HEIGHT);
		AudioStore.gameMusicSoundPlayer.loop();
		AudioStore.fireBulletSoundPlayer.loop();
		paintThread = new PaintThread();
		paintThread.start();
	}

	/**
	 * @return
	 */
	public List<EnemyPlane> getEnemyPlanes() {
		return this.enemyPlanes;
	}

	/**
	 * @return
	 */
	public List<Bullet> getBullets() {
		return this.bullets;
	}

	//
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Container#paintComponents(java.awt.Graphics)
	 */
	@Override
	public void paintComponents(Graphics g) {
		super.paintComponents(g);
		drawScore(g);
		drawBomb(g);
		myPlane.draw(g);
		for (int i = 0; i < this.enemyPlanes.size(); i++) {
			EnemyPlane enemyPlane = this.enemyPlanes.get(i);
			enemyPlane.draw(g);
		}
		for (int i = 0; i < this.bullets.size(); i++) {
			Bullet b = this.bullets.get(i);
			b.draw(g);
		}
		if (this.popBomb != null && !this.popBomb.isWeaponDisappear()) {
			this.popBomb.draw(g);
		}
		if (this.popDoubleLaser != null && !this.popDoubleLaser.isWeaponDisappear()) {
			this.popDoubleLaser.draw(g);
		}
	}

	private void drawScore(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		List<Integer> intList = new ArrayList<Integer>();
		int scoreCopy = this.score;
		int quotient = 0;
		while ((quotient = scoreCopy / 10) != 0) {
			intList.add(scoreCopy % 10);
			scoreCopy = quotient;
		}
		intList.add(scoreCopy % 10);
		// draw
		int posX = Config.SCORE_IMG_POS_X;
		int posY = Config.SCORE_IMG_POS_Y;
		g2d.drawImage(ImageStore.SCORE_IMG, posX, posY, ImageConstants.SCORE_IMG_WIDTH, ImageConstants.SCORE_IMG_HEIGHT,
				this);
		posX += ImageConstants.SCORE_IMG_WIDTH;
		posY += ImageConstants.SCORE_IMG_HEIGHT - ImageConstants.NUMBER_0_HEIGHT;
		int size = intList.size();
		for (int i = size - 1; i >= 0; i--) {
			switch (intList.get(i)) {
			case Config.NUMBER_0:
				g2d.drawImage(ImageStore.NUMBER_0_IMG, posX, posY, ImageConstants.NUMBER_0_WIDTH,
						ImageConstants.NUMBER_0_HEIGHT, this);
				posX += ImageConstants.NUMBER_0_WIDTH;
				break;
			case Config.NUMBER_1:
				g2d.drawImage(ImageStore.NUMBER_1_IMG, posX, posY, ImageConstants.NUMBER_1_WIDTH,
						ImageConstants.NUMBER_1_HEIGHT, this);
				posX += ImageConstants.NUMBER_1_WIDTH;
				break;
			case Config.NUMBER_2:
				g2d.drawImage(ImageStore.NUMBER_2_IMG, posX, posY, ImageConstants.NUMBER_2_WIDTH,
						ImageConstants.NUMBER_2_HEIGHT, this);
				posX += ImageConstants.NUMBER_2_WIDTH;
				break;
			case Config.NUMBER_3:
				g2d.drawImage(ImageStore.NUMBER_3_IMG, posX, posY, ImageConstants.NUMBER_3_WIDTH,
						ImageConstants.NUMBER_3_HEIGHT, this);
				posX += ImageConstants.NUMBER_3_WIDTH;
				break;
			case Config.NUMBER_4:
				g2d.drawImage(ImageStore.NUMBER_4_IMG, posX, posY, ImageConstants.NUMBER_4_WIDTH,
						ImageConstants.NUMBER_4_HEIGHT, this);
				posX += ImageConstants.NUMBER_4_WIDTH;
				break;
			case Config.NUMBER_5:
				g2d.drawImage(ImageStore.NUMBER_5_IMG, posX, posY, ImageConstants.NUMBER_5_WIDTH,
						ImageConstants.NUMBER_5_HEIGHT, this);
				posX += ImageConstants.NUMBER_5_WIDTH;
				break;
			case Config.NUMBER_6:
				g2d.drawImage(ImageStore.NUMBER_6_IMG, posX, posY, ImageConstants.NUMBER_6_WIDTH,
						ImageConstants.NUMBER_6_HEIGHT, this);
				posX += ImageConstants.NUMBER_6_WIDTH;
				break;
			case Config.NUMBER_7:
				g2d.drawImage(ImageStore.NUMBER_7_IMG, posX, posY, ImageConstants.NUMBER_7_WIDTH,
						ImageConstants.NUMBER_7_HEIGHT, this);
				posX += ImageConstants.NUMBER_7_WIDTH;
				break;
			case Config.NUMBER_8:
				g2d.drawImage(ImageStore.NUMBER_8_IMG, posX, posY, ImageConstants.NUMBER_8_WIDTH,
						ImageConstants.NUMBER_8_HEIGHT, this);
				posX += ImageConstants.NUMBER_8_WIDTH;
				break;
			case Config.NUMBER_9:
				g2d.drawImage(ImageStore.NUMBER_9_IMG, posX, posY, ImageConstants.NUMBER_9_WIDTH,
						ImageConstants.NUMBER_9_HEIGHT, this);
				posX += ImageConstants.NUMBER_9_WIDTH;
				break;
			}
		}
	}

	private void drawBomb(Graphics g) {
		if (this.myPlane.getHoldBombCount() > 0) {
			Graphics2D g2d = (Graphics2D) g;
			int posX = Config.CAUGHT_BOMB_IMG_POS_X;
			int posY = Config.CAUGHT_BOMB_IMG_POS_Y;
			g2d.drawImage(ImageStore.CAUGHT_BOMB_IMG, posX, posY, ImageConstants.CAUGHT_BOMB_WIDTH,
					ImageConstants.CAUGHT_BOMB_HEIGHT, this);

			posX += ImageConstants.CAUGHT_BOMB_WIDTH;
			posY += (ImageConstants.CAUGHT_BOMB_HEIGHT - ImageConstants.X_MARK_HEIGHT) / 2;

			g2d.drawImage(ImageStore.X_MARK_IMG, posX, posY, ImageConstants.X_MARK_WIDTH, ImageConstants.X_MARK_HEIGHT,
					this);
			posX += ImageConstants.X_MARK_WIDTH;
			switch (this.myPlane.getHoldBombCount()) {
			case Config.ONE_BOMB:
				g2d.drawImage(ImageStore.NUMBER_1_IMG, posX, posY, ImageConstants.NUMBER_1_WIDTH,
						ImageConstants.NUMBER_1_HEIGHT, this);
				break;
			case Config.TWO_BOMB:
				g2d.drawImage(ImageStore.NUMBER_2_IMG, posX, posY, ImageConstants.NUMBER_2_WIDTH,
						ImageConstants.NUMBER_2_HEIGHT, this);
				break;
			case Config.THREE_BOMB:
				g2d.drawImage(ImageStore.NUMBER_3_IMG, posX, posY, ImageConstants.NUMBER_3_WIDTH,
						ImageConstants.NUMBER_3_HEIGHT, this);
				break;
			}

		}
	}

	// --自定义的一些事件

	/*
	 * (non-Javadoc)
	 * 
	 * @see me.maiz.game.myairbattle.listener.EnemyPlaneListener#
	 * onEnemyPlaneLocationChanged(me.maiz.game.myairbattle.model.EnemyPlane)
	 */
	@Override
	public void onEnemyPlaneLocationChanged(EnemyPlane p) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.maiz.game.myairbattle.listener.BulletListener#onBulletLocationChanged(
	 * me.maiz.game.myairbattle.model.Bullet)
	 */
	@Override
	public void onBulletLocationChanged(Bullet b) {
	}

	// ==============鼠标相关事件==========
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.
	 * MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
	}

	/*
	 * 鼠标移动
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (this.myPlane != null && this.myPlane.isAlive()) {
			myPlane.mouseMoved(e);
			this.repaint();
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

	private class PaintThread extends Thread {
		@Override
		public void run() {
			while (myPlane.isAlive()) {
				for (int i = 0; i < bullets.size(); i++) {
					Bullet b = bullets.get(i);
					onBulletLocationChanged(b);
				}

				for (int i = 0; i < enemyPlanes.size(); i++) {
					EnemyPlane enemyPlane = enemyPlanes.get(i);
					onEnemyPlaneLocationChanged(enemyPlane);
				}

				// 是否释放小飞机
				if (countdownTimer.remainTimeToPopSmallPlane > 0) {
					countdownTimer.remainTimeToPopSmallPlane -= Config.GAME_PANEL_REPAINT_INTERVAL;
				} else {
					// 释放一架小飞机
					EnemyPlane smallPlane = EnemyPlaneFactory.createEnemyPlane(GamePlayingScreen.this,
							EnemyPlaneType.SMALL_ENEMY_PLANE);
					synchronized (GamePlayingScreen.this.enemyPlanes) {
						enemyPlanes.add(smallPlane);
					}
					countdownTimer.remainTimeToPopSmallPlane = Config.POP_SMALL_ENEMY_PLANE_INTERVAL;
				}

				GamePlayingScreen.this.repaint();

				try {
					Thread.sleep(Config.GAME_PANEL_REPAINT_INTERVAL);
				} catch (InterruptedException e) {

				}

			}
		}
	}

}
