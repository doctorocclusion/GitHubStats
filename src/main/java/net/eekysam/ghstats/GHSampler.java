package net.eekysam.ghstats;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import com.google.common.base.Predicate;

public abstract class GHSampler
{
	public RepositoryService repoService;
	
	public GHSampler(RepositoryService repoService)
	{
		this.repoService = repoService;
	}
	
	private Collection<Repository> getRawCluster(long start)
	{
		try
		{
			return this.repoService.pageAllRepositories(start).next();
		}
		catch (NoSuchElementException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public void getCluster(long start, Collection<Repository> sample)
	{
		Collection<Repository> raw = this.getRawCluster(start);
		if (raw == null)
		{
			return;
		}
		Predicate<Repository> criteria = this.criteria();
		for (Repository repo : raw)
		{
			if (criteria.apply(repo))
			{
				sample.add(repo);
			}
		}
	}
	
	public abstract Predicate<Repository> criteria();
}
