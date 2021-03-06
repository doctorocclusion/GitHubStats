package net.eekysam.ghstats.data;

import java.time.Instant;
import java.util.EnumMap;
import java.util.HashMap;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import net.eekysam.ghstats.data.adapters.LangsAdapter;
import net.eekysam.ghstats.data.adapters.RequestLogAdapter;
import net.eekysam.ghstats.filter.FilterVar;
import net.eekysam.ghstats.grab.GatherReq;

public class RepoEntry
{
	public String name;
	public boolean direct;
	public boolean selected;
	
	@SerializedName("repo")
	public RepoData repoData;
	
	@JsonAdapter(LangsAdapter.class)
	public HashMap<String, Long> langs = null;
	
	@JsonAdapter(RequestLogAdapter.class)
	public EnumMap<GatherReq, Instant> reqs = new EnumMap<GatherReq, Instant>(GatherReq.class);
	
	public Object getVar(FilterVar var)
	{
		switch (var)
		{
			case DIRECT:
				return this.direct;
			case NAME:
				return this.name;
			case SELECTED:
				return this.selected;
			case REPO_GRABED:
				return this.repoData != null;
			default:
				if (this.repoData != null)
				{
					return this.repoData.getVar(var);
				}
				else
				{
					return FilterVar.NOT_LOADED;
				}
		}
	}
	
	public long getLang(String lang)
	{
		if (this.langs == null)
		{
			throw new IllegalStateException(String.format("Langs not loaded for %s", this.name));
		}
		Long bytes = this.langs.get(lang);
		if (bytes == null)
		{
			return 0;
		}
		return bytes;
	}
	
	@Override
	public int hashCode()
	{
		return this.name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof RepoEntry)
		{
			return this.name.equals(((RepoEntry) obj).name);
		}
		return false;
	}
}
