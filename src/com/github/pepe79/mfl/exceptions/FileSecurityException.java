package com.github.pepe79.mfl.exceptions;

public class FileSecurityException extends SecurityException
{
	private SecurityException cause;

	public FileSecurityException(SecurityException cause)
	{
		this.cause = cause;
	}

	public Throwable getCause()
	{
		return cause;
	}

}
