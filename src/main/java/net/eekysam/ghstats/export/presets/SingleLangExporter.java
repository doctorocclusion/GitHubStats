package net.eekysam.ghstats.export.presets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import net.eekysam.ghstats.data.RepoEntry;

public class SingleLangExporter implements IExporter
{
	private long minBytes;
	private String lang;
	
	public SingleLangExporter(long minBytes, String lang)
	{
		this.minBytes = minBytes;
		this.lang = lang;
	}
	
	@Override
	public void export(BufferedWriter writer, List<RepoEntry> repos) throws IOException
	{
		for (RepoEntry repo : repos)
		{
			long bytes = 0;
			if (repo.langs != null)
			{
				Long rbytes = repo.langs.get(this.lang);
				if (rbytes != null)
				{
					bytes = rbytes;
				}
			}
			if (bytes >= this.minBytes)
			{
				writer.write(String.valueOf(bytes));
				writer.newLine();
			}
		}
	}
}
