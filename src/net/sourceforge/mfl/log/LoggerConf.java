package net.sourceforge.mfl.log;

import java.util.Vector;

public class LoggerConf
{
	private String classNamePrefix;

	private int logLevel = Logger.TRACE_LEVEL;

	private Vector appenders = new Vector();

	public LoggerConf()
	{
	}

	public LoggerConf(Appender appender)
	{
		addAppender(appender);
	}

	public void addAppender(Appender appender)
	{
		appenders.addElement(appender);
	}

	public Vector getAppenders()
	{
		return appenders;
	}

	public String getClassNamePrefix()
	{
		return classNamePrefix;
	}

	public void setClassNamePrefix(String classNamePrefix)
	{
		this.classNamePrefix = classNamePrefix;
	}

	public int getLogLevel()
	{
		return logLevel;
	}

	public void setLogLevel(int logLevel)
	{
		this.logLevel = logLevel;
	}

}
