package net.eekysam.ghstats.export.presets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;

import net.eekysam.ghstats.data.RepoEntry;

public class PrimeLangExporter extends Exporter
{
	HashMap<String, Long> counter;
	
	public PrimeLangExporter(List<RepoEntry> entries)
	{
		super(entries);
	}
	
	@Override
	public void start(List<RepoEntry> entries)
	{
		this.counter = new HashMap<String, Long>();
	}
	
	@Override
	public void add(RepoEntry repo)
	{
		if (repo.repoData != null)
		{
			String lang = repo.repoData.primaryLanguage;
			if (lang != null && !lang.isEmpty())
			{
				this.counter.put(lang, this.counter.get(lang) + 1);
			}
		}
	}
	
	@Override
	public Table<String, String, Long> end()
	{
		ArrayTable<String, String, Long> table = ArrayTable.create(this.counter.keySet(), Arrays.asList("primary"));
		for (Entry<String, Long> lang : this.counter.entrySet())
		{
			table.put(lang.getKey(), "primary", lang.getValue());
		}
		return table;
	}
}
