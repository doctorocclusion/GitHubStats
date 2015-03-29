package net.eekysam.ghstats;

import net.eekysam.ghstats.data.DataFile;

public abstract class Action
{
	public GitHub gh;
	public DataFile data;
	
	public Action(GitHub gh, DataFile data)
	{
		this.gh = gh;
		this.data = data;
	}
}
