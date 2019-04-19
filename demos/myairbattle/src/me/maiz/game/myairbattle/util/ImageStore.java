package me.maiz.game.myairbattle.util;

import java.awt.Image;
import java.io.IOException;

import me.maiz.game.myairbattle.config.Config;

/**
 *
 * @author Lucas
 *
 * @Date 2018年10月17日 上午10:31:58
 *
 */
public class ImageStore {

	public static void loadImage() throws IOException {
		ImageCropper imgCropper = null;
		imgCropper = new ImageCropper(Config.SHOOT_BACKGROUND_IMG);
		GAME_LOADING_IMG1 = imgCropper.getImage(ImageConstants.GAME_LOADING_PLANE_1_POS_X,
				ImageConstants.GAME_LOADING_PLANE_1_POS_Y, ImageConstants.GAME_LOADING_PLANE_1_WIDTH,
				ImageConstants.GAME_LOADING_PLANE_1_HEIGHT);
		GAME_LOADING_IMG2 = imgCropper.getImage(ImageConstants.GAME_LOADING_PLANE_2_POS_X,
				ImageConstants.GAME_LOADING_PLANE_2_POS_Y, ImageConstants.GAME_LOADING_PLANE_2_WIDTH,
				ImageConstants.GAME_LOADING_PLANE_2_HEIGHT);
		GAME_LOADING_IMG3 = imgCropper.getImage(ImageConstants.GAME_LOADING_PLANE_3_POS_X,
				ImageConstants.GAME_LOADING_PLANE_3_POS_Y, ImageConstants.GAME_LOADING_PLANE_3_WIDTH,
				ImageConstants.GAME_LOADING_PLANE_3_HEIGHT);

		SHOOT_BACKGROUND_IMG = imgCropper.getImage(ImageConstants.GAME_BACKGROUND_IMG_POS_X,
				ImageConstants.GAME_BACKGROUND_IMG_POS_Y, ImageConstants.GAME_BACKGROUND_IMG_WIDTH,
				ImageConstants.GAME_BACKGROUND_IMG_HEIGHT);

		GAME_LOADING_TEXT_IMG = imgCropper.getImage(ImageConstants.GAME_LOADING_TEXT_IMG_POS_X,
				ImageConstants.GAME_LOADING_TEXT_IMG_POS_Y, ImageConstants.GAME_LOADING_TEXT_IMG_WIDTH,
				ImageConstants.GAME_LOADING_TEXT_IMG_HEIGHT);

		imgCropper = new ImageCropper(Config.SHOOT_IMG);
		YELLOW_BULLET_IMG = imgCropper.getImage(ImageConstants.YELLOW_BULLET_POS_X, ImageConstants.YELLOW_BULLET_POS_Y,
				ImageConstants.YELLOW_BULLET_WIDTH, ImageConstants.YELLOW_BULLET_HEIGHT);
		BLUE_BULLET_IMG = imgCropper.getImage(ImageConstants.BLUE_BULLET_POS_X, ImageConstants.BLUE_BULLET_POS_Y,
				ImageConstants.BLUE_BULLET_WIDTH, ImageConstants.BLUE_BULLET_HEIGHT);
		MY_PLANE_IMG = imgCropper.getImage(ImageConstants.MY_PLANE_POS_X, ImageConstants.MY_PLANE_POS_Y,
				ImageConstants.MY_PLANE_WIDTH, ImageConstants.MY_PLANE_HEIGHT);
		MY_PLANE_FLYING_IMG = imgCropper.getImage(ImageConstants.MY_PLANE_FLYING_POS_X,
				ImageConstants.MY_PLANE_FLYING_POS_Y, ImageConstants.MY_PLANE_FLYING_WIDTH,
				ImageConstants.MY_PLANE_FLYING_HEIGHT);
		SMALL_PLANE_IMG = imgCropper.getImage(ImageConstants.SMALL_PLANE_POS_X, ImageConstants.SMALL_PLANE_POS_Y,
				ImageConstants.SMALL_PLANE_WIDTH, ImageConstants.SMALL_PLANE_HEIGHT);
		BIG_PLANE_IMG = imgCropper.getImage(ImageConstants.BIG_PLANE_POS_X, ImageConstants.BIG_PLANE_POS_Y,
				ImageConstants.BIG_PLANE_WIDTH, ImageConstants.BIG_PLANE_HEIGHT);
		BOSS_PLANE_IMG = imgCropper.getImage(ImageConstants.BOSS_PLANE_POS_X, ImageConstants.BOSS_PLANE_POS_Y,
				ImageConstants.BOSS_PLANE_WIDTH, ImageConstants.BOSS_PLANE_HEIGHT);
		BOMB_IMG = imgCropper.getImage(ImageConstants.BOMB_POS_X, ImageConstants.BOMB_POS_Y, ImageConstants.BOMB_WIDTH,
				ImageConstants.BOMB_HEIGHT);
		CAUGHT_BOMB_IMG = imgCropper.getImage(ImageConstants.CAUGHT_BOMB_POS_X, ImageConstants.CAUGHT_BOMB_POS_Y,
				ImageConstants.CAUGHT_BOMB_WIDTH, ImageConstants.CAUGHT_BOMB_HEIGHT);
		DOUBLE_LASER_IMG = imgCropper.getImage(ImageConstants.DOUBLE_LASER_POS_X, ImageConstants.DOUBLE_LASER_POS_Y,
				ImageConstants.DOUBLE_LASER_WIDTH, ImageConstants.DOUBLE_LASER_HEIGHT);

		SMALL_PLANE_FIGHTING_IMG = imgCropper.getImage(ImageConstants.SMALL_PLANE_FIGHTING_POS_X,
				ImageConstants.SMALL_PLANE_FIGHTING_POS_Y, ImageConstants.SMALL_PLANE_FIGHTING_WIDTH,
				ImageConstants.SMALL_PLANE_FIGHTING_HEIGHT);
		SMALL_PLANE_KILLED_IMG = imgCropper.getImage(ImageConstants.SMALL_PLANE_KILLED_POS_X,
				ImageConstants.SMALL_PLANE_KILLED_POS_Y, ImageConstants.SMALL_PLANE_KILLED_WIDTH,
				ImageConstants.SMALL_PLANE_KILLED_HEIGHT);
		SMALL_PLANE_ASHED_IMG = imgCropper.getImage(ImageConstants.SMALL_PLANE_ASHED_POS_X,
				ImageConstants.SMALL_PLANE_ASHED_POS_Y, ImageConstants.SMALL_PLANE_ASHED_WIDTH,
				ImageConstants.SMALL_PLANE_ASHED_HEIGHT);

		BIG_PLANE_FIGHTING_IMG = imgCropper.getImage(ImageConstants.BIG_PLANE_FIGHTING_POS_X,
				ImageConstants.BIG_PLANE_FIGHTING_POS_Y, ImageConstants.BIG_PLANE_FIGHTING_WIDTH,
				ImageConstants.BIG_PLANE_FIGHTING_HEIGHT);
		BIG_PLANE_HITTED_IMG = imgCropper.getImage(ImageConstants.BIG_PLANE_HITTED_POS_X,
				ImageConstants.BIG_PLANE_HITTED_POS_Y, ImageConstants.BIG_PLANE_HITTED_WIDTH,
				ImageConstants.BIG_PLANE_HITTED_HEIGHT);
		BIG_PLANE_BADDLY_WOUNDED_IMG = imgCropper.getImage(ImageConstants.BIG_PLANE_BADDLY_WOUNDED_POS_X,
				ImageConstants.BIG_PLANE_BADDLY_WOUNDED_POS_Y, ImageConstants.BIG_PLANE_BADDLY_WOUNDED_WIDTH,
				ImageConstants.BIG_PLANE_BADDLY_WOUNDED_HEIGHT);
		BIG_PLANE_KILLED_IMG = imgCropper.getImage(ImageConstants.BIG_PLANE_KILLED_POS_X,
				ImageConstants.BIG_PLANE_KILLED_POS_Y, ImageConstants.BIG_PLANE_KILLED_WIDTH,
				ImageConstants.BIG_PLANE_KILLED_HEIGHT);
		BIG_PLANE_ASHED_IMG = imgCropper.getImage(ImageConstants.BIG_PLANE_ASHED_POS_X,
				ImageConstants.BIG_PLANE_ASHED_POS_Y, ImageConstants.BIG_PLANE_ASHED_WIDTH,
				ImageConstants.BIG_PLANE_ASHED_HEIGHT);

		BOSS_PLANE_FIGHTING_IMG = imgCropper.getImage(ImageConstants.BOSS_PLANE_FIGHTING_POS_X,
				ImageConstants.BOSS_PLANE_FIGHTING_POS_Y, ImageConstants.BOSS_PLANE_FIGHTING_WIDTH,
				ImageConstants.BOSS_PLANE_FIGHTING_HEIGHT);
		BOSS_PLANE_HITTED_IMG = imgCropper.getImage(ImageConstants.BOSS_PLANE_HITTED_POS_X,
				ImageConstants.BOSS_PLANE_HITTED_POS_Y, ImageConstants.BOSS_PLANE_HITTED_WIDTH,
				ImageConstants.BOSS_PLANE_HITTED_HEIGHT);
		BOSS_PLANE_BADDLY_WOUNDED_IMG = imgCropper.getImage(ImageConstants.BOSS_PLANE_BADDLY_WOUNDED_POS_X,
				ImageConstants.BOSS_PLANE_BADDLY_WOUNDED_POS_Y, ImageConstants.BOSS_PLANE_BADDLY_WOUNDED_WIDTH,
				ImageConstants.BOSS_PLANE_BADDLY_WOUNDED_HEIGHT);
		BOSS_PLANE_KILLED_IMG = imgCropper.getImage(ImageConstants.BOSS_PLANE_KILLED_POS_X,
				ImageConstants.BOSS_PLANE_KILLED_POS_Y, ImageConstants.BOSS_PLANE_KILLED_WIDTH,
				ImageConstants.BOSS_PLANE_KILLED_HEIGHT);
		BOSS_PLANE_ASHED_IMG = imgCropper.getImage(ImageConstants.BOSS_PLANE_ASHED_POS_X,
				ImageConstants.BOSS_PLANE_ASHED_POS_Y, ImageConstants.BOSS_PLANE_ASHED_WIDTH,
				ImageConstants.BOSS_PLANE_ASHED_HEIGHT);

		SCORE_IMG = imgCropper.getImage(ImageConstants.SCORE_IMG_POS_X, ImageConstants.SCORE_IMG_POS_Y,
				ImageConstants.SCORE_IMG_WIDTH, ImageConstants.SCORE_IMG_HEIGHT);

		imgCropper = new ImageCropper(Config.FONT_IMG);
		X_MARK_IMG = imgCropper.getImage(ImageConstants.X_MARK_POS_X, ImageConstants.X_MARK_POS_Y,
				ImageConstants.X_MARK_WIDTH, ImageConstants.X_MARK_HEIGHT);

		NUMBER_0_IMG = imgCropper.getImage(ImageConstants.NUMBER_0_POS_X, ImageConstants.NUMBER_0_POS_Y,
				ImageConstants.NUMBER_0_WIDTH, ImageConstants.NUMBER_0_HEIGHT);
		NUMBER_1_IMG = imgCropper.getImage(ImageConstants.NUMBER_1_POS_X, ImageConstants.NUMBER_1_POS_Y,
				ImageConstants.NUMBER_1_WIDTH, ImageConstants.NUMBER_1_HEIGHT);
		NUMBER_2_IMG = imgCropper.getImage(ImageConstants.NUMBER_2_POS_X, ImageConstants.NUMBER_2_POS_Y,
				ImageConstants.NUMBER_2_WIDTH, ImageConstants.NUMBER_2_HEIGHT);
		NUMBER_3_IMG = imgCropper.getImage(ImageConstants.NUMBER_3_POS_X, ImageConstants.NUMBER_3_POS_Y,
				ImageConstants.NUMBER_3_WIDTH, ImageConstants.NUMBER_3_HEIGHT);
		NUMBER_4_IMG = imgCropper.getImage(ImageConstants.NUMBER_4_POS_X, ImageConstants.NUMBER_4_POS_Y,
				ImageConstants.NUMBER_4_WIDTH, ImageConstants.NUMBER_4_HEIGHT);
		NUMBER_5_IMG = imgCropper.getImage(ImageConstants.NUMBER_5_POS_X, ImageConstants.NUMBER_5_POS_Y,
				ImageConstants.NUMBER_5_WIDTH, ImageConstants.NUMBER_5_HEIGHT);
		NUMBER_6_IMG = imgCropper.getImage(ImageConstants.NUMBER_6_POS_X, ImageConstants.NUMBER_6_POS_Y,
				ImageConstants.NUMBER_6_WIDTH, ImageConstants.NUMBER_6_HEIGHT);
		NUMBER_7_IMG = imgCropper.getImage(ImageConstants.NUMBER_7_POS_X, ImageConstants.NUMBER_7_POS_Y,
				ImageConstants.NUMBER_7_WIDTH, ImageConstants.NUMBER_7_HEIGHT);
		NUMBER_8_IMG = imgCropper.getImage(ImageConstants.NUMBER_8_POS_X, ImageConstants.NUMBER_8_POS_Y,
				ImageConstants.NUMBER_8_WIDTH, ImageConstants.NUMBER_8_HEIGHT);
		NUMBER_9_IMG = imgCropper.getImage(ImageConstants.NUMBER_9_POS_X, ImageConstants.NUMBER_9_POS_Y,
				ImageConstants.NUMBER_9_WIDTH, ImageConstants.NUMBER_9_HEIGHT);

	}

