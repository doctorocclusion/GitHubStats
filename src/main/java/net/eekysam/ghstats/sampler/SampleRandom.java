package net.eekysam.ghstats.sampler;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

import net.eekysam.ghstats.Action;
import net.eekysam.ghstats.GitHub;
import net.eekysam.ghstats.data.DataFile;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.uncommons.maths.random.DefaultSeedGenerator;
import org.uncommons.maths.random.SeedException;

public class SampleRandom extends Action
{
	public static Options options = new Options();
	
	private CommandLine cmd;
	
	public SampleRandom(GitHub gh, DataFile data, String[] pars) throws IOException, ParseException
	{
		super(gh, data);
		CommandLineParser parser = new BasicParser();
		this.cmd = parser.parse(SampleRandom.options, pars);
		
		System.out.printf("%d repos stored%n", data.repos.size());
		
		int max = Integer.parseInt(this.cmd.getOptionValue("m"));
		int sampleSize = Integer.parseInt(this.cmd.getOptionValue("s"));
		int clusterSize = Integer.parseInt(this.cmd.getOptionValue("c"));
		
		RandomType randt = RandomType.MT;
		if (this.cmd.hasOption("r"))
		{
			randt = RandomType.valueOf(this.cmd.getOptionValue("r").toUpperCase());
		}
		Random random;
		try
		{
			random = randt.getRandom(DefaultSeedGenerator.getInstance());
		}
		catch (SeedException e)
		{
			e.printStackTrace();
			return;
		}
		
		ArrayList<String> sample = new ArrayList<String>();
		
		RepoSampler samp = new RepoSampler(this.gh, sample, max);
		Instant start = Instant.now();
		System.out.printf("Sampled %d repos in %.1fs%n", samp.sample(sampleSize, clusterSize, random), Duration.between(start, Instant.now()).toMillis() / 1000.0F);
		
		for (String repo : sample)
		{
			this.data.addRepo(repo, true);
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
		options.addOption(OptionBuilder.hasArg().isRequired(true).withLongOpt("sample").create("s"));
		options.addOption(OptionBuilder.hasArg().isRequired(true).withLongOpt("cluster").create("c"));
		options.addOption(OptionBuilder.hasArg().isRequired(true).withLongOpt("max").create("m"));
		options.addOption(OptionBuilder.hasArg().create("r"));
	}
}
