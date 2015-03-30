package net.eekysam.ghstats.export.presets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.eekysam.ghstats.data.RepoEntry;

public class NumLangExporter extends LangExporter<Integer>
{
	private long minBytes;
	private int total;
	
	public NumLangExporter(long minBytes)
	{
		this.minBytes = minBytes;
	}
	
	@Override
	public Integer startLangs(String lang, List<RepoEntry> entries)
	{
		this.total = entries.size();
		if (this.minBytes > 0)
		{
			return 0;
		}
		else
		{
			return entries.size();
		}
	}
	
	@Override
	public Integer addLang(String lang, long bytes, Integer value)
	{
		if (this.minBytes > 0 && bytes >= this.minBytes)
		{
			return value + 1;
		}
		return value;
	}
	
	@Override
	public Map<String, Integer> endLang(String lang, Integer value)
	{
		HashMap<String, Integer> out = new HashMap<String, Integer>();
		out.put("count", value);
		out.put("total", this.total);
		return out;
	}
}
