package net.eekysam.ghstats.data.adapters;

import java.io.IOException;
import java.time.Instant;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class InstantAdapter extends TypeAdapter<Instant>
{
	@Override
	public void write(JsonWriter out, Instant value) throws IOException
	{
		if (value != null)
		{
			out.value(value.toString());
		}
		else
		{
			out.nullValue();
		}
	}
	
	@Override
	public Instant read(JsonReader in) throws IOException
	{
		JsonToken next = in.peek();
		if (next == JsonToken.NULL)
		{
			in.skipValue();
			return null;
		}
		try
		{
			return Instant.parse(in.nextString());
		}
		catch (IllegalStateException e)
		{
			return null;
		}
	}
}
