package net.eekysam.ghstats.data.adapters;

import java.io.IOException;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map.Entry;

import net.eekysam.ghstats.grab.GrabReq;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class RequestLogAdapter extends TypeAdapter<EnumMap<GrabReq, Instant>>
{
	static final InstantAdapter instant = new InstantAdapter();
	
	@Override
	public void write(JsonWriter out, EnumMap<GrabReq, Instant> value) throws IOException
	{
		out.beginObject();
		if (value != null)
		{
			for (Entry<GrabReq, Instant> req : value.entrySet())
			{
				if (req.getValue() != null)
				{
					out.name(req.getKey().name());
					instant.write(out, req.getValue());
				}
			}
		}
		out.endObject();
	}
	
	@Override
	public EnumMap<GrabReq, Instant> read(JsonReader in) throws IOException
	{
		EnumMap<GrabReq, Instant> reqs = new EnumMap<GrabReq, Instant>(GrabReq.class);
		in.beginObject();
		while (in.hasNext())
		{
			String reqn = in.nextName();
			Instant time = instant.read(in);
			try
			{
				reqs.put(GrabReq.valueOf(reqn), time);
			}
			catch (IllegalArgumentException e)
			{
			}
		}
		in.endObject();
		return reqs;
	}
}
