package net.eekysam.ghstats;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;

public class SampleRandom extends Action
{
	public static Options options = new Options();
	
	public HashSet<Repo> sample = new HashSet<Repo>();
	
	public SampleRandom(Github gh, File file, EnumOutType outtype) throws IOException
	{
		super(gh, file, outtype);
		if (file.exists())
		{
			if (EnumOutType.NEW == this.outtype)
			{
				throw new IOException("File already exists! Specify overwrite or append.");
			}
			else if (EnumOutType.APPEND == this.outtype)
			{
				List<String> lines = Files.readAllLines(file.toPath());
				for (String line : lines)
				{
					this.sample.add(gh.repos().get(new Coordinates.Simple(line)));
				}
			}
		}
		else
		{
			file.createNewFile();
		}
	}
	
	public void sample(String[] pars) throws ParseException
	{
		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(SampleRandom.options, pars);
	}
	
	public void write()
	{
		
	}
}
