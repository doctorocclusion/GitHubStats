package net.eekysam.ghstats;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Random;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.eekysam.ghstats.RepoSampler.SampleInfo;

import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import com.google.common.base.Predicate;

public class TestMain
{
	public static void main(String[] args) throws IOException
	{
		OptionParser parse = new OptionParser();
		parse.accepts("a").withRequiredArg();
		parse.accepts("m").withRequiredArg().ofType(Integer.class);
		
		OptionSet ops = parse.parse(args);
		
		GitHub gh;
		if (ops.has("a"))
		{
			gh = GitHub.connectUsingOAuth((String) ops.valueOf("a"));
		}
		else
		{
			gh = GitHub.connectAnonymously();
		}
		
		TestMain.printRateInfo(gh);
		
		int max = 32000000;
		if (ops.has("m"))
		{
			max = (Integer) ops.valueOf("m");
		}
		RepoSampler sampler = new RepoSampler(gh, max);
		Predicate<SampleInfo> sampleWhile = new Predicate<SampleInfo>()
		{
			@Override
			public boolean apply(SampleInfo input)
			{
				return input.size <= 1000 && input.requestsUsed <= 10;
			}
		};
		HashSet<GHRepository> repos = new HashSet<GHRepository>();
		SampleInfo info = sampler.sample(sampleWhile, repos, 50, new Random());
		
		System.out.printf("Completed a sample of size %d with %d requests of %d pages.%n", info.size, info.requestsUsed, info.pages);
		TestMain.printRateInfo(gh);
		
		System.out.println();
		
		File out = null;
		int n = 0;
		do
		{
			out = new File("out/" + n + ".ghstats.txt");
			n++;
		}
		while (out.exists());
		out.createNewFile();
		PrintWriter data = new PrintWriter(out);
		
		for (GHRepository repo : repos)
		{
			System.out.printf("%d - %s%n", repo.getId(), repo.getFullName());
			data.println(repo.getFullName());
		}
		
		data.close();
	}
	
	public static void printRateInfo(GitHub gh) throws IOException
	{
		GHRateLimit rl = gh.getRateLimit();
		Duration reset = Duration.between(Instant.now(), rl.getResetDate().toInstant());
		System.out.printf("RateLimit: %d/%d (reset in %dm%ds)%n", rl.remaining, rl.limit, reset.toMinutes(), reset.getSeconds() - reset.toMinutes() * 60);
	}
}
