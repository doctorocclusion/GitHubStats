package net.eekysam.ghstats.export.presets;

import java.util.List;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import net.eekysam.ghstats.data.RepoEntry;

public class ComboExporter extends Exporter
{
	Exporter[] exporters;
	
	public ComboExporter(Exporter... exporters)
	{
		this.exporters = exporters;
	}
	
	@Override
	public void start(List<RepoEntry> entries)
	{
		for (Exporter exporter : this.exporters)
		{
			exporter.start(entries);
		}
	}
	
	@Override
	public void add(RepoEntry repo)
	{
		for (Exporter exporter : this.exporters)
		{
			exporter.add(repo);
		}
	}
	
	@Override
	public Table<String, String, Object> end()
	{
		HashBasedTable<String, String, Object> table = HashBasedTable.create();
		for (Exporter exporter : this.exporters)
		{
			table.putAll(exporter.end());
		}
		return table;
	}
}
