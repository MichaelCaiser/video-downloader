package com.github.pepe79.mfl;

import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class StatusImages
{
	public static Image[] statusImages;

	public static synchronized void initialize()
	{
		if (statusImages == null)
		{
			try
			{
				Image loader = Image.createImage("/loader.png");

				statusImages = new Image[8];
				statusImages[0] = loader;
				statusImages[1] =
					Image.createImage(loader, 0, 0, loader.getWidth(), loader.getHeight(), Sprite.TRANS_MIRROR);
				statusImages[2] =
					Image.createImage(loader, 0, 0, loader.getWidth(), loader.getHeight(), Sprite.TRANS_ROT90);
				statusImages[3] =
					Image.createImage(loader, 0, 0, loader.getWidth(), loader.getHeight(), Sprite.TRANS_MIRROR_ROT90);
				statusImages[4] =
					Image.createImage(loader, 0, 0, loader.getWidth(), loader.getHeight(), Sprite.TRANS_ROT180);
				statusImages[5] =
					Image.createImage(loader, 0, 0, loader.getWidth(), loader.getHeight(), Sprite.TRANS_MIRROR_ROT180);
				statusImages[6] =
					Image.createImage(loader, 0, 0, loader.getWidth(), loader.getHeight(), Sprite.TRANS_ROT270);
				statusImages[7] =
					Image.createImage(loader, 0, 0, loader.getWidth(), loader.getHeight(), Sprite.TRANS_MIRROR_ROT270);
			}
			catch (IOException e)
			{
				statusImages = new Image[0];
			}
		}
	}
}
