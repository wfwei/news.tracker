package cn.zju.tdd.fetcher;

import org.apache.log4j.Logger;

import cn.zju.tdd.util.HttpUtil;

public class PageFetcher {

	private static Logger logger = Logger.getLogger(PageFetcher.class);

	public static String fetchHTML(String strURL) {
		try {
			return HttpUtil.fetchPage(strURL);
		} catch (Exception e) {
			logger.error("page fetch error\n" + e.toString());
		}
		return null;
	}

	public static void main(String args[]) {
		System.out.println(fetchHTML("http://www.sxdpf.org.cn/wzdt/wzdt.aspx"));
	}

}
