package net.eekysam.ghstats.export;

import org.apache.commons.cli.CommandLine;

public class ExportContext
{
	public TimeExport time = TimeExport.UTC;
	
	public ExportContext(CommandLine cmd)
	{
		if (cmd.hasOption("t"))
		{
			this.time = TimeExport.valueOf(cmd.getOptionValue("t").toUpperCase());
		}
	}
}
