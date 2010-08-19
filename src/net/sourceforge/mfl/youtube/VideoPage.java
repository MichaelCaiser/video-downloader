package net.sourceforge.mfl.youtube;

import net.sourceforge.mfl.StringUtils;
import net.sourceforge.mfl.UrlEncode;

public class VideoPage
{
	private String signature;

	private String id;

	private String fmtMap;

	private Format[] availableFormats;

	public boolean isHdAvailable()
	{
		if (availableFormats != null)
		{
			for (int i = 0; i < availableFormats.length; i++)
			{
				Format f = availableFormats[i];
				if (f != null && f.isHd())
				{
					return true;
				}
			}
		}
		return false;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getSignature()
	{
		return signature;
	}

	public void setSignature(String signature)
	{
		this.signature = signature;
	}

	public String getFmtMap()
	{
		return fmtMap;
	}

	public void setFmtMap(String fmtMap)
	{
		this.fmtMap = fmtMap;
		fillAvailableFormats();
	}

	private void fillAvailableFormats()
	{
		Format[] alwaysAvailable =
		{ Format.GP3_Q1, Format.GP3_Q2 };
		if (fmtMap != null)
		{
			String map = UrlEncode.urlDecode(fmtMap);
			String[] splittedMap = StringUtils.split(map, ",");
			availableFormats = new Format[splittedMap.length
					+ alwaysAvailable.length];
			for (int i = 0; i < splittedMap.length; i++)
			{
				String[] fmt = StringUtils.split(splittedMap[i], "\\/");
				Format f = Format.getFormat(fmt[0]);
				availableFormats[i] = f;
			}
			for (int i = 0; i < alwaysAvailable.length; i++)
			{
				availableFormats[splittedMap.length + i] = alwaysAvailable[i];
			}
		}
	}

	public Format[] getAvailableFormats()
	{
		return availableFormats;
	}
}
