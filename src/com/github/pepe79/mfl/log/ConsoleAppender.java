package com.github.pepe79.mfl.log;

import java.io.DataOutputStream;

public class ConsoleAppender implements Appender
{
	private DataOutputStream dataOutputStream;

	public ConsoleAppender()
	{
		this.dataOutputStream = new DataOutputStream(System.out);
	}

	public Appender init()
	{
		return this;
	}

	public DataOutputStream getDataOutputStream()
	{
		return dataOutputStream;
	}

	public void close()
	{
	}
}
