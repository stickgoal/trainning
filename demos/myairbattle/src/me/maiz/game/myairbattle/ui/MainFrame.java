package me.maiz.game.myairbattle.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import me.maiz.game.myairbattle.config.Config;
import me.maiz.game.myairbattle.util.ImageStore;
import me.maiz.game.myairbattle.util.SoundPlayer;

/**
 *
 * @author Lucas
 *
 * @Date 2018年10月17日 上午10:25:43
 *
 */
public class MainFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 2370200547744994071L;
	/**
	 * 排行榜音频播放器
	 */
	private SoundPlayer achievementSoundPlayer;
	/**
	 * 游戏加载屏
	 */
	private LoadingScreen loadingScreen;

	/**
	 * 游戏主菜单屏
	 */
	private GameMenuScreen gameMenuScreen;

	/**
	 * 游戏屏
	 */
	private GamePlayingScreen gamePlayingScreen;

	/**
	 * 默认构造器
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 */
	public MainFrame() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
		ImageStore.loadImage();
		loadSound();
		showUI();
	}

	/**
	 * 初始化音频
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 */
	private void loadSound() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
		this.achievementSoundPlayer = new SoundPlayer(Config.ACHIEVEMENT_AUDIO);
	}

	/**
	 * 
	 */
	private void showUI() {
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

	/**
	 * 设置背景图片
	 */
	private void setBackgroundImage() {
		ImageIcon bgImgIcon = new ImageIcon(ImageStore.SHOOT_BACKGROUND_IMG);
		JLabel bgLabel = new JLabel(bgImgIcon);
		this.getLayeredPane().add(bgLabel, new Integer(Integer.MIN_VALUE));
		bgLabel.setBounds(0, 0, bgImgIcon.getIconWidth(), bgImgIcon.getIconHeight());
		((JPanel) this.getContentPane()).setOpaque(false);
	}

	/**
	 * 加载游戏
	 * 
	 * @throws InterruptedException
	 */
	public void loadGame() throws InterruptedException {
		Container c = clear();
		if (loadingScreen == null) {
			loadingScreen = new LoadingScreen();
		}
		BoxLayout boxLayout = new BoxLayout(c, BoxLayout.Y_AXIS);
		c.setLayout(boxLayout);
		c.add(Box.createVerticalGlue());
		c.add(loadingScreen);
		c.add(Box.createVerticalGlue());
		loadingScreen.loading();

		startGame();
	}

	private Container clear() {
		Container c = this.getContentPane();
		c.removeAll();
		this.repaint();
		return c;
	}

	/**
	 * 启动游戏
	 */
	private void startGame() {
		Container c = clear();
		gamePlayingScreen = new GamePlayingScreen();
		long score = play();
		die();
	}

	/**
	 * 游戏死处理
	 */
	private void die() {

	}

	/**
	 * 玩游戏
	 */
	private long play() {
		long begin = System.currentTimeMillis();
		gamePlayingScreen.start();
		long end = System.currentTimeMillis();
		return end - begin;
	}

	/**
	 * 弹出菜单页
	 */
	private void popupMenuPanel() {
		Container c = clear();
		if (gameMenuScreen == null) {
			gameMenuScreen = new GameMenuScreen(this);
		}
		c.add(gameMenuScreen);
		this.validate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {
		case Config.START_GAME_BUTTON:
			try {
				loadGame();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			break;
		case Config.EXIT_GAME_BUTTON:
			System.exit(0);
			break;
		case Config.TOP_10_SCORES_BUTTON:
			achievementSoundPlayer.play();
			JOptionPane.showConfirmDialog(this, "前十名分别为：");
			break;
		case Config.HELP_BUTTON:
			JOptionPane.showMessageDialog(this, "帮助来了！");
			break;
		default:
			System.err.println("系统出错！");
			break;
		}
	}

}