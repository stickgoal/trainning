package me.maiz.game.myairbattle.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import me.maiz.game.myairbattle.config.Config;
import me.maiz.game.myairbattle.util.ImageConstants;
import me.maiz.game.myairbattle.util.ImageLoader;
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
	 * 游戏启动音频播放器
	 */
	private SoundPlayer achievementSoundPlayer;

	/**
	 * 游戏主菜单
	 */
	private GameMenuScreen gameMenuScreen;

	/**
	 * 默认构造器
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 */
	public MainFrame() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
		loadImages();
		initAchievementSoundPlayer();
		showUI();
	}

	/**
	 * 初始化
	 * 
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 */
	private void initAchievementSoundPlayer()
			throws LineUnavailableException, UnsupportedAudioFileException, IOException {
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

	private void setBackgroundImage() {
		ImageIcon bgImgIcon = new ImageIcon(ImageStore.SHOOT_BACKGROUND_IMG);
		JLabel bgLabel = new JLabel(bgImgIcon);
		this.getLayeredPane().add(bgLabel, new Integer(Integer.MIN_VALUE));
		bgLabel.setBounds(0, 0, bgImgIcon.getIconWidth(), bgImgIcon.getIconHeight());
		((JPanel) this.getContentPane()).setOpaque(false);
	}

	private void loadImages() throws IOException {
		ImageLoader imgLoader = null;
		imgLoader = new ImageLoader(Config.SHOOT_BACKGROUND_IMG);
		ImageStore.GAME_LOADING_IMG1 = imgLoader.getImage(ImageConstants.GAME_LOADING_PLANE_1_POS_X,
				ImageConstants.GAME_LOADING_PLANE_1_POS_Y, ImageConstants.GAME_LOADING_PLANE_1_WIDTH,
				ImageConstants.GAME_LOADING_PLANE_1_HEIGHT);
		ImageStore.GAME_LOADING_IMG2 = imgLoader.getImage(ImageConstants.GAME_LOADING_PLANE_2_POS_X,
				ImageConstants.GAME_LOADING_PLANE_2_POS_Y, ImageConstants.GAME_LOADING_PLANE_2_WIDTH,
				ImageConstants.GAME_LOADING_PLANE_2_HEIGHT);
		ImageStore.GAME_LOADING_IMG3 = imgLoader.getImage(ImageConstants.GAME_LOADING_PLANE_3_POS_X,
				ImageConstants.GAME_LOADING_PLANE_3_POS_Y, ImageConstants.GAME_LOADING_PLANE_3_WIDTH,
				ImageConstants.GAME_LOADING_PLANE_3_HEIGHT);

		ImageStore.SHOOT_BACKGROUND_IMG = imgLoader.getImage(ImageConstants.GAME_BACKGROUND_IMG_POS_X,
				ImageConstants.GAME_BACKGROUND_IMG_POS_Y, ImageConstants.GAME_BACKGROUND_IMG_WIDTH,
				ImageConstants.GAME_BACKGROUND_IMG_HEIGHT);

		ImageStore.GAME_LOADING_TEXT_IMG = imgLoader.getImage(ImageConstants.GAME_LOADING_TEXT_IMG_POS_X,
				ImageConstants.GAME_LOADING_TEXT_IMG_POS_Y, ImageConstants.GAME_LOADING_TEXT_IMG_WIDTH,
				ImageConstants.GAME_LOADING_TEXT_IMG_HEIGHT);

		imgLoader = new ImageLoader(Config.SHOOT_IMG);
		ImageStore.YELLOW_BULLET_IMG = imgLoader.getImage(ImageConstants.YELLOW_BULLET_POS_X,
				ImageConstants.YELLOW_BULLET_POS_Y, ImageConstants.YELLOW_BULLET_WIDTH,
				ImageConstants.YELLOW_BULLET_HEIGHT);
		ImageStore.BLUE_BULLET_IMG = imgLoader.getImage(ImageConstants.BLUE_BULLET_POS_X,
				ImageConstants.BLUE_BULLET_POS_Y, ImageConstants.BLUE_BULLET_WIDTH, ImageConstants.BLUE_BULLET_HEIGHT);
		ImageStore.MY_PLANE_IMG = imgLoader.getImage(ImageConstants.MY_PLANE_POS_X, ImageConstants.MY_PLANE_POS_Y,
				ImageConstants.MY_PLANE_WIDTH, ImageConstants.MY_PLANE_HEIGHT);
		ImageStore.MY_PLANE_FLYING_IMG = imgLoader.getImage(ImageConstants.MY_PLANE_FLYING_POS_X,
				ImageConstants.MY_PLANE_FLYING_POS_Y, ImageConstants.MY_PLANE_FLYING_WIDTH,
				ImageConstants.MY_PLANE_FLYING_HEIGHT);
		ImageStore.SMALL_PLANE_IMG = imgLoader.getImage(ImageConstants.SMALL_PLANE_POS_X,
				ImageConstants.SMALL_PLANE_POS_Y, ImageConstants.SMALL_PLANE_WIDTH, ImageConstants.SMALL_PLANE_HEIGHT);
		ImageStore.BIG_PLANE_IMG = imgLoader.getImage(ImageConstants.BIG_PLANE_POS_X, ImageConstants.BIG_PLANE_POS_Y,
				ImageConstants.BIG_PLANE_WIDTH, ImageConstants.BIG_PLANE_HEIGHT);
		ImageStore.BOSS_PLANE_IMG = imgLoader.getImage(ImageConstants.BOSS_PLANE_POS_X, ImageConstants.BOSS_PLANE_POS_Y,
				ImageConstants.BOSS_PLANE_WIDTH, ImageConstants.BOSS_PLANE_HEIGHT);
		ImageStore.BOMB_IMG = imgLoader.getImage(ImageConstants.BOMB_POS_X, ImageConstants.BOMB_POS_Y,
				ImageConstants.BOMB_WIDTH, ImageConstants.BOMB_HEIGHT);
		ImageStore.CAUGHT_BOMB_IMG = imgLoader.getImage(ImageConstants.CAUGHT_BOMB_POS_X,
				ImageConstants.CAUGHT_BOMB_POS_Y, ImageConstants.CAUGHT_BOMB_WIDTH, ImageConstants.CAUGHT_BOMB_HEIGHT);
		ImageStore.DOUBLE_LASER_IMG = imgLoader.getImage(ImageConstants.DOUBLE_LASER_POS_X,
				ImageConstants.DOUBLE_LASER_POS_Y, ImageConstants.DOUBLE_LASER_WIDTH,
				ImageConstants.DOUBLE_LASER_HEIGHT);

		ImageStore.SMALL_PLANE_FIGHTING_IMG = imgLoader.getImage(ImageConstants.SMALL_PLANE_FIGHTING_POS_X,
				ImageConstants.SMALL_PLANE_FIGHTING_POS_Y, ImageConstants.SMALL_PLANE_FIGHTING_WIDTH,
				ImageConstants.SMALL_PLANE_FIGHTING_HEIGHT);
		ImageStore.SMALL_PLANE_KILLED_IMG = imgLoader.getImage(ImageConstants.SMALL_PLANE_KILLED_POS_X,
				ImageConstants.SMALL_PLANE_KILLED_POS_Y, ImageConstants.SMALL_PLANE_KILLED_WIDTH,
				ImageConstants.SMALL_PLANE_KILLED_HEIGHT);
		ImageStore.SMALL_PLANE_ASHED_IMG = imgLoader.getImage(ImageConstants.SMALL_PLANE_ASHED_POS_X,
				ImageConstants.SMALL_PLANE_ASHED_POS_Y, ImageConstants.SMALL_PLANE_ASHED_WIDTH,
				ImageConstants.SMALL_PLANE_ASHED_HEIGHT);

		ImageStore.BIG_PLANE_FIGHTING_IMG = imgLoader.getImage(ImageConstants.BIG_PLANE_FIGHTING_POS_X,
				ImageConstants.BIG_PLANE_FIGHTING_POS_Y, ImageConstants.BIG_PLANE_FIGHTING_WIDTH,
				ImageConstants.BIG_PLANE_FIGHTING_HEIGHT);
		ImageStore.BIG_PLANE_HITTED_IMG = imgLoader.getImage(ImageConstants.BIG_PLANE_HITTED_POS_X,
				ImageConstants.BIG_PLANE_HITTED_POS_Y, ImageConstants.BIG_PLANE_HITTED_WIDTH,
				ImageConstants.BIG_PLANE_HITTED_HEIGHT);
		ImageStore.BIG_PLANE_BADDLY_WOUNDED_IMG = imgLoader.getImage(ImageConstants.BIG_PLANE_BADDLY_WOUNDED_POS_X,
				ImageConstants.BIG_PLANE_BADDLY_WOUNDED_POS_Y, ImageConstants.BIG_PLANE_BADDLY_WOUNDED_WIDTH,
				ImageConstants.BIG_PLANE_BADDLY_WOUNDED_HEIGHT);
		ImageStore.BIG_PLANE_KILLED_IMG = imgLoader.getImage(ImageConstants.BIG_PLANE_KILLED_POS_X,
				ImageConstants.BIG_PLANE_KILLED_POS_Y, ImageConstants.BIG_PLANE_KILLED_WIDTH,
				ImageConstants.BIG_PLANE_KILLED_HEIGHT);
		ImageStore.BIG_PLANE_ASHED_IMG = imgLoader.getImage(ImageConstants.BIG_PLANE_ASHED_POS_X,
				ImageConstants.BIG_PLANE_ASHED_POS_Y, ImageConstants.BIG_PLANE_ASHED_WIDTH,
				ImageConstants.BIG_PLANE_ASHED_HEIGHT);

		ImageStore.BOSS_PLANE_FIGHTING_IMG = imgLoader.getImage(ImageConstants.BOSS_PLANE_FIGHTING_POS_X,
				ImageConstants.BOSS_PLANE_FIGHTING_POS_Y, ImageConstants.BOSS_PLANE_FIGHTING_WIDTH,
				ImageConstants.BOSS_PLANE_FIGHTING_HEIGHT);
		ImageStore.BOSS_PLANE_HITTED_IMG = imgLoader.getImage(ImageConstants.BOSS_PLANE_HITTED_POS_X,
				ImageConstants.BOSS_PLANE_HITTED_POS_Y, ImageConstants.BOSS_PLANE_HITTED_WIDTH,
				ImageConstants.BOSS_PLANE_HITTED_HEIGHT);
		ImageStore.BOSS_PLANE_BADDLY_WOUNDED_IMG = imgLoader.getImage(ImageConstants.BOSS_PLANE_BADDLY_WOUNDED_POS_X,
				ImageConstants.BOSS_PLANE_BADDLY_WOUNDED_POS_Y, ImageConstants.BOSS_PLANE_BADDLY_WOUNDED_WIDTH,
				ImageConstants.BOSS_PLANE_BADDLY_WOUNDED_HEIGHT);
		ImageStore.BOSS_PLANE_KILLED_IMG = imgLoader.getImage(ImageConstants.BOSS_PLANE_KILLED_POS_X,
				ImageConstants.BOSS_PLANE_KILLED_POS_Y, ImageConstants.BOSS_PLANE_KILLED_WIDTH,
				ImageConstants.BOSS_PLANE_KILLED_HEIGHT);
		ImageStore.BOSS_PLANE_ASHED_IMG = imgLoader.getImage(ImageConstants.BOSS_PLANE_ASHED_POS_X,
				ImageConstants.BOSS_PLANE_ASHED_POS_Y, ImageConstants.BOSS_PLANE_ASHED_WIDTH,
				ImageConstants.BOSS_PLANE_ASHED_HEIGHT);

		ImageStore.SCORE_IMG = imgLoader.getImage(ImageConstants.SCORE_IMG_POS_X, ImageConstants.SCORE_IMG_POS_Y,
				ImageConstants.SCORE_IMG_WIDTH, ImageConstants.SCORE_IMG_HEIGHT);

		imgLoader = new ImageLoader(Config.FONT_IMG);
		ImageStore.X_MARK_IMG = imgLoader.getImage(ImageConstants.X_MARK_POS_X, ImageConstants.X_MARK_POS_Y,
				ImageConstants.X_MARK_WIDTH, ImageConstants.X_MARK_HEIGHT);

		ImageStore.NUMBER_0_IMG = imgLoader.getImage(ImageConstants.NUMBER_0_POS_X, ImageConstants.NUMBER_0_POS_Y,
				ImageConstants.NUMBER_0_WIDTH, ImageConstants.NUMBER_0_HEIGHT);
		ImageStore.NUMBER_1_IMG = imgLoader.getImage(ImageConstants.NUMBER_1_POS_X, ImageConstants.NUMBER_1_POS_Y,
				ImageConstants.NUMBER_1_WIDTH, ImageConstants.NUMBER_1_HEIGHT);
		ImageStore.NUMBER_2_IMG = imgLoader.getImage(ImageConstants.NUMBER_2_POS_X, ImageConstants.NUMBER_2_POS_Y,
				ImageConstants.NUMBER_2_WIDTH, ImageConstants.NUMBER_2_HEIGHT);
		ImageStore.NUMBER_3_IMG = imgLoader.getImage(ImageConstants.NUMBER_3_POS_X, ImageConstants.NUMBER_3_POS_Y,
				ImageConstants.NUMBER_3_WIDTH, ImageConstants.NUMBER_3_HEIGHT);
		ImageStore.NUMBER_4_IMG = imgLoader.getImage(ImageConstants.NUMBER_4_POS_X, ImageConstants.NUMBER_4_POS_Y,
				ImageConstants.NUMBER_4_WIDTH, ImageConstants.NUMBER_4_HEIGHT);
		ImageStore.NUMBER_5_IMG = imgLoader.getImage(ImageConstants.NUMBER_5_POS_X, ImageConstants.NUMBER_5_POS_Y,
				ImageConstants.NUMBER_5_WIDTH, ImageConstants.NUMBER_5_HEIGHT);
		ImageStore.NUMBER_6_IMG = imgLoader.getImage(ImageConstants.NUMBER_6_POS_X, ImageConstants.NUMBER_6_POS_Y,
				ImageConstants.NUMBER_6_WIDTH, ImageConstants.NUMBER_6_HEIGHT);
		ImageStore.NUMBER_7_IMG = imgLoader.getImage(ImageConstants.NUMBER_7_POS_X, ImageConstants.NUMBER_7_POS_Y,
				ImageConstants.NUMBER_7_WIDTH, ImageConstants.NUMBER_7_HEIGHT);
		ImageStore.NUMBER_8_IMG = imgLoader.getImage(ImageConstants.NUMBER_8_POS_X, ImageConstants.NUMBER_8_POS_Y,
				ImageConstants.NUMBER_8_WIDTH, ImageConstants.NUMBER_8_HEIGHT);
		ImageStore.NUMBER_9_IMG = imgLoader.getImage(ImageConstants.NUMBER_9_POS_X, ImageConstants.NUMBER_9_POS_Y,
				ImageConstants.NUMBER_9_WIDTH, ImageConstants.NUMBER_9_HEIGHT);

	}

	/**
	 * 启动游戏
	 */
	public void loadGame() {
		Container c = this.getContentPane();
		JPanel loadingPanel = new JPanel();
		loadingPanel.setOpaque(false);
		JLabel l = new JLabel();
		l.setOpaque(false);
		l.setForeground(Color.red);
		l.setFont(new Font("楷体", Font.BOLD, 120));
		loadingPanel.add(l);
		c.add(loadingPanel);
		l.setText("！空战！");

		for (int i = 3; i > 0; i--) {
			// 600毫秒后换图片，形成一个动画效果

			try {
				Thread.sleep(Config.GAME_LOADING_INTERVAL);
			} catch (Exception e) {
				e.printStackTrace();
			}
			l.setText("" + i);
		}
		try {
			Thread.sleep(Config.GAME_LOADING_INTERVAL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		l.setText("开战！");
		achievementSoundPlayer.play();

		popupMenuPanel();
	}

	/**
	 * 弹出菜单页
	 */
	private void popupMenuPanel() {
		Container c = this.getContentPane();
		c.removeAll();
		this.repaint();
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
			loadGame();
			break;
		case Config.EXIT_GAME_BUTTON:
			System.exit(0);
			break;
		case Config.TOP_10_SCORES_BUTTON:
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
