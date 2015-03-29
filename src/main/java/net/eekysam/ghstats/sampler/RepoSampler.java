package net.eekysam.ghstats.sampler;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.eekysam.ghstats.GitHub;
import net.eekysam.ghstats.Query;

public class RepoSampler
{
	private GitHub gh;
	public Collection<String> sample;
	public long maxId;
	
	private int sampled = 0;
	private int clusters = 0;
	
	public RepoSampler(GitHub github, Collection<String> sample, long maxId)
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
		Iterator<JsonElement> repos;
		try
		{
			repos = this.gh.getJson(Query.start("repositories").par("since", String.valueOf(first - 1))).getAsJsonArray().iterator();
		}
		catch (IOException | IllegalStateException | ClassCastException | JsonParseException e)
		{
			e.printStackTrace();
			return false;
		}
		this.clusters++;
		int num = 0;
		while (repos.hasNext() && (clusterSize <= 0 || num < clusterSize))
		{
			JsonObject repo = repos.next().getAsJsonObject();
			this.sample.add(repo.get("full_name").getAsString());
			this.sampled++;
			num++;
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
