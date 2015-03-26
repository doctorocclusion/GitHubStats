package net.eekysam.ghstats;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import javax.json.JsonObject;

import com.jcabi.github.Github;
import com.jcabi.github.Limits;

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
			int limit = json.getInt("limit");
			int remaining = json.getInt("remaining");
			long reset = json.getJsonNumber("reset").longValue();
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
	
	public static LimitInfo get(Limits limits) throws IOException
	{
		RateLimit core = RateLimit.fromJson(limits.get("core").json());
		RateLimit search = RateLimit.fromJson(limits.get("search").json());
		return new LimitInfo(core, search);
	}
	
	public static LimitInfo get(Github gh)
	{
		try
		{
			return LimitInfo.get(gh.limits());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
