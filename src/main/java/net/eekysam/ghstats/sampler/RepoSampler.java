package net.eekysam.ghstats.sampler;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import com.jcabi.github.Github;
import com.jcabi.github.Repo;

public class RepoSampler
{
	private Github gh;
	public Collection<Repo> sample;
	public long maxId;
	
	private int sampled = 0;
	private int clusters = 0;
	
	public RepoSampler(Github github, Collection<Repo> sample, long maxId)
	{
		this.gh = github;
		this.maxId = maxId;
		this.sample = sample;
	}
	
	public int numSampled()
	{
		return this.sampled;
	}
	
	public int clustersSampled()
	{
		return this.clusters;
	}
	
	public boolean sampleCluster(int clusterSize, long first)
	{
		Iterator<Repo> cluster = this.gh.repos().iterate("" + (first - 1)).iterator();
		this.clusters++;
		int num = 0;
		while (clusterSize < num)
		{
			Repo repo;
			try
			{
				repo = cluster.next();
			}
			catch (IllegalStateException | NoSuchElementException e)
			{
				return false;
			}
			this.sample.add(repo);
			this.sampled++;
			num++;
		}
		return true;
	}
	
	public int sample(int clusterSize, int sampleSize, Random rand)
	{
		int startsamp = this.sampled;
		while (this.sampled - startsamp < sampleSize)
		{
			if (!this.sampleCluster(clusterSize, rand.nextInt((int) this.maxId)))
			{
				break;
			}
		}
		return this.sampled - startsamp;
	}
}
