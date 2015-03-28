package net.eekysam.ghstats.data.adapters;

import java.io.IOException;

import net.eekysam.ghstats.Main;
import net.eekysam.ghstats.data.DataFile;
import net.eekysam.ghstats.data.RepoEntry;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class DataAdapter extends TypeAdapter<DataFile>
{
	@Override
	public void write(JsonWriter out, DataFile value) throws IOException
	{
		out.beginArray();
		for (RepoEntry repo : value.repos.values())
		{
			Main.gson.toJson(repo, RepoEntry.class, out);
		}
		out.endArray();
	}
	
	@Override
	public DataFile read(JsonReader in) throws IOException
	{
		DataFile file = new DataFile();
		in.beginArray();
		while (in.hasNext())
		{
			RepoEntry re = Main.gson.fromJson(in, RepoEntry.class);
			file.repos.put(re.name, re);
		}
		in.endArray();
		return file;
	}
}