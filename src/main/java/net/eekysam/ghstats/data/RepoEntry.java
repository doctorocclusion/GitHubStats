package net.eekysam.ghstats.data;

import java.time.Instant;
import java.util.EnumMap;
import java.util.HashMap;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import net.eekysam.ghstats.data.adapters.LangsAdapter;
import net.eekysam.ghstats.data.adapters.RequestLogAdapter;
import net.eekysam.ghstats.filter.FilterVar;
import net.eekysam.ghstats.grab.GrabReq;

public class RepoEntry
{
	public String name;
	public boolean direct;
	public boolean selected;
	
	@SerializedName("repo")
	public RepoData repoData;
	
	@JsonAdapter(LangsAdapter.class)
	public HashMap<String, Long> langs = new HashMap<String, Long>();
	
	@JsonAdapter(RequestLogAdapter.class)
	public EnumMap<GrabReq, Instant> reqs = new EnumMap<GrabReq, Instant>(GrabReq.class);
	
	public Object getVar(FilterVar var)
	{
		switch (var)
		{
			case DIRECT:
				return this.selected;
			case NAME:
				return this.name;
			case SELECTED:
				return this.selected;
			default:
				return FilterVar.NOT_LOADED;
		}
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
