package me.maiz.game.myairbattle.factory;

import java.util.Random;

import me.maiz.game.myairbattle.config.CatchableWeaponType;
import me.maiz.game.myairbattle.config.Config;
import me.maiz.game.myairbattle.model.Bomb;
import me.maiz.game.myairbattle.model.CatchableWeapon;
import me.maiz.game.myairbattle.model.DoubleLaser;
import me.maiz.game.myairbattle.ui.GamePlayingScreen;
import me.maiz.game.myairbattle.util.ImageConstants;
import me.maiz.game.myairbattle.util.ImageStore;

public class CatchableWeaponFactory {
	public static final Random rand = new Random();

	public static CatchableWeapon createCatchableWeapon(GamePlayingScreen playingPanel,
			CatchableWeaponType weaponType) {
		CatchableWeapon weapon = null;
		switch (weaponType) {
		case BOMB:
			weapon = new Bomb(playingPanel, weaponType);
			weapon.setWidth(ImageConstants.BOMB_WIDTH);
			weapon.setHeight(ImageConstants.BOMB_HEIGHT);
			weapon.setWeaponImage(ImageStore.BOMB_IMG);
			weapon.setSpeed(Config.POP_WEAPON_MOVE_SPEED);
			break;
		case DOUBLE_LASER:
			weapon = new DoubleLaser(playingPanel, weaponType);
			weapon.setWidth(ImageConstants.DOUBLE_LASER_WIDTH);
			weapon.setHeight(ImageConstants.DOUBLE_LASER_HEIGHT);
			weapon.setWeaponImage(ImageStore.DOUBLE_LASER_IMG);
			weapon.setSpeed(Config.POP_WEAPON_MOVE_SPEED);
			break;
		}

		int posX = rand.nextInt(playingPanel.getWidth() - weapon.getWidth());
		int posY = 0;
		weapon.setPosX(posX);
		weapon.setPosY(posY);

		return weapon;
	}
}