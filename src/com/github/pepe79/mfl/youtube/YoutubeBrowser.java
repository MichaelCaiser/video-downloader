package com.github.pepe79.mfl.youtube;

import com.github.pepe79.mfl.Logs;
import com.github.pepe79.mfl.StringUtils;
import com.github.pepe79.mfl.XmlUtils;
import com.github.pepe79.mfl.exceptions.FileSecurityException;
import com.github.pepe79.mfl.exceptions.NetworkSecurityException;
import com.github.pepe79.mfl.net.CompletionListener;
import com.github.pepe79.mfl.net.ConnectionHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.midlet.MIDlet;



public class YoutubeBrowser
{

	private MIDlet m;

	public YoutubeBrowser(MIDlet m)
	{
		this.m = m;
	}

	public void saveVideo(String filename, String videoId, String signature, Format format,
		CompletionListener completionListener) throws IOException
	{
		FileConnection fileConnection = null;

		try
		{
			OutputStream fos = null;
			try
			{
				fileConnection = (FileConnection) Connector.open("file:///" + filename);

				if (!fileConnection.exists())
				{
					fileConnection.create();
				}
				fos = fileConnection.openOutputStream();

			}
			catch (SecurityException e)
			{
				throw new FileSecurityException(e);
			}

			try
			{
				ConnectionHelper.copyData("http://www.youtube.com/get_video?video_id=" + videoId + "&t=" + signature
					+ "&fmt=" + format.getFmtId() + "&asv=3", fos, m, 10, completionListener);
			}
			catch (SecurityException e)
			{
				throw new NetworkSecurityException(e);
			}

			fos.close();
		}
		catch (IOException e)
		{
			Logs.MFL.error(this.getClass(), "An error occured while saving video to file:", e);
			throw e;
		}
		finally
		{
			if (fileConnection != null)
			{
				try
				{
					fileConnection.close();
				}
				catch (IOException e)
				{
					Logs.MFL.warn(this.getClass(), "An error occured while closing file.", e);
				}
			}
		}
	}

	public String getVideoPageUrl(String videoId)
	{
		return "http://www.youtube.com/watch?v=" + videoId;
	}

	public VideoPage getVideoPage(String videoId, CompletionListener completionListener) throws IOException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try
		{
			ConnectionHelper.copyData(getVideoPageUrl(videoId), os, m, 0, completionListener);
			os.close();
		}
		catch (IOException e)
		{
			Logs.MFL.error(this.getClass(), "An error occured while downloading youtube detail page for video " + videoId,
				e);
			throw e;
		}

		byte[] page = os.toByteArray();
		String pageContent = new String(page);

		String swfStartPattern = "swfConfig = {";
		int start = pageContent.indexOf(swfStartPattern);
		int end = pageContent.indexOf("}", start);

		String swfArgString = pageContent.substring(start + swfStartPattern.length(), end - 1);
		String[] swfArgPairs = StringUtils.split(swfArgString, ", ");

		Hashtable swfArgs = new Hashtable();
		for (int i = 0; i < swfArgPairs.length; i++)
		{
			String[] swfArgPair = StringUtils.split(swfArgPairs[i], ": ");
			if (swfArgPair.length == 2)
			{
				String key = stripQuota(swfArgPair[0]);
				String value = stripQuota(swfArgPair[1]);
				swfArgs.put(key, value);
			}
		}

		VideoPage videoPage = new VideoPage();
		videoPage.setId(videoId);
		videoPage.setSignature((String) swfArgs.get("t"));
		videoPage.setFmtMap((String) swfArgs.get("fmt_map"));

		return videoPage;
	}

	private String stripQuota(String target)
	{
		if (target.startsWith("\"") && target.endsWith("\""))
		{
			return target.substring(1, target.length() - 1);
		}
		else
		{
			return target;
		}
	}

	public Vector getSearchResults(String query, CompletionListener completionListener) throws IOException
	{
		query = query.replace(' ', '+');
		String url = "http://gdata.youtube.com/feeds/api/videos?q=" + query + "&max-results=10&v=2";
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ConnectionHelper.copyData(url, baos, m, 0, completionListener);
			baos.close();
			StringBuffer result = new StringBuffer(new String(baos.toByteArray(), "UTF-8"));

			String value = null;
			value = XmlUtils.findTag("id", result); // skip
			value = XmlUtils.findTag("title", result); // skip

			Vector results = new Vector();
			while ((value = XmlUtils.findTag("id", result)) != null)
			{
				SearchResult searchResult = new SearchResult();

				String vid = "video:";
				value = value.substring(value.indexOf(vid) + vid.length());

				searchResult.setId(value);
				searchResult.setTitle(XmlUtils.findTag("title", result));

				String seconds = XmlUtils.findAttributeValue("yt:duration", "seconds", result);
				String duration = "";
				try
				{
					int d = Integer.parseInt(seconds);
					duration = (d / 60) + ":" + ((d % 60) < 10 ? "0" : "") + (d % 60);
				}
				catch (NumberFormatException e)
				{
					// ignore
					Logs.MFL.warn(this.getClass(), "An error occured while formating number", e);
				}
				searchResult.setDuration(duration);
				results.addElement(searchResult);
			}
			return results;
		}
		catch (IOException e)
		{
			Logs.MFL.error(this.getClass(), "An error occured while getting youtube search results for input '" + query
				+ "'", e);
			throw e;
		}
	}
}
