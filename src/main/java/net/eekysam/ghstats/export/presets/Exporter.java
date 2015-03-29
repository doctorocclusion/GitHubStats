package net.eekysam.ghstats.export.presets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Table;

import net.eekysam.ghstats.data.RepoEntry;
import net.eekysam.ghstats.export.TableWriter;

public abstract class Exporter
{
	private List<RepoEntry> entries;
	
	public Exporter(List<RepoEntry> entries)
	{
		this.entries = entries;
	}
	
	public abstract void start(List<RepoEntry> entries);
	
	public abstract void add(RepoEntry repo);
	
	public abstract Table<String, String, ?> end();
	
	public void export(BufferedWriter writer) throws IOException
	{
		this.start(this.entries);
		for (RepoEntry repo : this.entries)
		{
			this.add(repo);
		}
		Table<String, String, ?> table = this.end();
		List<String> columns = Lists.newArrayList(table.columnKeySet());
		TableWriter tw = new TableWriter(writer);
		tw.write("X");
		for (String column : columns)
		{
			tw.write(column);
		}
		Set<String> rows = table.rowKeySet();
		for (String row : rows)
		{
			for (String column : columns)
			{
				tw.write(String.valueOf(table.get(row, column)));
			}
			tw.newLine();
		}
	}
}
