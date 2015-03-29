package net.eekysam.ghstats.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.eekysam.ghstats.Action;
import net.eekysam.ghstats.GitHub;
import net.eekysam.ghstats.data.DataFile;
import net.eekysam.ghstats.data.RepoEntry;
import net.eekysam.ghstats.export.presets.ExportPresets;
import net.eekysam.ghstats.export.presets.Exporter;

public class ExportData extends Action
{
	public static Options options = new Options();
	
	private CommandLine cmd;
	public ExportContext context;
	
	public ExportData(GitHub gh, DataFile data, String[] pars) throws ParseException, IOException
	{
		super(gh, data);
		
		CommandLineParser parser = new BasicParser();
		this.cmd = parser.parse(ExportData.options, pars);
		this.context = new ExportContext(this.cmd);
		
		File file = new File(this.cmd.getOptionValue("f"));
		file.createNewFile();
		BufferedWriter writer = Files.newBufferedWriter(file.toPath());
		
		ArrayList<RepoEntry> repos = new ArrayList<RepoEntry>();
		for (RepoEntry repo : this.data.repos.values())
		{
			if (repo.selected)
			{
				repos.add(repo);
			}
		}
		
		if (this.cmd.hasOption("p"))
		{
			Exporter exporter = ExportPresets.valueOf(this.cmd.getOptionValue("p")).export(repos, this.context);
			exporter.export(writer);
		}
		
		writer.close();
	}
	
	static
	{
		ExportData.getOptions(ExportData.options);
	}
	
	@SuppressWarnings("static-access")
	static void getOptions(Options options)
	{
		options.addOption(OptionBuilder.hasArg().isRequired(true).create("f"));
		options.addOption(OptionBuilder.hasArg().create("p"));
		options.addOption(OptionBuilder.hasArg().create("t"));
	}
}
