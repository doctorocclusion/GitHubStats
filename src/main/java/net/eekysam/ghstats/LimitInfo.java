package net.eekysam.ghstats;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import com.google.gson.JsonObject;

public class LimitInfo
{
	public static class RateLimit
	{
		public final int limit;
		public final int remaining;
		public final Instant reset;
		
		public RateLimit(int limit, int remaining, long reset)
		{
			this.limit = limit;
			this.remaining = remaining;
			this.reset = Instant.ofEpochSecond(reset);
		}
		
		public static RateLimit fromJson(JsonObject json)
		{
			int limit = json.get("limit").getAsInt();
			int remaining = json.get("remaining").getAsInt();
			long reset = json.get("reset").getAsLong();
			return new RateLimit(limit, remaining, reset);
		}
		
		@Override
		public String toString()
		{
			Duration in = Duration.between(Instant.now(), this.reset);
			String extra = "";
			if (in.isNegative())
			{
				in = in.abs();
				extra = String.format("(reset %dm %ds ago)", in.toMinutes(), in.getSeconds() % 60);
			}
			else
			{
				extra = String.format("(reset in %dm %ds)", in.toMinutes(), in.getSeconds() % 60);
			}
			return String.format("Limit: %d/%d %s", this.remaining, this.limit, extra);
		}
	}
	
	public final RateLimit core;
	public final RateLimit search;
	
	public LimitInfo(RateLimit core, RateLimit search)
	{
		this.core = core;
		this.search = search;
	}
	
	public static LimitInfo get(GitHub gh)
	{
		JsonObject json;
		try
		{
			json = gh.getJson(Query.start("rate_limit")).getAsJsonObject().getAsJsonObject("resources");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		return new LimitInfo(RateLimit.fromJson(json.getAsJsonObject("core")), RateLimit.fromJson(json.getAsJsonObject("search")));
	}
}
