package net.eekysam.ghstats;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
		int split = Arrays.asList(args).indexOf("|");
		String[] projargs = args;
		String[] operargs = new String[0];
		if (split != -1)
		{
			Arrays.copyOfRange(args, split + 1, args.length);
			Arrays.copyOfRange(args, 0, split);
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
			if (!cmd.hasOption("f"))
			{
				System.out.println("Must specify a file!");
				return;
			}
			File file = new File(cmd.getOptionValue("f"));
			EnumOutType ot = EnumOutType.NEW;
			LimitInfo li = LimitInfo.get(gh);
			System.out.println(li.core);
			if (cmd.hasOption("o"))
			{
				ot = EnumOutType.OVERWRITE;
			}
			else if (cmd.hasOption("a"))
			{
				ot = EnumOutType.APPEND;
			}
			if (cmd.hasOption("s"))
			{
				try
				{
					SampleRandom sr = new SampleRandom(gh, file, ot);
					sr.sample(operargs);
					sr.write();
				}
				catch (IOException | ParseException e)
				{
					e.printStackTrace();
				}
			}
			li = LimitInfo.get(gh);
			System.out.println(li.core);
		}
	}
	
	@SuppressWarnings("static-access")
	public static void getOptions(Options options)
	{
		options.addOption(OptionBuilder.hasArg().withLongOpt("file").create("f"));
		OptionGroup fileedit = new OptionGroup();
		fileedit.addOption(new Option("o", "overwrite", false, ""));
		fileedit.addOption(new Option("a", "append", false, ""));
		options.addOptionGroup(fileedit);
		options.addOption(OptionBuilder.hasArg().withLongOpt("oauth").create("u"));
		OptionGroup action = new OptionGroup();
		action.setRequired(true);
		action.addOption(new Option("s", "sample", false, ""));
		action.addOption(new Option("l", "limit", false, ""));
		action.addOption(new Option("g", "gather", false, ""));
		action.addOption(new Option("x", "export", false, ""));
		options.addOptionGroup(action);
	}
}