	public static Image GAME_LOADING_IMG1;
	public static Image GAME_LOADING_IMG2;
	public static Image GAME_LOADING_IMG3;

	public static Image GAME_LOADING_TEXT_IMG;

	public static Image SHOOT_BACKGROUND_IMG;

	public static Image MY_PLANE_IMG;
	public static Image MY_PLANE_FLYING_IMG;

	public static Image YELLOW_BULLET_IMG;
	public static Image BLUE_BULLET_IMG;

	public static Image SMALL_PLANE_IMG;
	public static Image BIG_PLANE_IMG;
	public static Image BOSS_PLANE_IMG;

	public static Image BOMB_IMG;
	public static Image DOUBLE_LASER_IMG;
	public static Image CAUGHT_BOMB_IMG;

	public static Image SMALL_PLANE_FIGHTING_IMG;
	public static Image SMALL_PLANE_KILLED_IMG;
	public static Image SMALL_PLANE_ASHED_IMG;

	public static Image BIG_PLANE_FIGHTING_IMG;
	public static Image BIG_PLANE_HITTED_IMG;
	public static Image BIG_PLANE_BADDLY_WOUNDED_IMG;
	public static Image BIG_PLANE_KILLED_IMG;
	public static Image BIG_PLANE_ASHED_IMG;

	public static Image BOSS_PLANE_FIGHTING_IMG;
	public static Image BOSS_PLANE_HITTED_IMG;
	public static Image BOSS_PLANE_BADDLY_WOUNDED_IMG;
	public static Image BOSS_PLANE_KILLED_IMG;
	public static Image BOSS_PLANE_ASHED_IMG;

	public static Image SCORE_IMG;

	public static Image X_MARK_IMG;

	public static Image NUMBER_0_IMG;
	public static Image NUMBER_1_IMG;
	public static Image NUMBER_2_IMG;
	public static Image NUMBER_3_IMG;
	public static Image NUMBER_4_IMG;
	public static Image NUMBER_5_IMG;
	public static Image NUMBER_6_IMG;
	public static Image NUMBER_7_IMG;
	public static Image NUMBER_8_IMG;
	public static Image NUMBER_9_IMG;
}
