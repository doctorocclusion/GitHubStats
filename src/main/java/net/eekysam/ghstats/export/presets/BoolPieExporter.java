package net.eekysam.ghstats.export.presets;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;

import net.eekysam.ghstats.data.RepoEntry;

public class BoolPieExporter extends Exporter
{
	public long[][] nums;
	
	public BoolPieExporter(List<RepoEntry> entries)
	{
		super(entries);
	}
	
	@Override
	public void start(List<RepoEntry> entries)
	{
		this.nums = new long[6][2];
	}
	
	@Override
	public void add(RepoEntry repo)
	{
		if (repo.repoData != null)
		{
			for (int i = 0; i < 6; i++)
			{
				this.nums[i][1]++;
			}
			if (repo.repoData.isPrivate)
			{
				this.nums[0][0]++;
			}
			if (repo.repoData.isFork)
			{
				this.nums[1][0]++;
			}
			if (repo.repoData.hasIssues)
			{
				this.nums[2][0]++;
			}
			if (repo.repoData.hasDownloads)
			{
				this.nums[3][0]++;
			}
			if (repo.repoData.hasWiki)
			{
				this.nums[4][0]++;
			}
			if (repo.repoData.hasPages)
			{
				this.nums[5][0]++;
			}
		}
	}
	
	@Override
	public Table<String, String, ?> end()
	{
		ArrayTable<String, String, Long> table = ArrayTable.create(Arrays.asList("isPrivate", "isFork", "hasIssues", "hasDownloads", "hasWiki", "hasPages"), Arrays.asList("true", "false", "total"));
		for (int i = 0; i < 6; i++)
		{
			table.set(i, 0, this.nums[i][0]);
			table.set(i, 1, this.nums[i][1] - this.nums[i][0]);
			table.set(i, 2, this.nums[i][1]);
		}
		return table;
	}
}
