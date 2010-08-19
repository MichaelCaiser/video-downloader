package net.sourceforge.mfl.log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Vector;


public class Logger
{
	public static final int FATAL_LEVEL = 1;

	public static final int ERROR_LEVEL = 2;

	public static final int WARN_LEVEL = 3;

	public static final int INFO_LEVEL = 4;

	public static final int DEBUG_LEVEL = 5;

	public static final int TRACE_LEVEL = 6;

	public void trace(Class clazz, String message)
	{
		log(clazz, TRACE_LEVEL, message, null);
	}

	public void debug(Class clazz, String message)
	{
		log(clazz, DEBUG_LEVEL, message, null);
	}

	public void info(Class clazz, String message)
	{
		log(clazz, INFO_LEVEL, message, null);
	}

	public void warn(Class clazz, String message)
	{
		warn(clazz, message, null);
	}

	public void warn(Class clazz, String message, Throwable t)
	{
		log(clazz, WARN_LEVEL, message, t);
	}

	public void error(Class clazz, String message, Throwable t)
	{
		log(clazz, ERROR_LEVEL, message, t);
	}

	public void fatal(Class clazz, String message, Throwable t)
	{
		log(clazz, FATAL_LEVEL, message, t);
	}

	private synchronized void log(Class clazz, int level, String message, Throwable t)
	{
		LoggerConf loggerConf = LogSystem.getLoggerConf(clazz.getName());
		if (loggerConf != null)
		{
			if (level <= loggerConf.getLogLevel())
			{
				Vector appenders = loggerConf.getAppenders();
				for (int i = 0; i < appenders.size(); i++)
				{
					log(((Appender) appenders.elementAt(i)).getDataOutputStream(), clazz, level, message, t);
				}
			}
		}
	}

	private void writeString(OutputStream os, String s) throws IOException
	{
		os.write(s.getBytes(LogSystem.getEncoding()));
	}

	private void log(OutputStream os, Class clazz, int level, String message, Throwable t)
	{
		try
		{
			writeString(os, new Date().toString());
			writeString(os, "  ");
			writeString(os, Logger.getLevel(level));
			writeString(os, " - ");
			writeString(os, clazz.getName());

			if (message != null)
			{
				writeString(os, ": ");
				writeString(os, message);
			}

			if (t != null)
			{
				writeString(os, " - ");
				writeString(os, t.getClass().getName());
				if (t.getMessage() != null)
				{
					writeString(os, ": ");
					writeString(os, t.getMessage());
				}
			}
			writeString(os, "\n");
			os.flush();
		}
		catch (IOException e)
		{
			System.err.println("Error while appending to appender: " + e.getMessage());
		}
	}

	public static String getLevel(int level)
	{
		switch (level)
		{
			case DEBUG_LEVEL:
				return "DEBUG";
			case ERROR_LEVEL:
				return "ERROR";
			case FATAL_LEVEL:
				return "FATAL";
			case INFO_LEVEL:
				return "INFO";
			case TRACE_LEVEL:
				return "TRACE";
			case WARN_LEVEL:
				return "WARN";
			default:
				return "UNKNOWN";
		}
	}
}
