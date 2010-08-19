package net.sourceforge.mfl.log;

import java.io.DataOutputStream;

public interface Appender
{
	DataOutputStream getDataOutputStream();

	void close();
}
