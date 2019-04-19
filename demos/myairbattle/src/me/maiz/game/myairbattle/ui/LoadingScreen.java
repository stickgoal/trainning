package me.maiz.game.myairbattle.ui;

import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import me.maiz.game.myairbattle.util.ImageStore;

/**
 * 游戏加载页<br>
 * 在页面上显示“飞机大战”四个大字和一个表示加载进度的动画
 * 
 * @author Lucas
 *
 * @Date 2018年10月17日 上午11:38:08
 *
 */
public class LoadingScreen extends JPanel {

	private static final long serialVersionUID = -2102141819812234952L;
	// 放置文字图片的panel
	private JLabel gameLoadingTextLabel;
	// 游戏加载动画的panel
	private JLabel gameLoadingImageLabel;
	// 游戏加载动画的图片数组
	private ImageIcon[] imageIcons = new ImageIcon[3];

	/**
	 * 默认构造器
	 */
	public LoadingScreen() {
		// 储存好三张图片
		imageIcons[0] = new ImageIcon(ImageStore.GAME_LOADING_IMG1);
		imageIcons[1] = new ImageIcon(ImageStore.GAME_LOADING_IMG2);
		imageIcons[2] = new ImageIcon(ImageStore.GAME_LOADING_IMG3);
		// 放置文字标签
		gameLoadingTextLabel = new JLabel(new ImageIcon(ImageStore.GAME_LOADING_TEXT_IMG));
		gameLoadingTextLabel.setOpaque(false);
		JPanel textPanel = new JPanel();
		textPanel.setOpaque(false);
		textPanel.add(gameLoadingTextLabel);

		// 放置图片标签
		gameLoadingImageLabel = new JLabel();
		JPanel imagePanel = new JPanel();
		imagePanel.setOpaque(false);
		imagePanel.add(gameLoadingImageLabel);

		// GridLayout 从上到下，从左到右排列
		GridLayout gridLayout = new GridLayout(2, 1);
		this.setLayout(gridLayout);
		this.setOpaque(false);
		this.add(imagePanel);
		this.add(textPanel);
	}

	public void loading() throws InterruptedException {
		for (int i = 0; i < imageIcons.length; i++) {
			gameLoadingImageLabel.setIcon(imageIcons[i]);
			Thread.sleep(600);
		}
	}

}
