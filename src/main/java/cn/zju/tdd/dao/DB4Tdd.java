package cn.zju.tdd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

import cn.zju.tdd.entity.News;
import cn.zju.tdd.entity.FeedItem;

/**
 * TDD的数据库操作类.
 * 
 * @author WangFengwei
 */
public final class DB4Tdd {

	/** 数据库链接. */
	private static Connection con = MySqlDB.getCon();

	/** 日志. */
	private static final Logger LOGGER = Logger.getLogger(DB4Tdd.class);

	/**
	 * fix checkstyle warning.
	 */
	private DB4Tdd() {
	}

	/**
	 * 从rss表中得到一条没有没有下载news的rss条目.
	 * 
	 * @return 成功则返回得到的rss条目，失败返回null
	 */
	public static synchronized FeedItem getFeedItemToFetchNews() {
		String sql = "select id, title, link, category from rss where status = "
				+ FeedItem.ST_JUST_STORED + " limit 0,1";
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				FeedItem ritem = new FeedItem();
				ritem.setId(rs.getLong(1));
				ritem.setTitle(rs.getString(2));
				ritem.setLink(rs.getString(3));
				ritem.setCategory(rs.getString(4));
				ritem.setStatus(FeedItem.ST_DISTRIBUTED);
				// 修改状态，以免重复抓取
				setFeedItemStatus(rs.getLong(1), FeedItem.ST_DISTRIBUTED);
				return ritem;
			}

		} catch (SQLException e) {
			LOGGER.warn(e.toString());
		}
		return null;
	}

	/**
	 * 更改rss条目的状态.
	 * 
	 * @param FeedItemId
	 *            rss的id
	 * @param status
	 *            状态
	 */
	public static void setFeedItemStatus(final long FeedItemId, final int status) {
		String sql = "update rss set status = " + status + " where id = "
				+ FeedItemId;
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			LOGGER.warn(e.toString());
		}
	}

	/**
	 * 插入一条news，数据库中link属性定义了唯一性索引，索引没有去重.
	 * 
	 * @param news
	 *            要插入数据库的新闻条目
	 */
	public static void insertNews(final News news) {
		final String sql = "insert into news(title, category, link, reportTime, fullPage, newsBody, bodyWords, status)"
				+ " values (?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement pStmt = con.prepareStatement(sql);
			pStmt.setString(1, news.getTitle());
			pStmt.setString(2, news.getCategory());
			pStmt.setString(3, news.getLink());
			// TODO reportTime may be null
			pStmt.setString(4, new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss").format(news.getReportTime()));
			pStmt.setString(5, rmInvalidChar(news.getFullPage()));
			pStmt.setString(6, rmInvalidChar(news.getNewsBody()));
			pStmt.setString(7, rmInvalidChar(news.getBodyWords()));
			pStmt.setInt(8, news.getStatus());
			pStmt.executeUpdate();
			pStmt.close();
		} catch (SQLException e) {
			LOGGER.warn("insert news error:\t" + e.toString());
		}
	}

	/**
	 * 插入一条rss item，数据库中link属性定义了唯一性索引，索引没有去重.
	 * 
	 * @param item
	 *            要插入数据库的item
	 */

	public static void insertFeedItem(final FeedItem item) {
		String sql = "insert into rss(title, link, category, description, pubDate, author, feedUrl, status) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement pStmt = con.prepareStatement(sql);
			pStmt.setString(1, item.getTitle());
			pStmt.setString(2, item.getLink());
			pStmt.setString(3, item.getCategory());
			pStmt.setString(4, rmInvalidChar(item.getDescription()));
			// MARK mysql存储日期时间类型，java.sql.Date只有日期！！！
			if (item.getPublishDate() != null) {
				pStmt.setString(5, new java.text.SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss").format(item.getPublishDate()));
			} else {
				pStmt.setString(5, null);
			}
			pStmt.setString(6, item.getAuthor());
			pStmt.setString(7, item.getFeedUrl());
			pStmt.setInt(8, item.getStatus());
			pStmt.executeUpdate();
			pStmt.close();
		} catch (SQLException e) {
			LOGGER.warn("insert rss item error:\t" + e.toString());
		}
	}

	/**
	 * 替换不合法的字符，将'''替换成'’'.
	 * 
	 * @param input
	 *            输入字符串
	 * @return output 替换后的字符串
	 */
	private static String rmInvalidChar(final String input) {
		String output = null;
		if (input != null) {
			output = input.trim().replaceAll("'", "’");
		}
		return output;
	}

	public static void main(String[] args) {
		System.out.println("test");
	}

	public static void updateFeedItem(long feedItemId, String fullPage,
			String content, String bodyWords) {
		String sql = "update rss set fullPage = '" + rmInvalidChar(fullPage)
				+ "',content = '" + rmInvalidChar(content) + "',bodyWords = '"
				+ rmInvalidChar(bodyWords) + "', status = "
				+ FeedItem.ST_PAGE_FETCHED + " where id = " + feedItemId;
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			LOGGER.warn(e.toString());
		}

	}
}
