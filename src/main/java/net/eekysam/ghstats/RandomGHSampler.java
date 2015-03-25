package net.eekysam.ghstats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import com.google.common.base.Predicate;

public class RandomGHSampler extends GHSampler
{
	public Random random;
	public int maxStart;
	public Predicate<Repository> criteria;
	
	public RandomGHSampler(RepositoryService repoService, Random random, int maxStart, Predicate<Repository> criteria)
	{
		super(repoService);
		this.random = random;
		this.maxStart = maxStart;
		this.criteria = criteria;
	}
	
	public Collection<Repository> sampleBySize(int targetSize, int maxRequests)
	{
		System.out.printf("Starting new sample of size %d%n", targetSize);
		Collection<Repository> sample = new ArrayList<Repository>();
		int lastremain = -1;
		int made = 0;
		GitHubClient client = this.repoService.getClient();
		int clusters = 0;
		while (sample.size() < targetSize)
		{
			int remain = client.getRemainingRequests();
			if (remain != -1 && lastremain != -1)
			{
				made += remain - lastremain;
			}
			lastremain = remain;
			if (made >= maxRequests)
			{
				break;
			}
			long start = this.nextStart();
			int size = sample.size();
			this.getCluster(start, sample);
			size = sample.size() - size;
			clusters++;
			System.out.printf("Sample Cluster %d%n", clusters);
			System.out.printf("Start: %d%n", start);
			System.out.printf("New Samples: %d%n", size);
			System.out.printf("Total Samples: %d%n", sample.size());
			System.out.printf("Requests: %d/%d%n", client.getRemainingRequests(), client.getRequestLimit());
			System.out.println();
		}
		return sample;
	}
	
	public long nextStart()
	{
		return this.random.nextInt(this.maxStart + 1) - 1;
	}
	
	@Override
	public Predicate<Repository> criteria()
	{
		return this.criteria;
	}
}
