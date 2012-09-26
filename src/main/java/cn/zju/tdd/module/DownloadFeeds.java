package cn.zju.tdd.module;

import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import be.lechtitseb.google.reader.api.core.GoogleReader;
import be.lechtitseb.google.reader.api.model.exception.GoogleReaderException;
import cn.zju.tdd.dao.DB4Tdd;
import cn.zju.tdd.entity.FeedItem;

public class DownloadFeeds extends Thread {

	private static final Logger LOG = Logger.getLogger(DownloadFeeds.class);
	private GoogleReader googleReader;

	private String feedUrl, category;
	private Integer pastNday;

	public DownloadFeeds(String feedUrl, String category, int pastNday) {
		this.feedUrl = feedUrl;
		this.category = category;
		this.pastNday = pastNday;
		if (googleReader == null) {
			login();
		}
	}

	public void run() {
		fetchAndStoreFeedItems();
	}

	public void fetchAndStoreFeedItems() {

		try {
			Integer numberOfElements = 20;
			String continuation = null;
			boolean over = false;
			while (!over) {
				String result = googleReader.getFeedItems("feed/" + feedUrl,
						numberOfElements, continuation, pastNday);
				int extractedFeedNum = extractAndStoreFeed(result, feedUrl,
						category);
				continuation = extractContinuation(result);
				System.out.println("Stored Rss Feeds:\t" + extractedFeedNum
						+ "\t" + new Date(System.currentTimeMillis()));
				if (continuation == null) {
					// 能否仅用continuation字段判断终止条件
					over = true;
				} else {
					Thread.sleep(2 * 60 * 1000);
				}
			}
		} catch (GoogleReaderException e) {
			LOG.error("error in fetching feeds:\t" + e.toString());
		} catch (InterruptedException e) {
			LOG.warn("error in Thread.sleep:\t" + e.toString());
		}
	}

	/**
	 * 从xml中提取continuation字段.
	 * <p>
	 * 如提取<gr:continuation>CIGbk_XOrbIC</gr:continuation
	 * >中的CIGbk_XOrbIC，如果没有则返回null
	 * 
	 */
	private String extractContinuation(String src) {
		String continuation = null;
		Matcher match = Pattern.compile(
				"<gr:continuation>([^<>]*?)</gr:continuation>").matcher(src);
		if (match.find()) {
			continuation = match.group(1).trim();
		}
		return continuation;
	}

	private int extractAndStoreFeed(String src, String feedUrl, String category) {
		int extractedFeedNum = 0;
		List<FeedItem> feedsFetched = new ArrayList<FeedItem>();
		String regex = "<entry[^>]*?gr:crawl-timestamp-msec=\\\"(\\d+)\\\">"
				+ ".*?<title[^>]*>([^<]*?)</title>"
				+ ".*?<published>([^<]+)</published>"
				+ ".*?<link.*?href=\\\"([^\"]+)\\\""
				+ ".*?<summary[^>]*>([^<]+)</summary>"
				+ ".*?<author><name>([^<]*)</name></author>" + ".*?</entry>";
		Matcher match = Pattern.compile(regex).matcher(src);

		while (match.find()) {
			// String crawlTimestamp = match.group(1);//爬取时间爱你，暂时没用
			String title = match.group(2);
			String published = match.group(3);// 新闻的真正发布时间
			String link = match.group(4);
			String summary = match.group(5);
			String author = match.group(6);

			FeedItem fitem = new FeedItem();

			if (link != null && link.contains("url=http")) {
				// http://go.rss.sina.com.cn/redirect.php?url=http://news.sina.com.cn/c/2012-09-18/191725201315.shtml
				link = link.substring(link.indexOf("url=http") + 4,
						link.length());
				fitem.setLink(link);
			} else {
				fitem.setLink(link);
			}

			// if (crawlTimestamp != null) {
			// Date publishDate = new Date(Long.parseLong(crawlTimestamp));
			// fitem.setPublishDate(publishDate);
			// }
			if (published != null) {
				// TODO 写的太绕圈子了，简化一下
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss'Z'");
				TimeZone timeZoneGMT0 = TimeZone.getTimeZone("GMT+0");
				TimeZone timeZoneGMT8 = TimeZone.getTimeZone("GMT+8");
				sdf.setTimeZone(timeZoneGMT0);
				try {
					Date publishDate = sdf.parse(published);
					sdf.setTimeZone(timeZoneGMT8);
					publishDate = sdf.parse(sdf.format(publishDate));
					fitem.setPublishDate(publishDate);
				} catch (ParseException e) {
					LOG.error("error parsing publishDate:\t" + e.toString());
					e.printStackTrace();
				}

			}

			fitem.setTitle(title);
			fitem.setDescription(summary);
			fitem.setAuthor(author);

			fitem.setFeedUrl(feedUrl);
			fitem.setCategory(category);

			feedsFetched.add(fitem);
			extractedFeedNum++;
		}
		// 将获取到的feeds存到数据库
		saveFeedsToDB(feedsFetched);
		// 返回获取到的feeds个数

		LOG.info("Stored " + extractedFeedNum + " feeds to DB");
		return extractedFeedNum;
	}

	private void saveFeedsToDB(List<FeedItem> feedsToStore) {
		if (null != feedsToStore) {
			for (FeedItem fitem : feedsToStore) {
				DB4Tdd.insertFeedItem(fitem);
			}
		}
	}

	private void login() {
		try {
			Properties auth = new Properties();
			auth.load(new FileInputStream("auth.properties"));
			googleReader = new GoogleReader(auth.getProperty("auth.key"),
					auth.getProperty("auth.secret"));
			googleReader.login();
			LOG.info("Log in as " + auth.getProperty("auth.key") + " success!");
		} catch (Exception e) {
			LOG.error("Fail in Login!\n" + e.getLocalizedMessage());
		}
	}

}
