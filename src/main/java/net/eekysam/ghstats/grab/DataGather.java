package net.eekysam.ghstats.grab;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.eekysam.ghstats.Action;
import net.eekysam.ghstats.GitHub;
import net.eekysam.ghstats.GitHub.GitHubException;
import net.eekysam.ghstats.Query;
import net.eekysam.ghstats.data.DataFile;
import net.eekysam.ghstats.data.RepoEntry;

public class DataGather extends Action
{
	public static Options options = new Options();
	
	private CommandLine cmd;
	
	private int numError = 0;
	
	public DataGather(GitHub gh, DataFile data, String[] pars) throws ParseException
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
					if (!this.gather(repo, req))
					{
						break;
					}
					num++;
				}
			}
		}
		System.out.printf("Gathered info for %d repos%n", num);
	}
	
	public boolean gather(RepoEntry entry, GatherReq req)
	{
		Query query = Query.start("repos").path(entry.name);
		if (req == GatherReq.REPO)
		{
			try
			{
				this.data.readRepo(this.gh.getJson(query), true, Instant.now());
				this.numError = 0;
			}
			catch (Exception e)
			{
				System.err.printf("Error when gathering %s for %s%n", req.name(), entry.name);
				return !this.shouldErrorStop(e);
			}
		}
		else if (req == GatherReq.LANGS)
		{
			try
			{
				JsonObject json = this.gh.getJson(query.path("languages")).getAsJsonObject();
				HashMap<String, Long> langs = new HashMap<String, Long>();
				for (Entry<String, JsonElement> lang : json.entrySet())
				{
					langs.put(lang.getKey(), lang.getValue().getAsLong());
				}
				entry.langs = langs;
				entry.reqs.put(req, Instant.now());
				this.numError = 0;
			}
			catch (Exception e)
			{
				System.err.printf("Error when gathering % for %s%n", req.name(), entry.name);
				return !this.shouldErrorStop(e);
			}
		}
		return true;
	}
	
	public boolean shouldErrorStop(Exception e)
	{
		this.numError++;
		e.printStackTrace();
		if (e instanceof GitHubException)
		{
			GitHubException ghe = (GitHubException) e;
			if (Integer.parseInt(ghe.response.header.get("X-RateLimit-Remaining")) == 0)
			{
				return true;
			}
		}
		if (this.numError > 10)
		{
			return true;
		}
		return false;
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
