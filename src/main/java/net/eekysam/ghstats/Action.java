package net.eekysam.ghstats;

import net.eekysam.ghstats.data.DataFile;

import com.jcabi.github.Github;

public abstract class Action
{
	public Github gh;
	public DataFile data;
	
	public Action(Github gh, DataFile data)
	{
		this.gh = gh;
		this.data = data;
	}
}
