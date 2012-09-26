package cn.zju.tdd.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextExtract {

	private List<String> lines;
	private final int blocksWidth;
	private int threshold;
	private String html;
	private boolean flag;
	private int start;
	private int end;
	private StringBuilder text;
	private ArrayList<Integer> indexDistribution;
	{
		lines = new ArrayList<String>();
		indexDistribution = new ArrayList<Integer>();
		text = new StringBuilder();
		blocksWidth = 3;
		flag = false;
		/* 当待抽取的网页正文中遇到成块的新闻标题未剔除时，只要增大此阈值即可。 */
		/* 阈值增大，准确率提升，召回率下降；值变小，噪声会大，但可以保证抽到只有一句话的正文 */
		threshold = 86;
	}

	public TextExtract(String _html) {
		html = _html;
		preProcess();
	}

	/**
	 * 
	 * @param _html
	 *            网页原文
	 * @param flag
	 *            true进行主题类判断, 省略此参数则默认为false
	 */
	public TextExtract(String _html, boolean _flag) {
		// TODO how to use: TextExtract(_html);
		flag = _flag; // true进行主题类判断, 省略此参数则默认为false
		html = _html;
		preProcess();
	}

	
	public String getText() {

		indexDistribution.clear();

		for (int i = 0; i < lines.size() - blocksWidth; i++) {
			int wordsNum = 0;
			for (int j = i; j < i + blocksWidth; j++) {
				lines.set(j, lines.get(j).replaceAll("\\s+", ""));
				wordsNum += lines.get(j).length();
			}
			indexDistribution.add(wordsNum);
			// System.out.println(wordsNum);
		}

		start = -1;
		end = -1;
		boolean boolstart = false, boolend = false;
		text.setLength(0);

		for (int i = 0; i < indexDistribution.size() - 1; i++) {
			if (indexDistribution.get(i) > threshold && !boolstart) {
				if (indexDistribution.get(i + 1).intValue() != 0
						|| indexDistribution.get(i + 2).intValue() != 0
						|| indexDistribution.get(i + 3).intValue() != 0) {
					boolstart = true;
					start = i;
					continue;
				}
			}
			if (boolstart) {
				if (indexDistribution.get(i).intValue() == 0
						|| indexDistribution.get(i + 1).intValue() == 0) {
					end = i;
					boolend = true;
				}
			}
			StringBuilder tmp = new StringBuilder();
			if (boolend) {
				// System.out.println(start+1 + "\t\t" + end+1);
				for (int ii = start; ii <= end; ii++) {
					if (lines.get(ii).length() < 5)
						continue;
					tmp.append(lines.get(ii) + "\n");
				}
				String str = tmp.toString();
				// System.out.println(str);
				if (str.contains("Copyright") || str.contains("版权所有"))
					continue;
				text.append(str);
				boolstart = boolend = false;
			}
		}
		return text.toString();
	}

	private void preProcess() {
		html = html.replaceAll("(?is)<!DOCTYPE.*?>", "");
		html = html.replaceAll("(?is)<!--.*?-->", ""); // remove html comment
		html = html.replaceAll("(?is)<script.*?>.*?</script>", ""); // remove
		// // javascript
		html = html.replaceAll("(?is)<style.*?>.*?</style>", ""); // remove css
		html = html.replaceAll("&.{2,5};|&#.{2,5};", " "); // remove special
		// // char
		html = html.replaceAll("(?is)<.*?>", "");
		// <!--[if !IE]>|xGv00|9900d21eb16fa4350a3001b3974a9415<![endif]-->
		lines = Arrays.asList(html.split("\n"));
	}

	private void setthreshold(int value) {
		threshold = value;
	}

	// TODO Not in use
	private boolean isRelativeReport(String str) {
		str = str.trim();
		if (str.startsWith("相关报道："))
			return true;
		if (str.length() < 120) {
			if (str.matches(".*([0-9]{2,4})(-|年)([0-9]{1,2})(-|月)([0-9]{1,2})(日)?\\s*([0-9]{2}:[0-9]{2})?")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * MARK not in use, we extract this info from rss feed
	 * <p>
	 * 2012年07月18日22:56 2012-07-18 20:56:00　 2012年07月19日 12:51 2012年07月19日00:25
	 * 
	 * @param html
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private Date getTime() {
		String timeReg = "([0-9]{2,4})(-|年)([0-9]{1,2})(-|月)([0-9]{1,2})(日)?\\s*([0-9]{1,2}):([0-9]{1,2})";
		Pattern timePtn = Pattern.compile(timeReg);
		Matcher match = null;
		Date reportTime = null;
		int minLen = 10000000;
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.length() < 5)
				continue;
			match = timePtn.matcher(line);
			if (match.find() && line.length() < minLen) {

				// for (int j = 1; j <= match.groupCount(); j++) {
				// System.out.println(j+"\t"+match.group(j));
				// }
				reportTime = new Date(Integer.parseInt(match.group(1)) - 1900,
						Integer.parseInt(match.group(3)),
						Integer.parseInt(match.group(5)),
						Integer.parseInt(match.group(7)),
						Integer.parseInt(match.group(8)));
				minLen = line.length();
			}
		}
		return reportTime;
	}


	public static void main(String args[]) {
		String html = cn.zju.tdd.fetcher.PageFetcher
				.fetchHTML("http://mil.news.sina.com.cn/2012-09-19/0909701398.html");
		TextExtract te = new TextExtract(html);
		String content = te.getText();
		Date t = te.getTime();
	}

}
