package net.sourceforge.mfl.youtube;

public final class Format
{
	private String extension;

	private String fmtId;

	private boolean hd;

	private String description;

	private Format(String fmtId, String extension, boolean hd, String description)
	{
		this.fmtId = fmtId;
		this.extension = extension;
		this.hd = hd;
		this.description = description;
	}

	public static final Format FLV_FLASH_LITE = new Format("5", "flv", false, "Flash video - Flash Lite compatible");

	public static final Format GP3_Q1 = new Format("17", "3gp", false, "3GP - Mobile video format - Better quality");

	public static final Format GP3_Q2 = new Format("13", "3gp", false, "3GP - Mobile video format - Poor quality");

	public static final Format MP4_1 = new Format("18", "mp4", true, "MPEG-4 (avc1)(18) - Won't play on all mobiles");

	public static final Format MP4_2 = new Format("22", "mp4", true, "MPEG-4 (avc1)(22) - Won't play on all mobiles");

	public static final Format FLV = new Format("34", "flv", false, "Flash video - Won't play with Flash Lite");

	public static final Format FLVHQ = new Format("35", "flv", true, "Flash video HQ - Won't play with Flash Lite");

	public static final Format FLVHQ2 = new Format("37", "flv", true, "Flash video HQ2 - Won't play with Flash Lite");

	public final static Format[] formats = {FLV_FLASH_LITE, GP3_Q2, GP3_Q1, MP4_1, MP4_2, FLV, FLVHQ, FLVHQ2};

	public static Format getFormat(String fmtId)
	{
		for (int i = 0; i < formats.length; i++)
		{
			if (formats[i].getFmtId().equals(fmtId))
			{
				return formats[i];
			}
		}
		return null;
	}

	public String getDescription()
	{
		return description;
	}

	public String getExtension()
	{
		return extension;
	}

	public String getFmtId()
	{
		return fmtId;
	}

	public boolean isHd()
	{
		return hd;
	}

}
