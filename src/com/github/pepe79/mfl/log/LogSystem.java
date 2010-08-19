package com.github.pepe79.mfl.log;

import java.util.Vector;


public class LogSystem
{
	private static Vector loggerConfs = new Vector();

	private static final boolean ENABLED = true;

	private static final String ENCODING = "UTF-8";

	static
	{
		if (ENABLED)
		{
			LoggerConf rootLoggerConf = new LoggerConf();
			rootLoggerConf.addAppender(new ConsoleAppender().init());
			rootLoggerConf.setLogLevel(Logger.TRACE_LEVEL);
			loggerConfs.addElement(rootLoggerConf);
		}
	}

	public static Vector getLoggerConfs()
	{
		return loggerConfs;
	}

	public static LoggerConf getLoggerConf(String className)
	{
		for (int i = 0; i < loggerConfs.size(); i++)
		{
			LoggerConf loggerConf = (LoggerConf) loggerConfs.elementAt(i);
			if (loggerConf.getClassNamePrefix() == null || className.startsWith(loggerConf.getClassNamePrefix()))
			{
				return loggerConf;
			}
		}
		return null;
	}

	public static void closeAppenders()
	{
		for (int c = 0; c < loggerConfs.size(); c++)
		{
			LoggerConf conf = (LoggerConf) loggerConfs.elementAt(c);
			for (int i = 0; i < conf.getAppenders().size(); i++)
			{
				((Appender) conf.getAppenders().elementAt(i)).close();
			}
		}
	}

	public static String getEncoding()
	{
		return ENCODING;
	}

	public static void addLoggerConf(LoggerConf loggerConf)
	{
		loggerConfs.addElement(loggerConf);
	}
}
