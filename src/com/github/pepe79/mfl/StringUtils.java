package com.github.pepe79.mfl;

import java.util.Vector;

public class StringUtils
{
	private StringUtils()
	{
	}

	public static String[] split(String target, String pattern)
	{
		int match;
		Vector vector = new Vector();
		while ((match = target.indexOf(pattern)) != -1)
		{
			vector.addElement(target.substring(0, match));
			target = target.substring(match + pattern.length());
		}
		vector.addElement(target);

		String[] result = new String[vector.size()];
		for (int i = 0; i < vector.size(); i++)
		{
			result[i] = (String) vector.elementAt(i);
		}
		return result;
	}
}
