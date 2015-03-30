package net.eekysam.ghstats.export.presets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import net.eekysam.ghstats.data.RepoEntry;

public abstract class LangExporter<T> extends Exporter
{
	public HashMap<String, T> langs;
	private List<RepoEntry> entries;
	
	@Override
	public void start(List<RepoEntry> entries)
	{
		this.langs = new HashMap<String, T>();
		this.entries = entries;
	}
	
	public abstract T startLangs(String lang, List<RepoEntry> entries);
	
	@Override
	public void add(RepoEntry repo)
	{
		if (repo.langs == null)
		{
			throw new IllegalArgumentException(String.format("Repo %s does not have any lang data.", repo.name));
		}
		for (Entry<String, Long> lang : repo.langs.entrySet())
		{
			String lname = lang.getKey();
			if (!this.langs.containsKey(lname))
			{
				this.langs.put(lname, this.startLangs(lname, this.entries));
			}
			this.langs.put(lname, this.addLang(lname, lang.getValue(), this.langs.get(lname)));
		}
	}
	
	public abstract T addLang(String lang, long bytes, T value);
	
	@Override
	public Table<String, String, Object> end()
	{
		HashBasedTable<String, String, Object> table = HashBasedTable.create();
		for (Entry<String, T> lang : this.langs.entrySet())
		{
			Map<String, ?> row = this.endLang(lang.getKey(), lang.getValue());
			for (Entry<String, ?> entry : row.entrySet())
			{
				table.put(lang.getKey(), entry.getKey(), entry.getValue());
			}
		}
		return table;
	}
	
	public abstract Map<String, ?> endLang(String lang, T value);
	
	public static List<RepoEntry> filter(List<RepoEntry> repos)
	{
		int size = repos.size();
		repos.removeIf(new UngatheredLangs());
		System.out.printf("%d of the given %d repos has gathered lang data.%n", repos.size(), size);
		return repos;
	}
	
	private static class UngatheredLangs implements Predicate<RepoEntry>
	{
		@Override
		public boolean test(RepoEntry repo)
		{
			return repo.langs == null;
		}
	};
}
