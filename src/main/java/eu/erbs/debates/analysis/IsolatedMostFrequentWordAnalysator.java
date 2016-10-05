package eu.erbs.debates.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import eu.erbs.debates.model.TalkEvent;

public class IsolatedMostFrequentWordAnalysator extends AbstractAnalysator {

	protected int topN;
	protected Set<String> stopwords;
	

	public IsolatedMostFrequentWordAnalysator(int topN) {
		this.topN = topN;
		stopwords = new HashSet<>();
	}
	
	public Map<String, Double> analyseTokens(List<String> tokens) {
		Map<String, Double> frequentWords = new HashMap<>();

		FrequencyDistribution<String> frequencies = new FrequencyDistribution<>();

		for(String token : tokens){
			if(!stopwords.contains(token)){
				frequencies.inc(token.toLowerCase());
			}
		}

		frequentWords.put("Total", (double) frequencies.getN());
		for(String sample : frequencies.getMostFrequentSamples(topN)){
			frequentWords.put(sample, (double) frequencies.getCount(sample));
		}

		return frequentWords;
	}

	@Override
	public Map<String, Double> analyse(List<TalkEvent> talkEvents) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}