package net.eekysam.ghstats.export.presets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.eekysam.ghstats.data.RepoEntry;

public class AvgLangExporter extends LangExporter<AvgLangExporter.AvgBytes>
{
	public static class AvgBytes
	{
		public int num = 0;
		public long total = 0;
	}
	
	private long minBytes;
	
	public AvgLangExporter(long minBytes)
	{
		this.minBytes = minBytes;
	}
	
	@Override
	public AvgBytes startLangs(String lang, List<RepoEntry> entries)
	{
		AvgBytes avg = new AvgBytes();
		if (this.minBytes <= 0)
		{
			avg.num = entries.size();
		}
		return avg;
	}
	
	@Override
	public AvgBytes addLang(String lang, long bytes, AvgBytes value)
	{
		if (bytes >= this.minBytes)
		{
			value.total += bytes;
			if (this.minBytes > 0)
			{
				value.num++;
			}
		}
		return value;
	}
	
	@Override
	public Map<String, Double> endLang(String lang, AvgBytes value)
	{
		HashMap<String, Double> out = new HashMap<String, Double>();
		out.put("avg_bytes", (double) value.total / value.num);
		return out;
	}
}
