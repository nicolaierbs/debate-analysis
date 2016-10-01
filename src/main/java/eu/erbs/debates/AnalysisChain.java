package eu.erbs.debates;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;

import eu.erbs.debates.analysis.DebateAnalysator;
import eu.erbs.debates.analysis.DiversityAnalysator;
import eu.erbs.debates.analysis.LengthAnalysator;
import eu.erbs.debates.analysis.MostFrequentWordsAnalysator;
import eu.erbs.debates.analysis.ReadabilityAnalysator;
import eu.erbs.debates.analysis.WordCountAnalysator;
import eu.erbs.debates.filter.Filter;
import eu.erbs.debates.filter.SpeakerFilter;
import eu.erbs.debates.io.DebateLoader;
import eu.erbs.debates.model.TalkEvent;
import eu.erbs.debates.utils.DKProUtils;
import eu.erbs.debates.wordcloud.WordCloudGenerator;
import eu.erbs.debates.wordcloud.WordCloudGenerator.Politician;

public class AnalysisChain {
	
	private static final String[] INTERESTING_WORDS = new String[]{"tremendous", "wrong", "country", "correct", "right", "donald", "trump","hillary","secretary","clinton"};

	private static final File stopwordFile = new File("src/main/resources/stopwords.txt");
	public static void main(String[] args) throws Exception {
		List<TalkEvent> talkEvents = DebateLoader.loadDebate("TrumpClinton1.txt");
		
	
		Filter trumpFilter = new SpeakerFilter("TRUMP: ");
		List<TalkEvent> trumpTalks =  trumpFilter.filter(talkEvents);
		Filter clintonFilter = new SpeakerFilter("CLINTON: ");
		List<TalkEvent> clintonTalks =  clintonFilter.filter(talkEvents);
		
		//Create WordClouds
//		List<String> trumpWords = getWords(trumpTalks);
//		List<String> clintonWords = getWords(clintonTalks);
//		WordCloudGenerator.createWordCloud(trumpWords, Politician.Trump);
//		WordCloudGenerator.createWordCloud(clintonWords, Politician.Clinton);
//		WordCloudGenerator.createDifferenceWordCloud(trumpWords, clintonWords);

		
		List<DebateAnalysator> analysators = new ArrayList<>();
		analysators.add(new LengthAnalysator());
		analysators.add(new DiversityAnalysator());
		analysators.add(new ReadabilityAnalysator());
		analysators.add(new WordCountAnalysator(INTERESTING_WORDS));
		analysators.add(new MostFrequentWordsAnalysator(15,stopwordFile));
		
		
		for(DebateAnalysator analysator : analysators){
			System.out.println();
			System.out.println(analysator.getName());
//			System.out.print("All:\t\t");
//			System.out.println(analysator.analyse(talkEvents));
			System.out.print("Trump:\t\t");
			System.out.println(analysator.analyse(trumpTalks));
			System.out.print("Clinton:\t");
			System.out.println(analysator.analyse(clintonTalks));
		}
		
	}

	private static List<String> getWords(List<TalkEvent> talkEvents) throws UIMAException {
		List<String> words = new ArrayList<>();
		for(TalkEvent talkEvent : talkEvents){
			words.addAll(DKProUtils.tokenize(talkEvent.getText()));
		}
		return words;
	}
}