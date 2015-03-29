package net.eekysam.ghstats.sampler;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import net.eekysam.ghstats.Action;
import net.eekysam.ghstats.data.DataFile;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.jcabi.github.Github;
import com.jcabi.github.Repo;

public class SampleRandom extends Action
{
	public static Options options = new Options();
	
	public HashSet<Repo> sample = new HashSet<Repo>();
	private CommandLine cmd;
	
	public SampleRandom(Github gh, DataFile data, String[] pars) throws IOException, ParseException
	{
		super(gh, data);
		CommandLineParser parser = new BasicParser();
		this.cmd = parser.parse(SampleRandom.options, pars);
		
		System.out.printf("%d repos stored%n", data.repos.size());
		
		int max = Integer.parseInt(this.cmd.getOptionValue("m"));
		int sampleSize = Integer.parseInt(this.cmd.getOptionValue("s"));
		int clusterSize = Integer.parseInt(this.cmd.getOptionValue("c"));
		Random rand = new Random();
		if (this.cmd.hasOption("r"))
		{
			rand = new Random(Long.parseLong(this.cmd.getOptionValue("r")));
		}
		
		ArrayList<Repo> sample = new ArrayList<Repo>();
		
		RepoSampler samp = new RepoSampler(this.gh, sample, max);
		Instant start = Instant.now();
		System.out.printf("Sampled %d repos in %.1fs%n", samp.sample(sampleSize, clusterSize, rand), Duration.between(start, Instant.now()).toMillis() / 1000.0F);
		
		for (Repo repo : sample)
		{
			String name = repo.coordinates().user() + "/" + repo.coordinates().repo();
			this.data.addRepo(name, true);
		}
		
		System.out.printf("%d repos stored%n", data.repos.size());
	}
	
	static
	{
		SampleRandom.getOptions(SampleRandom.options);
	}
	
	@SuppressWarnings("static-access")
	static void getOptions(Options options)
	{
		options.addOption(OptionBuilder.hasArg().isRequired(true).create("s"));
		options.addOption(OptionBuilder.hasArg().isRequired(true).create("c"));
		options.addOption(OptionBuilder.hasArg().isRequired(true).create("m"));
		options.addOption(OptionBuilder.hasArg().withLongOpt("seed").create("r"));
	}
}
