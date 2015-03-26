package net.eekysam.ghstats.sampler;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import com.google.common.base.Predicate;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;

public class RepoSampler
{
	private Github gh;
	public Predicate<Repo> criteria;
	public Collection<Repo> sample;
	public long maxId;

	private int sampled = 0;
	private int iterated = 0;
	private int clusters = 0;

	public RepoSampler(Github github, Predicate<Repo> criteria, Collection<Repo> sample, long maxId)
	{
		this.gh = github;
		this.criteria = criteria;
		this.maxId = maxId;
		this.sample = sample;
	}

	public int numSampled()
	{
		return this.sampled;
	}

	public int numIterated()
	{
		return this.iterated;
	}

	public int clustersSampled()
	{
		return this.clusters;
	}

	public boolean sampleCluster(int clusterSize, int iterationLimit, long first)
	{
		Iterator<Repo> cluster = this.gh.repos().iterate("" + (first - 1)).iterator();
		this.clusters++;
		int num = 0;
		int iters = 0;
		while (clusterSize < num && (iters < iterationLimit || iterationLimit < 0))
		{
			Repo repo;
			try
			{
				repo = cluster.next();
				this.iterated++;
				iters++;
			}
			catch (IllegalStateException | NoSuchElementException e)
			{
				return false;
			}
			if (this.criteria == null || this.criteria.apply(repo))
			{
				this.sample.add(repo);
				this.sampled++;
				num++;
			}
		}
		return true;
	}

	public int sample(int clusterSize, int sampleSize, int clusterItLimit, int totalItLimit, Random rand)
	{
		int startsamp = this.sampled;
		int startit = this.iterated;
		while (this.sampled - startsamp < sampleSize && (this.iterated - startit < totalItLimit || totalItLimit < 1))
		{
			if (!this.sampleCluster(clusterSize, clusterItLimit, rand.nextInt((int) this.maxId)))
			{
				break;
			}
		}
		return this.sampled - startsamp;
	}
}
