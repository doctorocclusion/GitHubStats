package net.eekysam.ghstats.data;

import java.time.Duration;
import java.time.Instant;

import net.eekysam.ghstats.data.adapters.InstantAdapter;
import net.eekysam.ghstats.filter.FilterVar;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

public class RepoData
{
	@SerializedName("full_name")
	public String name;
	public long id;
	@SerializedName("private")
	public boolean isPrivate;
	public String description;
	@SerializedName("fork")
	public boolean isFork;
	@JsonAdapter(InstantAdapter.class)
	@SerializedName("created_at")
	public Instant createdAt;
	@JsonAdapter(InstantAdapter.class)
	@SerializedName("updated_at")
	public Instant updatedAt;
	@JsonAdapter(InstantAdapter.class)
	@SerializedName("pushed_at")
	public Instant pushedAt;
	public String homepage;
	public long size;
	@SerializedName("stargazers_count")
	public int stargazersCount;
	@SerializedName("watchers_count")
	public int watchersCount;
	@SerializedName("language")
	public String primaryLanguage;
	@SerializedName("has_issues")
	public boolean hasIssues;
	@SerializedName("has_downloads")
	public boolean hasDownloads;
	@SerializedName("has_wiki")
	public boolean hasWiki;
	@SerializedName("has_pages")
	public boolean hasPages;
	@SerializedName("forks_count")
	public int forksCount;
	@SerializedName("mirror_url")
	public String mirrorURL;
	@SerializedName("open_issues_count")
	public int openIssuesCount;
	@SerializedName("default_branch")
	public String defaultBranch;
	@SerializedName("network_count")
	public int networkCount;
	@SerializedName("subscribers_count")
	public int subscribersCount;
	
	public Object getVar(FilterVar var)
	{
		switch (var)
		{
			case ID:
				return this.id;
			case IS_FORK:
				return this.isFork;
			case CREATED:
				return this.createdAt;
			case UPDATED:
				return this.updatedAt;
			case PUSHED:
				return this.pushedAt;
			case SIZE:
				return this.size;
			case STARS:
				return this.stargazersCount;
			case WATCHERS:
				return this.watchersCount;
			case FORKS:
				return this.forksCount;
			case SUBSCRIBERS:
				return this.subscribersCount;
			case AGE:
				return Duration.between(this.createdAt, Instant.now());
			default:
				return FilterVar.NOT_LOADED;
		}
	}
}
