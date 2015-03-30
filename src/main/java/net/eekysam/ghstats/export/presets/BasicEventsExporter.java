package net.eekysam.ghstats.export.presets;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;

import net.eekysam.ghstats.data.RepoEntry;
import net.eekysam.ghstats.export.TimeExport;

public class BasicEventsExporter extends Exporter
{
	private ArrayTable<String, String, String> table;
	private TimeExport time;
	
	public BasicEventsExporter(TimeExport time)
	{
		this.time = time;
	}
	
	@Override
	public void start(List<RepoEntry> entries)
	{
		String[] names = new String[entries.size()];
		for (int i = 0; i < names.length; i++)
		{
			names[i] = entries.get(i).name;
		}
		this.table = ArrayTable.create(Arrays.asList(names), Arrays.asList("created", "updated", "pushed"));
	}
	
	@Override
	public void add(RepoEntry repo)
	{
		if (repo.repoData != null)
		{
			this.table.put(repo.name, "created", this.time.export(repo.repoData.createdAt));
			this.table.put(repo.name, "updated", this.time.export(repo.repoData.updatedAt));
			this.table.put(repo.name, "pushed", this.time.export(repo.repoData.pushedAt));
		}
	}
	
	@Override
	public Table<String, String, ?> end()
	{
		return this.table;
	}
}
