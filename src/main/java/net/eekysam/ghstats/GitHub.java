package net.eekysam.ghstats;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class GitHub
{
	public static class GitHubException extends IOException
	{
		private static final long serialVersionUID = 7534448844623765875L;
		
		public GHResponse response;
		
		public GitHubException(GHResponse response)
		{
			super();
			this.response = response;
		}
	}
	
	public static class GHResponse
	{
		public final InputStream response;
		public final int code;
		public final String responseMessage;
		public final Map<String, List<String>> header;
		
		GHResponse(InputStream response, int code, String responseMessage, Map<String, List<String>> header)
		{
			super();
			this.response = response;
			this.code = code;
			this.responseMessage = responseMessage;
			this.header = header;
		}
	}
	
	private String auth = null;
	public boolean https = true;
	
	public GitHub()
	{
		this(null);
	}
	
	public GitHub(String auth)
	{
		this.auth = auth;
	}
	
	public HttpURLConnection newConnection(URL url) throws IOException
	{
		HttpURLConnection.setFollowRedirects(true);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		if (this.auth != null)
		{
			con.addRequestProperty("Authorization", "token " + this.auth);
		}
		con.setRequestMethod("GET");
		return con;
	}
	
	public HttpURLConnection newConnection(Query query) throws IOException
	{
		String path = this.https ? "https://" : "http://";
		path += "api.github.com/";
		return this.newConnection(query.getURL(path));
	}
	
	public GHResponse get(Query query) throws IOException
	{
		HttpURLConnection con = this.newConnection(query);
		con.connect();
		GHResponse re = new GHResponse(con.getInputStream(), con.getResponseCode(), con.getResponseMessage(), con.getHeaderFields());
		if (re.code != HttpURLConnection.HTTP_OK)
		{
			throw new GitHubException(re);
		}
		return re;
	}
	
	public JsonReader getJsonReader(Query query) throws IOException
	{
		GHResponse re = this.get(query);
		return new JsonReader(new InputStreamReader(re.response));
	}
	
	public JsonElement getJson(Query query) throws IOException, JsonParseException
	{
		return new JsonParser().parse(this.getJsonReader(query));
	}
}
