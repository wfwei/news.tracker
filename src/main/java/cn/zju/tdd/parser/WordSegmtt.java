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
				"�����Ҵ�С�Ͳ�����������ϲ���޶�������ϲ�������˶�����ϲ�������⹤�������Ǹ�ϲ��˼����������ϲ����ѧ�Ϳ�ѧ������ϲ��һ���˹���������Լ���������������Ĵ���������ǿ����ϲ�����������ֺ�Ϸ����  ��ϲ���������ڵĹ�����������ϲ�������µĶ����� ��ϲ��������������ϲ���̱�������ϲ���ͻ����͹��ߴ򽻵�����ϲ�����쵼����ϲ����֯�����ʲô�����ִ򽻵���")
				.getWords();
		System.out.println(res);
	}

}
