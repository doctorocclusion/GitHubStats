package net.eekysam.ghstats;

import java.io.File;

import com.jcabi.github.Github;

public abstract class Action
{
	public Github gh;
	public File file;
	public EnumOutType outtype;

	public Action(Github gh, File file, EnumOutType outtype)
	{
		this.gh = gh;
		this.file = file;
		this.outtype = outtype;
	}
}
