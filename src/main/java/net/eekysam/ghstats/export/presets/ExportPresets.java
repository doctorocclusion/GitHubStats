package net.eekysam.ghstats.export.presets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import net.eekysam.ghstats.data.RepoEntry;
import net.eekysam.ghstats.export.ExportContext;

public enum ExportPresets
{
	WHEN
	{
		@Override
		public void export(BufferedWriter writer, List<RepoEntry> repos, ExportContext context) throws IOException
		{
			new BasicEventsExporter(context.time).export(writer, repos);;
		}
	},
	BOOLS
	{
		@Override
		public void export(BufferedWriter writer, List<RepoEntry> repos, ExportContext context) throws IOException
		{
			new BoolPieExporter().export(writer, repos);
		}
	},
	LANGS
	{
		@Override
		public void export(BufferedWriter writer, List<RepoEntry> repos, ExportContext context) throws IOException
		{
			LangExporter.filter(repos);
			Exporter num = new NumLangExporter(context.minLang);
			Exporter avg = new AvgLangExporter(context.minLang);
			Exporter perc = new PercentLangExporter(context.minLang);
			new ComboExporter(num, avg, perc).export(writer, repos);;
		}
	};
	
	public abstract void export(BufferedWriter writer, List<RepoEntry> repos, ExportContext context) throws IOException;
}
