package net.eekysam.ghstats.data;

import java.io.StringWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.json.Json;

import net.eekysam.ghstats.Main;
import net.eekysam.ghstats.data.adapters.DataAdapter;
import net.eekysam.ghstats.grab.GatherReq;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(DataAdapter.class)
public class DataFile
{
	public HashMap<String, RepoEntry> repos = new HashMap<String, RepoEntry>();
	
	public RepoEntry addRepo(String name, boolean direct)
	{
		if (name == null)
		{
			return null;
		}
		RepoEntry existing = this.repos.get(name);
		if (existing == null)
		{
			RepoEntry re = new RepoEntry();
			re.direct = direct;
			re.name = name;
			re.selected = false;
			this.repos.put(name, re);
			return re;
		}
		else
		{
			existing.direct |= direct;
			return existing;
		}
	}
	
	public List<RepoEntry> selected()
	{
		ArrayList<RepoEntry> sel = new ArrayList<RepoEntry>();
		for (RepoEntry repo : this.repos.values())
		{
			if (repo.selected)
			{
				sel.add(repo);
			}
		}
		return sel;
	}
	
	public RepoEntry addRepo(RepoData data, boolean direct, Instant req)
	{
		if (data == null)
		{
			return null;
		}
		RepoEntry re = this.addRepo(data.name, direct);
		if (re == null)
		{
			return null;
		}
		re.repoData = data;
		if (req != null)
		{
			re.reqs.put(GatherReq.REPO, req);
		}
		return re;
	}
	
	public void readRepo(JsonElement json, boolean direct, Instant req)
	{
		if (json == null || !json.isJsonObject())
		{
			return;
		}
		JsonObject jo = json.getAsJsonObject();
		this.addRepo(Main.gson.fromJson(jo, RepoData.class), direct, req);
		this.readRepo(jo.get("parent"), false, req);
		this.readRepo(jo.get("source"), false, req);
	}
	
	public static JsonElement fromJavax(javax.json.JsonStructure javax)
	{
		StringWriter out = new StringWriter();
		Json.createWriter(out).write(javax);
		return (new JsonParser()).parse(out.toString());
	}
}
