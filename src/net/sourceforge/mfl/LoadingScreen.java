package net.sourceforge.mfl;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


public class LoadingScreen extends Canvas
{

	BaseThread animationThread;

	private int rotation = 0;

	public LoadingScreen()
	{
		StatusImages.initialize();

		setFullScreenMode(true);

		animationThread = new BaseThread()
		{
			public void runSafe()
			{
				while (isAlive())
				{
					if (LoadingScreen.this.isShown())
					{
						rotation++;
						repaint();
					}
					try
					{
						Thread.sleep(500);
					}
					catch (InterruptedException e)
					{
						// ignore
					}
				}
			}
		};
		animationThread.start();
	}

	protected void paint(Graphics g)
	{
		int width = getWidth();
		int height = getHeight();

		g.setColor(0);
		g.fillRect(0, 0, width, height);

		if (StatusImages.statusImages.length > 0)
		{
			Image image = StatusImages.statusImages[rotation % StatusImages.statusImages.length];
			g.drawImage(image, width / 2, height / 2, Graphics.VCENTER | Graphics.HCENTER);
		}
	}

}
