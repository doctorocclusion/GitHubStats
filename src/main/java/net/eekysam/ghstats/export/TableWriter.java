package net.eekysam.ghstats.export;

import java.io.BufferedWriter;
import java.io.IOException;

public class TableWriter
{
	private BufferedWriter writer;
	private boolean lineRequired = false;
	private boolean tabRequired = false;
	
	public TableWriter(BufferedWriter writer)
	{
		this.writer = writer;
	}
	
	public void write(String item) throws IOException
	{
		if (this.lineRequired)
		{
			this.lineRequired = false;
			this.writer.newLine();
		}
		if (this.tabRequired)
		{
			this.writer.write("\t");
		}
		this.writer.write(item);
		this.tabRequired = true;
	}
	
	public void newLine()
	{
		this.tabRequired = false;
		this.lineRequired = true;
	}
}
