package net.eekysam.ghstats.sampler;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import javax.json.JsonException;

import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.jcabi.http.Request;
import com.jcabi.http.RequestURI;

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
		// Really nasty kludge to fix a bug in the com.jcabi.github library
		try
		{
			Class<?> clazz = Class.forName("com.jcabi.github.RtValuePagination$Items");
			Field reqfield = clazz.getDeclaredField("request");
			reqfield.setAccessible(true);
			RequestURI ruri = ((Request) reqfield.get(cluster)).uri();
			if (!ruri.toString().contains("repositories"))
			{
				reqfield.set(cluster, ruri.path("/repositories").back());
			}
		}
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		this.clusters++;
		int num = 0;
		while (num < clusterSize)
		{
			Repo repo = null;
			try
			{
				repo = cluster.next();
			}
			catch (IllegalStateException | JsonException e)
			{
				e.printStackTrace();
			}
			catch (NoSuchElementException e)
			{
				e.printStackTrace();
				return false;
			}
			if (repo != null)
			{
				this.sample.add(repo);
				this.sampled++;
				num++;
			}
		}
		return true;
	}
	
	public int sample(int sampleSize, int clusterSize, Random rand)
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
