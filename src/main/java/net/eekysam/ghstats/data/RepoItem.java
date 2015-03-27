package net.eekysam.ghstats.data;

import net.eekysam.ghstats.filter.FilterVar;

public class RepoItem
{
	public final String name;
	public final boolean directlyAdded;
	private boolean selected;
	
	public RepoItem(String name, boolean selected, boolean direct)
	{
		this.name = name;
		this.selected = selected;
		this.directlyAdded = direct;
	}
	
	public boolean isSelected()
	{
		return this.selected;
	}
	
	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}
	
	public Object getVar(FilterVar var)
	{
		switch (var)
		{
			case ADDED_DIRECT:
				return this.selected;
			case NAME:
				return this.name;
			case SELECTED:
				return this.selected;
			default:
				return FilterVar.NOT_LOADED;
		}
	}
}
