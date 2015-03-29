package net.eekysam.ghstats.export;

import java.time.Duration;
import java.time.Instant;

public enum TimeExport
{
	UTC
	{
		@Override
		public String export(Instant time)
		{
			return time.toString();
		}
		
		@Override
		public String export(Duration time)
		{
			return time.toString();
		}
	},
	SECS
	{
		@Override
		public String export(Instant time)
		{
			return "" + time.getEpochSecond();
		}
		
		@Override
		public String export(Duration time)
		{
			return "" + time.getSeconds();
		}
	};
	
	public abstract String export(Instant time);
	
	public abstract String export(Duration time);
}
