package cn.zju.tdd.entity;

import java.util.Date;

public class News {
	private long id;
	private String title;
	private String category;
	private String link;
	private Date reportTime;
	private String fullPage;
	private String newsBody;
	private String bodyWords;
	private int status;
	
	public static final int ST_PAGE_PETCHED= 1;
	public static final int ST_SEGEMENTED = 2;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Date getReportTime() {
		return reportTime;
	}

	public void setReportTime(Date reportTime) {
		this.reportTime = reportTime;
	}

	public String getFullPage() {
		return fullPage;
	}

	public void setFullPage(String fullPage) {
		this.fullPage = fullPage;
	}

	public String getNewsBody() {
		return newsBody;
	}

	public void setNewsBody(String newsBody) {
		this.newsBody = newsBody;
	}

	public String getBodyWords() {
		return bodyWords;
	}

	public void setBodyWords(String bodyWords) {
		this.bodyWords = bodyWords;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
