package net.eekysam.ghstats.export.presets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;

import net.eekysam.ghstats.data.RepoEntry;

public class PercentLangExporter extends Exporter
{
	public static class AvgBytes
	{
		public int num = 0;
		public double total = 0.0D;
	}
	
	private long minBytes;
	
	public HashMap<String, AvgBytes> langs;
	private int repoCount;
	
	public PercentLangExporter(long minBytes)
	{
		this.minBytes = minBytes;
	}
	
	@Override
	public void start(List<RepoEntry> entries)
	{
		this.langs = new HashMap<String, AvgBytes>();
		this.repoCount = entries.size();
	}
	
	@Override
	public void add(RepoEntry repo)
	{
		if (repo.langs == null)
		{
			throw new IllegalArgumentException(String.format("Repo %s does not have any lang data.", repo.name));
		}
		long totalb = 0;
		for (Long bytes : repo.langs.values())
		{
			totalb += bytes;
		}
		for (Entry<String, Long> lang : repo.langs.entrySet())
		{
			if (lang.getValue() >= this.minBytes)
			{
				AvgBytes avg = this.langs.get(lang.getKey());
				if (avg == null)
				{
					avg = new AvgBytes();
					if (this.minBytes <= 0)
					{
						avg.num = this.repoCount;
					}
					this.langs.put(lang.getKey(), avg);
				}
				if (totalb > 0)
				{
					avg.total += (double) lang.getValue() / totalb;
					if (this.minBytes > 0)
					{
						avg.num++;
					}
				}
			}
		}
	}
	
	@Override
	public Table<String, String, ?> end()
	{
		ArrayTable<String, String, Object> table = ArrayTable.create(this.langs.keySet(), Arrays.asList("avg_percent"));
		for (Entry<String, AvgBytes> bytes : this.langs.entrySet())
		{
			table.put(bytes.getKey(), "avg_percent", bytes.getValue().total / bytes.getValue().num);
		}
		return table;
	}
}
