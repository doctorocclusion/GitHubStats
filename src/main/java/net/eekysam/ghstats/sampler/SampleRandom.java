package net.eekysam.ghstats.sampler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;

public class SampleRandom
{
	public static Options options = new Options();
	
	public Github gh;
	public File file;
	public HashSet<Repo> sample = new HashSet<Repo>();
	private CommandLine cmd;
	
	public SampleRandom(Github gh, File file, String[] pars) throws IOException, ParseException
	{
		this.gh = gh;
		this.file = file;
		CommandLineParser parser = new BasicParser();
		this.cmd = parser.parse(SampleRandom.options, pars);
	}
	
	public void sample()
	{
		
	}
	
	public void write()
	{
		
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
		options.addOption(OptionBuilder.hasArg().withLongOpt("seed").create("r"));
	}
}
