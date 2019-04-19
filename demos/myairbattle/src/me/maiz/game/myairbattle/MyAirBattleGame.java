package me.maiz.game.myairbattle;

import javax.swing.JOptionPane;

import me.maiz.game.myairbattle.ui.MainFrame;

/**
 *
 * @author Lucas
 *
 * @Date 2018年10月17日 上午10:27:51
 *
 */
public class MyAirBattleGame {

	public static void main(String[] args) {
		try {
			MainFrame frame = new MainFrame();
			frame.loadGame();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "游戏加载出错...");
		}
	}
}
