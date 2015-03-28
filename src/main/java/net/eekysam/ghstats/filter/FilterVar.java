package net.eekysam.ghstats.filter;

import java.util.EnumSet;

public enum FilterVar
{
	NAME,
	DIRECT,
	SELECTED,
	ID,
	IS_FORK,
	SIZE,
	AGE,
	CREATED,
	UPDATED,
	PUSHED,
	STARS,
	WATCHERS,
	FORKS,
	SUBSCRIBERS;
	
	public static final EnumSet<FilterVar> all = EnumSet.allOf(FilterVar.class);
	
	public static final Object NOT_LOADED = new Object();
}
