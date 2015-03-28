package net.eekysam.ghstats.filter;

import java.io.IOException;
import java.util.Arrays;

import net.eekysam.ghstats.Action;
import net.eekysam.ghstats.data.DataFile;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.jcabi.github.Github;

public class Filterer extends Action
{
	public static Options options = new Options();
	public static FilterEval evaler = new FilterEval();
	
	private CommandLine cmd;
	
	public Filterer(Github gh, DataFile data, String[] pars) throws IOException, ParseException
	{
		super(gh, data);
		
		int split = Arrays.asList(pars).indexOf(":");
		String expr = "";
		if (split != -1)
		{
			expr = String.join(" ", Arrays.copyOfRange(pars, split + 1, pars.length));
			pars = Arrays.copyOfRange(pars, 0, split);
		}
		
		CommandLineParser parser = new BasicParser();
		this.cmd = parser.parse(Filterer.options, pars);
		
		if (this.cmd.hasOption("t"))
		{
			Object out = Filterer.evaler.evaluate(expr);
			if (out == FilterVar.NOT_LOADED)
			{
				System.out.println("A variable's value was not accessible.");
			}
			else
			{
				System.out.println(out);
			}
		}
		else
		{
			
		}
	}
	
	static
	{
		Filterer.getOptions(Filterer.options);
	}
	
	@SuppressWarnings("static-access")
	static void getOptions(Options options)
	{
		OptionGroup evals = new OptionGroup();
		evals.setRequired(true);
		evals.addOption(OptionBuilder.create("a"));
		evals.addOption(OptionBuilder.create("r"));
		evals.addOption(OptionBuilder.create("s"));
		evals.addOption(OptionBuilder.create("t"));
		options.addOptionGroup(evals);
	}
}
