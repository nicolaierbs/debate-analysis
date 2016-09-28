package eu.erbs.debates.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UIMAException;

import eu.erbs.debates.model.TalkEvent;

public class WordCountAnalysator extends AbstractAnalysator {

	private String[] words;

	public WordCountAnalysator(String... words){
		this.words = words;
	}

	@Override
	public  Map<String, Double> analyse(List<TalkEvent> talkEvents) throws UIMAException {
		Map<String, Double> wordCounts = new HashMap<>();

		for(String word : words){
			int count = 0;
			for(TalkEvent talkEvent : talkEvents){
				count += StringUtils.countMatches(talkEvent.getText().toLowerCase(), word.toLowerCase());
			}
			wordCounts.put(word, (double) count);
		}

		return wordCounts;
	}

}