package net.eekysam.ghstats;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

public class Query
{
	private String path;
	private HashMap<String, String> pars = new HashMap<String, String>();
	
	private Query()
	{
		
	}
	
	public Query path(String add)
	{
		add = add.replace('\\', '/');
		if (!this.path.endsWith("/"))
		{
			this.path += "/";
		}
		if (add.startsWith("/"))
		{
			add = add.substring(1);
		}
		this.path += add;
		return this;
	}
	
	public Query par(String name, String value)
	{
		this.pars.put(name, value);
		return this;
	}
	
	public URL getURL(String apiUrl) throws MalformedURLException
	{
		String params = "";
		if (this.pars != null)
		{
			for (Entry<String, String> par : this.pars.entrySet())
			{
				if (params.isEmpty())
				{
					params += "?";
				}
				else
				{
					params += "&";
				}
				params += par.getKey() + "=" + par.getValue();
			}
		}
		String full = apiUrl;
		full = full.replace('\\', '/');
		if (!full.endsWith("/"))
		{
			full += "/";
		}
		if (this.path.startsWith("/"))
		{
			this.path = this.path.substring(1);
		}
		full += this.path;
		if (full.endsWith("/"))
		{
			full = full.substring(0, full.length() - 1);
		}
		full += params;
		return new URL(full);
	}
	
	public static Query start(String path)
	{
		Query q = new Query();
		q.path = path.replace('\\', '/');
		return q;
	}
}
