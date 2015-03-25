package net.eekysam.ghstats;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Random;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GsonUtils;
import org.eclipse.egit.github.core.service.RepositoryService;

public class TestMain
{
	public static void main(String[] args)
	{
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token(args[0]);
		RepositoryService service = new RepositoryService(client);
		RandomGHSampler sampler = new RandomGHSampler(service, new Random(), 33000000, new AllRepos());
		Collection<Repository> repos = sampler.sampleBySize(100, 2);
		for (Repository repo : repos)
		{
			System.out.printf("%d - %s%n", repo.getId(), repo.generateId());
		}
		String json = GsonUtils.toJson(repos.toArray(), false);
		File out = null;
		int n = 0;
		do
		{
			out = new File("out/" + n + ".ghstats.json");
			n++;
		}
		while (out.exists());
		try
		{
			out.createNewFile();
			PrintWriter print = new PrintWriter(out);
			print.print(json);
			print.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
