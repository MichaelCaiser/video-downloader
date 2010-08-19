package com.github.pepe79.mfl.youtube;

public class SearchResult
{
	private String title;

	private String id;

	private String duration;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getImageUrl()
	{
		return "http://i.ytimg.com/vi/" + getId() + "/default.jpg";
	}

	public String getDuration()
	{
		return duration;
	}

	public void setDuration(String duration)
	{
		this.duration = duration;
	}

}
