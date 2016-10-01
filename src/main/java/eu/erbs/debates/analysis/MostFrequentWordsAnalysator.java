package eu.erbs.debates.analysis;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import eu.erbs.debates.model.TalkEvent;
import eu.erbs.debates.utils.DKProUtils;

public class MostFrequentWordsAnalysator extends AbstractAnalysator {
	
	private int topN;
	private Set<String> stopwords;
	
	public MostFrequentWordsAnalysator(int topN, File stopwordFile) throws IOException {
		this.topN = topN;
		stopwords = new HashSet<>();
		stopwords.addAll(FileUtils.readLines(stopwordFile, Charset.defaultCharset()));
	}

	@Override
	public  Map<String, Double> analyse(List<TalkEvent> talkEvents) throws UIMAException {
		Map<String, Double> frequentWords = new HashMap<>();
		
		FrequencyDistribution<String> frequencies = new FrequencyDistribution<>();

		for(TalkEvent talkEvent : talkEvents){
			for(String token : DKProUtils.tokenize(talkEvent.getText())){
				if(!stopwords.contains(token)){
					frequencies.inc(token.toLowerCase());
				}
			}
		}
		
		frequentWords.put("Total", (double) frequencies.getN());
		for(String sample : frequencies.getMostFrequentSamples(topN)){
			frequentWords.put(sample, (double) frequencies.getCount(sample));
		}

		return frequentWords;
	}

}