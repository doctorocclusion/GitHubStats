package net.eekysam.ghstats;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;

import com.google.common.base.Predicate;

public class RepoSampler
{
	public static class SampleInfo
	{
		public final int size;
		public final int pages;
		public final int requestsUsed;
		public final int requestsRemaining;
		public final int requestLimit;
		
		public SampleInfo(int size, int pages, int requestsUsed, int requestsRemaining, int requestLimit)
		{
			super();
			this.size = size;
			this.pages = pages;
			this.requestsUsed = requestsUsed;
			this.requestsRemaining = requestsRemaining;
			this.requestLimit = requestLimit;
		}
	}
	
	public GitHub gh;
	public Predicate<GHRepository> criteria;
	public long maxId;
	
	public RepoSampler(GitHub github, Predicate<GHRepository> criteria, long maxId)
	{
		this.gh = github;
		this.criteria = criteria;
		this.maxId = maxId;
	}
	
	public RepoSampler(GitHub github, long maxId)
	{
		this(github, null, maxId);
	}
	
	public int samplePage(long first, Collection<GHRepository> sample)
	{
		return this.samplePage(first, sample, -1);
	}
	
	public int samplePage(long first, Collection<GHRepository> sample, int maxNum)
	{
		PagedIterable<GHRepository> page = this.gh.listAllPublicRepositories("" + (first - 1));
		List<GHRepository> repos = page.iterator().nextPage();
		int i;
		int num = 0;
		for (i = 0; i < repos.size() && (maxNum < 0 || num < maxNum); i++)
		{
			GHRepository repo = repos.get(i);
			if (this.criteria == null || this.criteria.apply(repo))
			{
				sample.add(repo);
				num++;
			}
		}
		return num;
	}
	
	public SampleInfo sample(Predicate<SampleInfo> sampleWhile, Collection<GHRepository> sample, int maxPerPage, Random rand) throws IOException
	{
		GHRateLimit limit = this.gh.getRateLimit();
		SampleInfo info = new SampleInfo(0, 0, 0, limit.remaining, limit.limit);
		GHRateLimit startlim = limit;
		int num = 0;
		int page = 0;
		while (sampleWhile.apply(info))
		{
			page++;
			
			num += this.samplePage(rand.nextInt((int) this.maxId), sample, maxPerPage);
			
			limit = this.gh.getRateLimit();
			info = new SampleInfo(num, page, startlim.remaining - limit.remaining, limit.remaining, limit.limit);
		}
		return info;
	}
}
