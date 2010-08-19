package net.sourceforge.mfl.exceptions;

public class NetworkSecurityException extends SecurityException {
	private SecurityException cause;

	public NetworkSecurityException(SecurityException cause) {
		this.cause = cause;
	}

	public Throwable getCause() {
		return cause;
	}

}
