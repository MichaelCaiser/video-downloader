package net.sourceforge.mfl.log;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

public class FileAppender implements Appender
{
	private String filename;

	private DataOutputStream dataOutputStream;

	private FileConnection fileConnection = null;

	public FileAppender()
	{
		filename = "E:/mfl/mfl.txt";
	}

	public Appender init()
	{
		try
		{
			fileConnection = (FileConnection) Connector.open("file:///"
					+ filename);
			if (fileConnection.exists())
			{
				fileConnection.delete();
			}
			fileConnection.create();
			
			dataOutputStream = fileConnection.openDataOutputStream();
		} catch (IOException e)
		{
			System.err.println("Error while initializing file appender: "
					+ e.getMessage());
		}
		return this;
	}

	public DataOutputStream getDataOutputStream()
	{
		return dataOutputStream;
	}

	public void close()
	{
		if (fileConnection != null)
		{
			try
			{
				dataOutputStream.close();
				fileConnection.close();
			} catch (IOException e)
			{
				// ignore
			}
		}
	}

}
