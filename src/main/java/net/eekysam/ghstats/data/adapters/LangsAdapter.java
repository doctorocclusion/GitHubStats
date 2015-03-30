package net.eekysam.ghstats.data.adapters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class LangsAdapter extends TypeAdapter<HashMap<String, Long>>
{
	@Override
	public void write(JsonWriter out, HashMap<String, Long> value) throws IOException
	{
		if (value == null)
		{
			out.nullValue();
			return;
		}
		out.beginObject();
		if (value != null)
		{
			for (Entry<String, Long> lang : value.entrySet())
			{
				out.name(lang.getKey());
				out.value(lang.getValue());
			}
		}
		out.endObject();
	}
	
	@Override
	public HashMap<String, Long> read(JsonReader in) throws IOException
	{
		if (in.peek() == JsonToken.NULL)
		{
			return null;
		}
		HashMap<String, Long> langs = new HashMap<String, Long>();
		in.beginObject();
		while (in.hasNext())
		{
			langs.put(in.nextName(), in.nextLong());
		}
		in.endObject();
		return langs;
	}
}
