package com.github.pepe79.mfl.net;

import com.github.pepe79.mfl.Logs;
import com.github.pepe79.mfl.log.LogSystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;



public class ConnectionHelper
{

	public static void copyData(String url, OutputStream os, MIDlet m, int allowedRedirectDeepness,
		CompletionListener completionListener) throws IOException
	{
		HttpConnection con = null;
		Alert a = null;
		String redirectLocation = null;
		try
		{

			Logs.MFL.debug(ConnectionHelper.class, "Open connection to '" + url + "'");

			con = (HttpConnection) Connector.open(url);
			con
				.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 6.0; de; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)");

			if (HttpConnection.HTTP_OK == con.getResponseCode())
			{
				Logs.MFL.debug(ConnectionHelper.class, "Response OK");

				if (completionListener != null)
				{
					completionListener.completed(0);
				}

				long max = con.getLength();
				long downloadedBytes = 0;
				InputStream is = con.openInputStream();
				byte[] data = new byte[2048];
				int dataLength = 0;
				int completedPercentage = 0;

				boolean load = true;
				while (load)
				{
					try
					{
						dataLength = 0;
//						Logs.MFL.info(ConnectionHelper.class, "available=" + is.available());
						dataLength = is.read(data);
					}
					catch (IOException e)
					{
						Logs.MFL.error(ConnectionHelper.class, "An error occured during copyData", e);
					}
					if (dataLength < 0)
					{
						break;
					}
					if (dataLength > 0)
					{
						os.write(data, 0, dataLength);
						if (completionListener != null && max > 0)
						{
							downloadedBytes += dataLength;
							int percentage = (int) ((downloadedBytes * 100) / max);

							if (completedPercentage < percentage)
							{
								completedPercentage = percentage;
								completionListener.completed(completedPercentage);
							}
						}
					}
				}

				if (completionListener != null)
				{
					completionListener.completed(100);
					completionListener.ready();
				}
			}
			else
			{
				redirectLocation = con.getHeaderField("Location");
				if (redirectLocation != null)
				{
					Logs.MFL.debug(ConnectionHelper.class, "Redirect to '" + redirectLocation + "' requested.");
				}

				if (allowedRedirectDeepness == 0 || redirectLocation == null)
				{
					String message = "Response not expected: location=" + redirectLocation + ": " + con.getResponseMessage();
					Logs.MFL.warn(ConnectionHelper.class, message);

					if (m != null)
					{
						a = new Alert("Response NOT OK", message, null, AlertType.INFO);
						a.setTimeout(Alert.FOREVER);
						Display.getDisplay(m).setCurrent(a);
						try
						{
							Thread.sleep(10000);
						}
						catch (InterruptedException e)
						{
							// ignore
						}
					}
				}
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
			Logs.MFL.error(ConnectionHelper.class, "An error occured while loading data from '" + url + "'", e);
			throw e;
		}
		finally
		{
			if (con != null)
			{
				try
				{
					con.close();
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}
		if (allowedRedirectDeepness > 0 && redirectLocation != null)
		{
			copyData(redirectLocation, os, m, allowedRedirectDeepness - 1, completionListener);
		}
	}
}
