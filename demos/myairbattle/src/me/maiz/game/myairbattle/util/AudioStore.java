package me.maiz.game.myairbattle.util;

import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import me.maiz.game.myairbattle.config.Config;

/**
 *
 * @author Lucas
 *
 * @Date 2018年10月17日 下午5:13:38
 *
 */
public class AudioStore {
	public static SoundPlayer SMALL_PLANE_KILLED_SOUND_PLAYER;
	public static SoundPlayer BIG_PLANE_KILLED_SOUND_PLAYER;
	public static SoundPlayer bossPlaneKilledSoundPlayer;
	public static SoundPlayer bossPlaneFlyingSoundPlayer;
	public static SoundPlayer popWeaponSoundPlayer;
	public static SoundPlayer fireBulletSoundPlayer;
	public static SoundPlayer useBombSoundPlayer;
	public static SoundPlayer getDoubleLaserSoundPlayer;
	public static SoundPlayer getBombSoundPlayer;
	public static SoundPlayer gameMusicSoundPlayer;
	public static SoundPlayer gameOverSoundPlayer;

	public static void loadAudio() {
		try {
			SMALL_PLANE_KILLED_SOUND_PLAYER = new SoundPlayer(Config.SMALL_PLANE_KILLED_AUDIO);
			BIG_PLANE_KILLED_SOUND_PLAYER = new SoundPlayer(Config.BIG_PLANE_KILLED_AUDIO);
			bossPlaneKilledSoundPlayer = new SoundPlayer(Config.BOSS_PLANE_KILLED_AUDIO);
			bossPlaneFlyingSoundPlayer = new SoundPlayer(Config.BOSS_PLANE_FLYING_AUDIO);
			popWeaponSoundPlayer = new SoundPlayer(Config.POP_WEAPON_AUDIO);
			fireBulletSoundPlayer = new SoundPlayer(Config.FIRE_BULLET_AUDIO);
			useBombSoundPlayer = new SoundPlayer(Config.USER_BOMB_AUDIO);
			getDoubleLaserSoundPlayer = new SoundPlayer(Config.GET_DOUBLE_LASER_AUDIO);
			getBombSoundPlayer = new SoundPlayer(Config.GET_BOMB_AUDIO);
			gameMusicSoundPlayer = new SoundPlayer(Config.GAME_MUSIC_AUDIO);
			gameOverSoundPlayer = new SoundPlayer(Config.GAME_OVER_AUDIO);
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
	}
}
