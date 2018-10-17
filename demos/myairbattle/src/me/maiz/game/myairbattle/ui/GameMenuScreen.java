package me.maiz.game.myairbattle.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import me.maiz.game.myairbattle.config.Config;
import me.maiz.game.myairbattle.util.ImageStore;

/**
 *
 * @author Lucas
 *
 * @Date 2018年10月17日 下午2:39:09
 *
 */
public class GameMenuScreen extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel logoLabel;
	private GameButton startGameButton;
	private GameButton exitGameButton;
	private GameButton top10ScoresButton;
	private GameButton helpButton;

	/**
	 * @param mainFrame
	 * 
	 */
	public GameMenuScreen(MainFrame mainFrame) {
		this.logoLabel = new JLabel();
		this.logoLabel.setIcon(new ImageIcon(ImageStore.MY_PLANE_IMG));
		// 添加4个按钮，使用MainFrame处理鼠标事件
		this.startGameButton = new GameButton("新游戏！");
		this.startGameButton.addActionListener(mainFrame);
		this.startGameButton.setActionCommand(Config.START_GAME_BUTTON);
		this.startGameButton.setOpaque(false);

		this.top10ScoresButton = new GameButton("前十名");
		this.top10ScoresButton.addActionListener(mainFrame);
		this.top10ScoresButton.setActionCommand(Config.TOP_10_SCORES_BUTTON);
		this.top10ScoresButton.setOpaque(false);

		this.helpButton = new GameButton("帮助");
		this.helpButton.addActionListener(mainFrame);
		this.helpButton.setActionCommand(Config.HELP_BUTTON);
		this.helpButton.setOpaque(false);

		this.exitGameButton = new GameButton("退出游戏");
		this.exitGameButton.addActionListener(mainFrame);
		this.exitGameButton.setActionCommand(Config.EXIT_GAME_BUTTON);
		this.exitGameButton.setOpaque(false);

		JPanel logoPanel = new JPanel();
		logoPanel.setOpaque(false);
		logoPanel.add(logoLabel);

		GridLayout gridLayout = new GridLayout(4, 1, 0, 10);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(gridLayout);

		buttonPanel.add(startGameButton);
		buttonPanel.add(top10ScoresButton);
		buttonPanel.add(helpButton);
		buttonPanel.add(exitGameButton);

		Dimension d = new Dimension(Config.POP_UP_MENU_PANEL_WIDTH, Config.POP_UP_MENU_PANEL_HEIGHT);
		buttonPanel.setSize(d);
		buttonPanel.setPreferredSize(d);

		BorderLayout mainLayout = new BorderLayout();
		mainLayout.setVgap(25);
		JPanel mainPanel = new JPanel();
		mainPanel.setOpaque(false);
		mainPanel.setLayout(mainLayout);
		mainPanel.add(logoPanel, BorderLayout.NORTH);
		mainPanel.add(buttonPanel, BorderLayout.CENTER);

		this.setOpaque(false);
		this.add(mainPanel);
	}
}
