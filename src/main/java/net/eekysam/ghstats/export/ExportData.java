package net.eekysam.ghstats.export;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.eekysam.ghstats.Action;
import net.eekysam.ghstats.GitHub;
import net.eekysam.ghstats.data.DataFile;

public class ExportData extends Action
{
	public static Options options = new Options();
	
	private CommandLine cmd;
	public ExportContext context;
	
	public ExportData(GitHub gh, DataFile data, String[] pars) throws ParseException
	{
		super(gh, data);
		
		CommandLineParser parser = new BasicParser();
		this.cmd = parser.parse(ExportData.options, pars);
		this.context = new ExportContext(this.cmd);
		
	}
	
	static
	{
		ExportData.getOptions(ExportData.options);
	}
	
	@SuppressWarnings("static-access")
	static void getOptions(Options options)
	{
		options.addOption(OptionBuilder.hasArg().isRequired(true).create("f"));
		options.addOption(OptionBuilder.hasArg().create("t"));
	}
}
