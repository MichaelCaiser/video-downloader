package com.github.pepe79.mfl;

import com.github.pepe79.mfl.net.CompletionListener;
import com.github.pepe79.mfl.youtube.SearchResult;

import java.io.IOException;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;


public class DownloadTask implements CompletionListener
{
	private int completionPercentage = 0;

	private int index;

	private List parent;

	private SearchResult searchResult;

	private Image readyImage;

	private Image errorImage;

	private boolean ready = false;

	private boolean error = false;

	private Thread downloadThread;

	private String targetFilename;

	public DownloadTask(List parent, SearchResult searchResult)
	{
		this.searchResult = searchResult;
		this.parent = parent;

		// create status images
		try
		{
			readyImage = Image.createImage("/check.png");
			errorImage = Image.createImage("/error.png");
			StatusImages.initialize();
		}
		catch (IOException e)
		{
			Logs.MFL.warn(this.getClass(), "An unexpteced error occured while preparing status images", e);
		}

		index = parent.append(getStatus(), getStatusImage());
	}

	public Thread getDownloadThread()
	{
		return downloadThread;
	}

	public void setDownloadThread(Thread downloadThread)
	{
		this.downloadThread = downloadThread;
	}

	public boolean isError()
	{
		return error;
	}

	public void setError(boolean error)
	{
		this.error = error;
	}

	public void completed(int percentage)
	{
		this.completionPercentage = percentage;
		updateList();
	}

	private Image getStatusImage()
	{
		if (error)
		{
			return errorImage;
		}
		if (ready)
		{
			return readyImage;
		}
		else
		{
			if (StatusImages.statusImages != null)
			{
				return StatusImages.statusImages[completionPercentage % StatusImages.statusImages.length];
			}
			else
			{
				return null;
			}
		}
	}

	public SearchResult getSearchResult()
	{
		return searchResult;
	}

	public int getCompletionPercentage()
	{
		return completionPercentage;
	}

	public void ready()
	{
		ready = true;
		updateList();
	}

	public void updateList()
	{
		parent.set(index, getStatus(), getStatusImage());
	}

	private String getStatus()
	{
		return completionPercentage + "% " + searchResult.getTitle();
	}

	public String getTargetFilename()
	{
		return targetFilename;
	}

	public void setTargetFilename(String targetFilename)
	{
		this.targetFilename = targetFilename;
	}

}
