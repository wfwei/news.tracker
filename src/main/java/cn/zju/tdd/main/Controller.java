package cn.zju.tdd.main;

import cn.zju.tdd.module.DownloadFeeds;
import cn.zju.tdd.module.DownloadNews;

public class Controller {
	public static void main(String[] args) {
		String feedUrl = "http://rss.sina.com.cn/news/china/focus15.xml";
		String category = "国内新闻";
		int lastNday = 10;
		new DownloadFeeds(feedUrl, category, lastNday).start();
		new DownloadNews().start();
	}
}
