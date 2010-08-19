package com.github.pepe79.mfl;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;


public class AsynchronousListImageLoader extends BaseThread
{

	private List list;

	private String[] imageUrls;

	public AsynchronousListImageLoader(List list, String[] imageUrls)
	{
		this.list = list;
		this.imageUrls = imageUrls;
	}

	public void runSafe() throws InterruptedException
	{
		for (int i = 0; i < list.size(); i++)
		{
			Image image = null;

			HttpConnection connection = null;
			try
			{
				connection = (HttpConnection) Connector.open(imageUrls[i]);
				connection.setRequestMethod(HttpConnection.GET);
				image = Image.createImage(connection.openInputStream());
				list.set(i, list.getString(i), image);
			}
			catch (Exception e)
			{
				Logs.MFL.error(this.getClass(), "An error occured while downloading and creating images.", e);
			}
			finally
			{
				if (connection != null)
				{
					try
					{
						connection.close();
					}
					catch (IOException e)
					{
						Logs.MFL.error(this.getClass(), "An error occured while closing connection.", e);
					}
				}
			}
		}
	}

}
