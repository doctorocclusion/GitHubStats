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
	public abstract void start(List<RepoEntry> repos);
	
	public abstract void add(RepoEntry repo);
	
	public abstract Table<String, String, ?> end();
	
	public void export(BufferedWriter writer, List<RepoEntry> repos) throws IOException
	{
		this.start(repos);
		for (RepoEntry repo : repos)
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
		tw.newLine();
		Set<String> rows = table.rowKeySet();
		for (String row : rows)
		{
			tw.write(row);
			for (String column : columns)
			{
				tw.write(String.valueOf(table.get(row, column)));
			}
			tw.newLine();
		}
		System.out.printf("Exported the data from %d repos.%n", repos.size());
	}
}
