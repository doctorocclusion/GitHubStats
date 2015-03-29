package net.eekysam.ghstats.grab;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;

import javax.json.JsonException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Language;
import com.jcabi.github.Repo;

import net.eekysam.ghstats.Action;
import net.eekysam.ghstats.data.DataFile;
import net.eekysam.ghstats.data.RepoEntry;

public class DataGather extends Action
{
	public static Options options = new Options();
	
	private CommandLine cmd;
	
	public DataGather(Github gh, DataFile data, String[] pars) throws ParseException
	{
		super(gh, data);
		
		CommandLineParser parser = new BasicParser();
		this.cmd = parser.parse(DataGather.options, pars);
		
		GatherReq req = GatherReq.valueOf(this.cmd.getOptionValue("g").toUpperCase());
		Instant gate = null;
		boolean useGate = false;
		
		if (this.cmd.hasOption("r"))
		{
			useGate = true;
			String gatev = this.cmd.getOptionValue("r");
			if (gatev != null)
			{
				gate = Instant.now().minus(Duration.parse(gatev));
			}
		}
		
		int num = 0;
		RepoEntry[] res = new RepoEntry[this.data.repos.size()];
		this.data.repos.values().toArray(res);
		for (RepoEntry repo : res)
		{
			if (repo.selected)
			{
				Instant last = repo.reqs.get(req);
				if (last == null || (useGate && (gate == null || last.isBefore(gate))))
				{
					this.gather(repo, req);
					num++;
				}
			}
		}
		System.out.printf("Gathered info for %d repos%n", num);
	}
	
	public void gather(RepoEntry entry, GatherReq req)
	{
		Repo repo = this.gh.repos().get(new Coordinates.Simple(entry.name));
		if (req == GatherReq.REPO)
		{
			try
			{
				this.data.readRepo(DataFile.fromJavax(repo.json()), true, Instant.now());
			}
			catch (IOException | JsonException | IllegalStateException e)
			{
				e.printStackTrace();
			}
		}
		else if (req == GatherReq.LANGS)
		{
			try
			{
				Iterator<Language> langsit = repo.languages().iterator();
				HashMap<String, Long> langs = new HashMap<String, Long>();
				while (langsit.hasNext())
				{
					Language lang = langsit.next();
					langs.put(lang.name(), lang.bytes());
				}
				entry.langs = langs;
				entry.reqs.put(req, Instant.now());
			}
			catch (IOException | JsonException | IllegalStateException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	static
	{
		DataGather.getOptions(DataGather.options);
	}
	
	@SuppressWarnings("static-access")
	static void getOptions(Options options)
	{
		options.addOption(OptionBuilder.hasArg().isRequired(true).create("g"));
		options.addOption(OptionBuilder.hasOptionalArg().create("r"));
	}
}
