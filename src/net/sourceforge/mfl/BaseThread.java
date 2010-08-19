package net.sourceforge.mfl;

public abstract class BaseThread extends Thread
{

	public abstract void runSafe() throws InterruptedException;

	public void run()
	{
		try
		{
			runSafe();
		} catch (InterruptedException e)
		{
			Logs.MFL.warn(getClass(), "Thread was interrupted.");
		} catch (RuntimeException e)
		{
			Logs.MFL.error(getClass(), "An error occured while running thread",
					e);
			throw e;
		}
	}
}
