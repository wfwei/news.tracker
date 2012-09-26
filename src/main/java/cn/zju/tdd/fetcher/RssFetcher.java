package cn.zju.tdd.fetcher;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import cn.zju.tdd.entity.FeedItem;

public class RssFetcher {

	private static Logger logger = Logger.getLogger(RssFetcher.class);

	/**
	 * 根据feedUrl，获取并返回获取到的rss条目.
	 * 
	 * @param feedUrl
	 * @return
	 */
	public static FeedItem[] fetchRss(String feedUrl) {
		List<FeedItem> RssFetched = null;
		try {
			URL url = new URL(feedUrl);
			// 读取Rss源
			XmlReader reader = new XmlReader(url);
			SyndFeedInput input = new SyndFeedInput();
			// 得到SyndFeed对象，即得到Rss源里的所有信息
			SyndFeed feed = input.build(reader);
			// 得到Rss新闻中子项列表
			List entries = feed.getEntries();
			RssFetched = new ArrayList<FeedItem>();
			for (int i = 0; i < entries.size(); i++) {
				SyndEntry entry = (SyndEntry) entries.get(i);
				FeedItem item = new FeedItem();

				// 标题、连接地址、标题简介、时间是一个Rss源项最基本的组成部分
				// logger.debug("标题：" + entry.getTitle());
				// logger.debug("连接地址：" + entry.getLink());
				// logger.debug("标题简介：" + entry.getDescription().getValue());
				// logger.debug("发布时间：" + entry.getPublishedDate());
				// logger.debug("标题的作者：" + entry.getAuthor());

				item.setFeedUrl(feedUrl);
				item.setTitle(entry.getTitle());
				item.setLink(entry.getLink());
				item.setDescription(entry.getDescription().getValue());
				item.setPublishDate(entry.getPublishedDate());
				item.setAuthor(entry.getAuthor());
				RssFetched.add(item);
			}
			logger.info("成功获取RSS:\t" + feedUrl + "\t数量：" + entries.size());
			return RssFetched.toArray(new FeedItem[entries.size()]);
		} catch (Exception e) {
			logger.warn("获取rss过程出错" + e.toString());
		}
		return null;
	}

	public static String fetchRss(String feedUrl, List<FeedItem> rssFetched) {
		String continuation = null;

		try {
			URL url = new URL(feedUrl);
			// 读取Rss源
			XmlReader reader = new XmlReader(url);
			SyndFeedInput input = new SyndFeedInput();
			// 得到SyndFeed对象，即得到Rss源里的所有信息
			SyndFeed feed = input.build(reader);
			// 得到Rss新闻中子项列表
			@SuppressWarnings("unchecked")
			List<SyndEntry> entries = feed.getEntries();

			// 得到Rss的Continuation字符串
			continuation = feed.toString();

			// 重置rssFetched
			rssFetched.clear();
			for (int i = 0; i < entries.size(); i++) {
				SyndEntry entry = entries.get(i);
				FeedItem item = new FeedItem();

				// 标题、连接地址、标题简介、时间是一个Rss源项最基本的组成部分
				logger.debug("标题：" + entry.getTitle());
				logger.debug("连接地址：" + entry.getLink());
				logger.debug("标题简介：" + entry.getDescription().getValue());
				logger.debug("发布时间：" + entry.getPublishedDate());
				logger.debug("标题的作者：" + entry.getAuthor());

				item.setFeedUrl(feedUrl);
				item.setTitle(entry.getTitle());
				item.setLink(entry.getLink());
				item.setDescription(entry.getDescription().getValue());
				item.setPublishDate(entry.getPublishedDate());
				item.setAuthor(entry.getAuthor());

				rssFetched.add(item);
			}
			logger.info("成功获取RSS:\t" + feedUrl + "\t数量：" + entries.size());

		} catch (Exception e) {
			logger.warn("获取rss过程出错" + e.toString());
		}

		return continuation;
	}

	public static FeedItem[] fetchRss(InputStream xmlStream, String feedUrl) {
		List<FeedItem> RssFetched = null;
		try {
			XmlReader reader = new XmlReader(xmlStream);
			SyndFeedInput input = new SyndFeedInput();
			// 得到SyndFeed对象，即得到Rss源里的所有信息
			SyndFeed feed = input.build(reader);
			// 得到Rss新闻中子项列表
			List entries = feed.getEntries();
			RssFetched = new ArrayList<FeedItem>();
			for (int i = 0; i < entries.size(); i++) {
				SyndEntry entry = (SyndEntry) entries.get(i);
				FeedItem item = new FeedItem();

				// 标题、连接地址、标题简介、时间是一个Rss源项最基本的组成部分
				// logger.debug("标题：" + entry.getTitle());
				// logger.debug("连接地址：" + entry.getLink());
				// logger.debug("标题简介：" + entry.getDescription().getValue());
				// logger.debug("发布时间：" + entry.getPublishedDate());
				// logger.debug("标题的作者：" + entry.getAuthor());

				item.setFeedUrl(feedUrl);
				item.setTitle(entry.getTitle());
				item.setLink(entry.getLink());
				item.setDescription(entry.getDescription().getValue());
				item.setPublishDate(entry.getPublishedDate());
				item.setAuthor(entry.getAuthor());
				RssFetched.add(item);
			}
			logger.info("成功获取RSS数量：" + entries.size());
			return RssFetched.toArray(new FeedItem[entries.size()]);
		} catch (Exception e) {
			logger.warn("获取rss过程出错" + e.toString());
		}
		return null;

	}

	public static void main(String[] args) {
		List<FeedItem> rssFetched = new ArrayList<FeedItem>();
		String continuation = fetchRss(
				"http://www.google.com/reader/atom/feed/http://xlvector.net/blog/?feed=rss2?n=10",
				rssFetched);
		System.out.println("continuation:" + continuation);
	}
}
