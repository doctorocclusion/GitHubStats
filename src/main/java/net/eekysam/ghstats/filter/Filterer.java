package net.eekysam.ghstats.filter;

import java.io.IOException;
import java.util.Arrays;

import net.eekysam.ghstats.Action;
import net.eekysam.ghstats.GitHub;
import net.eekysam.ghstats.data.DataFile;
import net.eekysam.ghstats.data.RepoEntry;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Filterer extends Action
{
	public static Options options = new Options();
	public static FilterEval evaler = new FilterEval();
	
	private CommandLine cmd;
	
	public Filterer(GitHub gh, DataFile data, String[] pars) throws IOException, ParseException
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
		
		if (this.cmd.hasOption("i"))
		{
			int num = 0;
			for (RepoEntry repo : this.data.repos.values())
			{
				if (repo.selected)
				{
					num++;
				}
			}
			System.out.printf("%d/%d repos selected%n", num, this.data.repos.size());
		}
		else if (this.cmd.hasOption("t"))
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
			int numa = 0;
			int numr = 0;
			int num = 0;
			
			if (this.cmd.hasOption("s"))
			{
				for (RepoEntry repo : this.data.repos.values())
				{
					Object result = Filterer.evaler.evaluate(expr, repo);
					if (result instanceof Boolean)
					{
						boolean old = repo.selected;
						repo.selected = (Boolean) result;
						boolean now = repo.selected;
						if (old && !now)
						{
							numr++;
						}
						else if (!old && now)
						{
							numa++;
						}
					}
					else if (result == FilterVar.NOT_LOADED)
					{
						System.out.printf("Some data for %s was not loaded.%n", repo.name);
					}
					else
					{
						throw new IllegalArgumentException(String.format("The filter expression returned %s, it must return a boolean!", result.toString()));
					}
					if (repo.selected)
					{
						num++;
					}
				}
			}
			else if (this.cmd.hasOption("a"))
			{
				for (RepoEntry repo : this.data.repos.values())
				{
					if (!repo.selected)
					{
						Object result = Filterer.evaler.evaluate(expr, repo);
						if (result instanceof Boolean)
						{
							if ((Boolean) result)
							{
								repo.selected = true;
								numa++;
							}
						}
						else
						{
							throw new IllegalArgumentException(String.format("The filter expression returned %s, it must return a boolean!", result.toString()));
						}
					}
					if (repo.selected)
					{
						num++;
					}
				}
			}
			else if (this.cmd.hasOption("r"))
			{
				for (RepoEntry repo : this.data.repos.values())
				{
					if (repo.selected)
					{
						Object result = Filterer.evaler.evaluate(expr, repo);
						if (result instanceof Boolean)
						{
							if ((Boolean) result)
							{
								repo.selected = false;
								numr++;
							}
						}
						else
						{
							throw new IllegalArgumentException(String.format("The filter expression returned %s, it must return a boolean!", result.toString()));
						}
					}
					if (repo.selected)
					{
						num++;
					}
				}
			}
			System.out.printf("Selection +%d -%d (%d/%d)%n", numa, numr, num, this.data.repos.size());
		}
	}
	
	static
	{
		Filterer.getOptions(Filterer.options);
	}
	
	static void getOptions(Options options)
	{
		OptionGroup evals = new OptionGroup();
		evals.setRequired(true);
		evals.addOption(OptionBuilder.create("a"));
		evals.addOption(OptionBuilder.create("r"));
		evals.addOption(OptionBuilder.create("s"));
		evals.addOption(OptionBuilder.create("t"));
		evals.addOption(OptionBuilder.create("i"));
		options.addOptionGroup(evals);
	}
}
