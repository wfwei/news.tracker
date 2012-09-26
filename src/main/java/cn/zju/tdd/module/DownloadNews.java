package cn.zju.tdd.module;

import java.io.IOException;
import org.apache.log4j.Logger;

import cn.zju.tdd.dao.DB4Tdd;
import cn.zju.tdd.entity.FeedItem;
import cn.zju.tdd.fetcher.PageFetcher;
import cn.zju.tdd.parser.TextExtract;
import cn.zju.tdd.parser.WordSegmtt;

public class DownloadNews extends Thread {

	private static Logger logger = Logger.getLogger(DownloadNews.class);

	public void run() {
		while (true) {
			boolean finished = fetchAndStoreContent();
			long timeToSleep = finished ? 60 * 60 * 1000 : 6000;
			try {
				sleep(timeToSleep);
			} catch (InterruptedException e) {
			}
		}

	}

	private boolean fetchAndStoreContent() {
		FeedItem feedItem = DB4Tdd.getFeedItemToFetchNews();
		boolean finished = false;
		if (feedItem != null) {
			String fullPage = PageFetcher.fetchHTML(feedItem.getLink());
			String content = new TextExtract(fullPage).getText();
			String bodyWords = new WordSegmtt(content).getWords();// TODO test
			DB4Tdd.updateFeedItem(feedItem.getId(), fullPage, content,
					bodyWords);
		} else {
			finished = true;
		}
		return finished;
	}

	public static void main(String[] args) throws IOException {
		for (int i = 0; i < 1; i++) {
			Thread t = new Thread(new DownloadNews());
			t.start();
			logger.info(t.toString() + " started");
		}
	}
}
