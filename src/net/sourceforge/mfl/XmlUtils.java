package net.sourceforge.mfl;

public class XmlUtils
{
	private XmlUtils()
	{
	}

	// FIXME: replace this dummy
	public static String findTag(String tagName, StringBuffer result)
	{
		String search = result.toString();
		String startTag = "<" + tagName + ">";
		String endTag = "</" + tagName + ">";
		int start = search.indexOf(startTag);
		int end = search.indexOf(endTag);

		if (start == -1 || end == -1)
		{
			return null;
		}

		String val = search.substring(start + startTag.length(), end);
		result.delete(0, end + endTag.length());
		return val;
	}

	// FIXME: replace this dummy
	public static String findAttributeValue(String tagName, String attrName,
			StringBuffer result)
	{
		String search = result.toString();
		String startTag = "<" + tagName + " " + attrName + "='";
		String endTag = "'/>";
		int start = search.indexOf(startTag);
		int end = -1;
		if (start != -1)
		{
			end = search.indexOf(endTag, start);
		}

		if (start == -1 || end == -1 || end < start)
		{
			return "unk";
		}

		String val = search.substring(start + startTag.length(), end);
		result.delete(0, end + endTag.length() - 1);
		return val;
	}
}
