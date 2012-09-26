package cn.zju.tdd.parser;

import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.SmartChineseAnalyzer;

public class WordSegmtt {
	String content;

	public WordSegmtt(String _content) {
		content = _content;
	}

	public String getWords() {
		StringBuffer sbWords = new StringBuffer();
		if (null != content) {
			Token nt = new Token();
			Analyzer ca = new SmartChineseAnalyzer(true);
			Reader sentence = new StringReader(content);
			TokenStream ts = ca.tokenStream("sentence", sentence);

			try {
				nt = ts.next(nt);
				while (nt != null) {
					sbWords.append(nt.term() + "||");
					nt = ts.next(nt);
				}
				ts.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sbWords.toString();
	}

	public static void main(String[] args) {
		String res = new WordSegmtt(
				"经过我从小就不由自主地你喜欢修东西吗？你喜欢体育运动吗？你喜欢在室外工作吗？你是个喜欢思考的人吗？你喜欢数学和科学课吗？你喜欢一个人工作吗？你对自己的智力自信吗？你的创造能力很强吗？你喜欢艺术，音乐和戏剧吗？  你喜欢自由自在的工作环境吗？你喜欢尝试新的东西吗？ 你喜欢帮助别人吗？你喜欢教别人吗？你喜欢和机器和工具打交道吗？你喜欢当领导吗？你喜欢组织活动吗？你什么和数字打交道吗？")
				.getWords();
		System.out.println(res);
	}

}
