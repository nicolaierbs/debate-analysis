package eu.erbs.debates.analysis;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;

import eu.erbs.debates.model.TalkEvent;
import eu.erbs.debates.utils.DKProUtils;

public class MostFrequentWordsAnalysator extends IsolatedMostFrequentWordAnalysator {

	public MostFrequentWordsAnalysator(int topN, File stopwordFile) throws IOException {
		super(topN);
		stopwords.addAll(FileUtils.readLines(stopwordFile, Charset.defaultCharset()));
	}

	@Override
	public  Map<String, Double> analyse(List<TalkEvent> talkEvents) throws UIMAException {

		List<String> tokens = new ArrayList<>();
		for(TalkEvent talkEvent : talkEvents){
			tokens.addAll(DKProUtils.tokenize(talkEvent.getText()));
		}
		return analyseTokens(tokens);

	}

}