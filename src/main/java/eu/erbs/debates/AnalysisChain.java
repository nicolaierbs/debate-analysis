package eu.erbs.debates;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UIMAException;

import eu.erbs.debates.analysis.DebateAnalysator;
import eu.erbs.debates.analysis.DiversityAnalysator;
import eu.erbs.debates.analysis.IsolatedMostFrequentWordAnalysator;
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

public class AnalysisChain {

	public enum Politician {Trump,Clinton};
	
	public static Map<String, Double> totalCountryFrequencies = new HashMap<>(); 

	private static final String[] INTERESTING_WORDS = new String[]{
			"tremendous", "wrong", "country", "great", "correct", "right", "false",
			"donald", "trump","joe","biden","obama","mike","pence","tim","kaine",
			"white", "black","supremacist","supremacists","antifa","police","riot",
			"covid","covid-19","corona","disease","virus","chinese","president"};

	private static final File stopwordFile = new File("src/main/resources/stopwords.txt");
	public static void main(String[] args) throws Exception {
		List<TalkEvent> talkEvents = new ArrayList<>();
		
//		talkEvents.addAll(DebateLoader.loadDebate("TrumpClinton1.txt"));
//		talkEvents.addAll(DebateLoader.loadDebate("TrumpClinton2.txt"));
//		talkEvents.addAll(DebateLoader.loadDebate("TrumpClinton3.txt"));
//		talkEvents.addAll(DebateLoader.loadDebate("KainePence.txt"));
		talkEvents.addAll(DebateLoader.loadDebate("TrumpBiden.txt"));


//		Filter trumpFilter = new SpeakerFilter(DebateLoader.TRUMP);
//		List<TalkEvent> trumpTalks =  trumpFilter.filter(talkEvents);
//		Filter clintonFilter = new SpeakerFilter(DebateLoader.CLINTON);
//		List<TalkEvent> clintonTalks =  clintonFilter.filter(talkEvents);
//		Filter penceFilter = new SpeakerFilter(DebateLoader.PENCE);
//		List<TalkEvent> penceTalks =  penceFilter.filter(talkEvents);
//		Filter kaineFilter = new SpeakerFilter(DebateLoader.KAINE);
//		List<TalkEvent> kaineTalks =  kaineFilter.filter(talkEvents);
		Filter trump2Filter = new SpeakerFilter(DebateLoader.TRUMP2);
		List<TalkEvent> trump2Talks =  trump2Filter.filter(talkEvents);
		Filter bidenFilter = new SpeakerFilter(DebateLoader.BIDEN);
		List<TalkEvent> bidenTalks =  bidenFilter.filter(talkEvents);

		//		createWordClounds(trumpTalks, clintonTalks);

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
//			System.out.print("Trump:\t\t");
//			System.out.println(analysator.analyse(trumpTalks));
//			System.out.print("Clinton:\t");
//			System.out.println(analysator.analyse(clintonTalks));
//			System.out.print("Pence:\t\t");
//			System.out.println(analysator.analyse(penceTalks));
//			System.out.print("Kaine:\t\t");
//			System.out.println(analysator.analyse(kaineTalks));
			System.out.print("Trump2:\t\t");
			System.out.println(analysator.analyse(trump2Talks));
			System.out.print("Biden:\t\t");
			System.out.println(analysator.analyse(bidenTalks));
			
		}
		
		
//		createWordClouds(trumpEntities, clintonEntities);

	}

	private static void exportCountryFrequencies(Map<String,Double> countryFrequencies) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("output/locations/" + new Date().getTime() + ".csv")));
		writer.write("Country,Frequency");
		for(Entry<String, Double> pair : countryFrequencies.entrySet()){
			writer.newLine();
			writer.write(pair.getKey() + "," + Math.round(pair.getValue()));
		}
		writer.close();
	}

	private static void createWordClouds(List<String> trumpWords, List<String> clintonWords) throws IOException {
		trumpWords = removeNull(trumpWords);
		clintonWords = removeNull(clintonWords);
		WordCloudGenerator.createWordCloud(trumpWords, Politician.Trump);
		WordCloudGenerator.createWordCloud(clintonWords, Politician.Clinton);
		WordCloudGenerator.createDifferenceWordCloud(trumpWords, clintonWords);
	}

	private static List<String> removeNull(List<String> words) {
		List<String> filtered = new ArrayList<>();
		for(String word : words){
			if(word != null && word.length() > 2){
				filtered.add(word);
			}
		}
		return filtered;
	}

	private static List<String> getWords(List<TalkEvent> talkEvents) throws UIMAException {
		List<String> words = new ArrayList<>();
		for(TalkEvent talkEvent : talkEvents){
			words.addAll(DKProUtils.tokenize(talkEvent.getText().trim()));
		}
		return words;
	}
}