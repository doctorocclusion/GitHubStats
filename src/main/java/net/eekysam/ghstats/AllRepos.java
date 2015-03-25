package net.eekysam.ghstats;

import org.eclipse.egit.github.core.Repository;

import com.google.common.base.Predicate;

public class AllRepos implements Predicate<Repository>
{
	@Override
	public boolean apply(Repository input)
	{
		return true;
	}
}
