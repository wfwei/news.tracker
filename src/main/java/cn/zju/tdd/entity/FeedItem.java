package cn.zju.tdd.entity;

import java.util.Date;


public class FeedItem {

	private long id;
	private String title;
	private String link;
	private String category;
	private String description;
	private Date pubDate;
	private String author;
	private String feedUrl;
	private int status;

	public static final int ST_JUST_STORED = 0;
	public static final int ST_DISTRIBUTED = 1;
	public static final int ST_PAGE_FETCHED = 2;


	@Override
	public String toString() {
		// TODO refine
		return "RssItem:\ttitle:" + title + "\tlink:" + link;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFeedUrl() {
		return feedUrl;
	}

	public void setFeedUrl(String feedUrl) {
		this.feedUrl = feedUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String newsTitle) {
		this.title = newsTitle;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getPublishDate() {
		return pubDate;
	}

	public void setPublishDate(Date publishDate) {
		this.pubDate = publishDate;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
