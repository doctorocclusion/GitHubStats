package net.eekysam.ghstats.export.presets;

import java.util.List;

import net.eekysam.ghstats.data.RepoEntry;
import net.eekysam.ghstats.export.ExportContext;

public enum ExportPresets
{
	WHEN
	{
		@Override
		public Exporter export(List<RepoEntry> repos, ExportContext context)
		{
			return new BasicEventsExporter(repos, context.time);
		}
	},
	BOOLS
	{
		@Override
		public Exporter export(List<RepoEntry> repos, ExportContext context)
		{
			return new BoolPieExporter(repos);
		}
	},
	LANGS
	{
		@Override
		public Exporter export(List<RepoEntry> repos, ExportContext context)
		{
			return null;
		}
		
	};
	
	public abstract Exporter export(List<RepoEntry> repos, ExportContext context);
	
	public static Exporter export(List<RepoEntry> repos, ExportContext context, ExportPresets... toExport)
	{
		Exporter[] exs = new Exporter[toExport.length];
		for (int i = 0; i < exs.length; i++)
		{
			exs[i] = toExport[i].export(repos, context);
		}
		return new ComboExporter(repos, exs);
	}
}
