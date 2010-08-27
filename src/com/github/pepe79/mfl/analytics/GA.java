package com.github.pepe79.mfl.analytics;

import com.github.pepe79.mfl.Logs;
import com.github.pepe79.mfl.UrlEncode;
import java.io.IOException;
import java.util.Random;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;


public class GA
{
	private static final String GA_ACCOUNT = "MO-17985180-2";

	private static final String GA_PIXEL = "http://www.google-analytics.com/__utm.gif";

	public static String createGoogleAnalyticsUrl(String referer, String path, String query, String visitorId)
	{
		StringBuffer url = new StringBuffer();
		url.append(GA_PIXEL + "?");
		url.append("utmac=").append(GA_ACCOUNT);
		url.append("&utmn=").append(Integer.toString((int) ((new Random().nextInt()) * 0x7fffffff)));
		if (referer == null || "".equals(referer))
		{
			referer = "-";
		}
		url.append("&utmr=").append(UrlEncode.urlEncode(referer));
		if (path != null)
		{
			if (query != null)
			{
				path += "?" + query;
			}
			url.append("&utmp=").append(UrlEncode.urlEncode(path));
		}
		url.append("&guid=ON");
		url.append("&utmvid=" + visitorId);
		return url.toString();
	}

	public static void track(String query) throws IOException
	{
		String url =
			createGoogleAnalyticsUrl("mfl-0.1.24", "/search/" + UrlEncode.urlEncode(query), null, "vid"
				+ System.identityHashCode(GA.class));
		Logs.MFL.info(GA.class, "GA tracking url: " + url);
		HttpConnection con = (HttpConnection) Connector.open(url);
		Logs.MFL.info(GA.class, "GA tracking response: " + con.getResponseCode());
		con.close();
	}
}
