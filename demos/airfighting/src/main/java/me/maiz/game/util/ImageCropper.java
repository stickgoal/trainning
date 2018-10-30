package me.maiz.game.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * This class is used to load the picture of each item from a whole picture
 */
public class ImageCropper {

	private BufferedImage sourceImg;

	public ImageCropper(String imagePath) throws IOException {
		sourceImg = ImageIO.read(new File(imagePath));

	}

	public Image getImage(int posX, int posY, int width, int height) {
		BufferedImage targetImg = this.sourceImg.getSubimage(posX, posY, width, height);
		Image img = new ImageIcon(targetImg).getImage();
		return img;
	}

}
