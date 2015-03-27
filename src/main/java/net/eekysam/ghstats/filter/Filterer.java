package net.eekysam.ghstats.filter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.primitives.Ints;
import com.jcabi.github.Github;

public class Filterer
{
	public static Options options = new Options();
	public static FilterEval evaler = new FilterEval();
	
	public Github gh;
	public File file;
	private CommandLine cmd;
	
	public Filterer(Github gh, File file, String[] pars) throws IOException, ParseException
	{
		this.gh = gh;
		this.file = file;
		
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
		evals.addOption(OptionBuilder.create("t"));
		options.addOptionGroup(evals);
	}
}
