package net.eekysam.ghstats.export.presets;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import net.eekysam.ghstats.data.RepoEntry;

public interface IExporter
{
	public void export(BufferedWriter writer, List<RepoEntry> repos) throws IOException;
}
