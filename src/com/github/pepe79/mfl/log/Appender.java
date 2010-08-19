package com.github.pepe79.mfl.log;

import java.io.DataOutputStream;

public interface Appender
{
	DataOutputStream getDataOutputStream();

	void close();
}
