package net.eekysam.ghstats;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import net.eekysam.ghstats.filter.Filterer;
import net.eekysam.ghstats.sampler.SampleRandom;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;

public class Main
{
	public static void main(String[] args)
	{
		int split = Arrays.asList(args).indexOf(":");
		String[] projargs = args;
		String[] operargs = new String[0];
		if (split != -1)
		{
			operargs = Arrays.copyOfRange(args, split + 1, args.length);
			projargs = Arrays.copyOfRange(args, 0, split);
		}
		
		Options options = new Options();
		Main.getOptions(options);
		CommandLineParser parser = new BasicParser();
		CommandLine cmd;
		try
		{
			cmd = parser.parse(options, projargs);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			return;
		}
		
		Github gh = null;
		if (cmd.hasOption("u"))
		{
			gh = new RtGithub(cmd.getOptionValue("u"));
		}
		else
		{
			gh = new RtGithub();
		}
		
		if (cmd.hasOption("l"))
		{
			LimitInfo li = LimitInfo.get(gh);
			System.out.println("Core " + li.core);
			System.out.println("Search " + li.search);
		}
		else
		{
			File file = null;
			if (!cmd.hasOption("f"))
			{
				System.out.println("WARNING! A file was not specified!");
			}
			else
			{
				file = new File(cmd.getOptionValue("f"));
			}
			if (cmd.hasOption("s"))
			{
				LimitInfo li = LimitInfo.get(gh);
				System.out.println(li.core);
				try
				{
					SampleRandom sr = new SampleRandom(gh, file, operargs);
					sr.sample();
					sr.write();
				}
				catch (IOException | ParseException e)
				{
					e.printStackTrace();
				}
				li = LimitInfo.get(gh);
				System.out.println(li.core);
			}
			else if (cmd.hasOption("x"))
			{
				try
				{
					Filterer filter = new Filterer(gh, file, operargs);
				}
				catch (IOException | ParseException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	public static void getOptions(Options options)
	{
		options.addOption(OptionBuilder.hasArg().withLongOpt("file").create("f"));
		options.addOption(OptionBuilder.hasArg().withLongOpt("oauth").create("u"));
		OptionGroup action = new OptionGroup();
		action.setRequired(true);
		action.addOption(new Option("l", "limit", false, ""));
		action.addOption(new Option("s", "sample", false, ""));
		action.addOption(new Option("x", "filter", false, ""));
		action.addOption(new Option("g", "gather", false, ""));
		action.addOption(new Option("e", "export", false, ""));
		options.addOptionGroup(action);
	}
}
