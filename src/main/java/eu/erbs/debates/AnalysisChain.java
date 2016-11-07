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

import eu.erbs.debates.ambiverse.AmbiverseConnector;
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
			"donald", "trump","hillary","secretary","clinton","mike","pence","tim","kaine"};

	private static final File stopwordFile = new File("src/main/resources/stopwords.txt");
	public static void main(String[] args) throws Exception {
		List<TalkEvent> talkEvents = new ArrayList<>();
		
		talkEvents.addAll(DebateLoader.loadDebate("TrumpClinton1.txt"));
		talkEvents.addAll(DebateLoader.loadDebate("TrumpClinton2.txt"));
		talkEvents.addAll(DebateLoader.loadDebate("TrumpClinton3.txt"));
		talkEvents.addAll(DebateLoader.loadDebate("KainePence.txt"));


		Filter trumpFilter = new SpeakerFilter(DebateLoader.TRUMP);
		List<TalkEvent> trumpTalks =  trumpFilter.filter(talkEvents);
		Filter clintonFilter = new SpeakerFilter(DebateLoader.CLINTON);
		List<TalkEvent> clintonTalks =  clintonFilter.filter(talkEvents);
		Filter penceFilter = new SpeakerFilter(DebateLoader.PENCE);
		List<TalkEvent> penceTalks =  penceFilter.filter(talkEvents);
		Filter kaineFilter = new SpeakerFilter(DebateLoader.KAINE);
		List<TalkEvent> kaineTalks =  kaineFilter.filter(talkEvents);

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
			System.out.print("Trump:\t\t");
			System.out.println(analysator.analyse(trumpTalks));
			System.out.print("Clinton:\t");
			System.out.println(analysator.analyse(clintonTalks));
//			System.out.print("Pence:\t\t");
//			System.out.println(analysator.analyse(penceTalks));
//			System.out.print("Kaine:\t\t");
//			System.out.println(analysator.analyse(kaineTalks));
		}
		
		
		System.out.println();
		System.out.println("--- Trump (Ambiverse) ---");
		List<String> trumpEntities = ambiverseAnalytics(trumpTalks);
		System.out.println();
		System.out.println("--- Clinton (Ambiverse) ---");
		List<String> clintonEntities = ambiverseAnalytics(clintonTalks);
		System.out.println();
		System.out.println("--- Pence (Ambiverse) ---");
		ambiverseAnalytics(penceTalks);
		System.out.println();
		System.out.println("--- Kaine (Ambiverse) ---");
		ambiverseAnalytics(kaineTalks);
		
		createWordClouds(trumpEntities, clintonEntities);

	}

	private static List<String> ambiverseAnalytics(List<TalkEvent> talks)
			throws IOException, InterruptedException, ClassNotFoundException, FileNotFoundException {

		List<String> entities = getEntities(talks);
		return entities;
//		List<String> categories = getCategories(entities);
//		List<String> entityNames = getNames(entities);
//		List<String> categoryNames = getNames(categories);
//		AmbiverseConnector.serialize();
//		
//		System.out.println("Loaded " + entities.size() + " entities, " + entityNames.size() + " entity names, " + categories.size() + " categories, and" + categoryNames.size() + " category names." );
//			
//		IsolatedMostFrequentWordAnalysator mostFrequentWords = new IsolatedMostFrequentWordAnalysator(10);
//		Map<String, Double> countryFrequencies = mostFrequentWords.analyseTokens(entityNames);
//		for(Entry<String,Double> entry : countryFrequencies.entrySet()){
//			if(totalCountryFrequencies.keySet().contains(entry.getKey())){
//				totalCountryFrequencies.put(entry.getKey(), totalCountryFrequencies.get(entry.getKey()) + entry.getValue());
//			}
//			else{
//				totalCountryFrequencies.put(entry.getKey(), entry.getValue());
//			}
//		}
////		exportCountryFrequencies(totalCountryFrequencies);
//		
//		System.out.print("Most frequent entities:\t");
//		System.out.println(mostFrequentWords.analyseTokens(entityNames));
////		System.out.print("Most frequent categories:\t");
////		System.out.println(mostFrequentWords.analyseTokens(categories));
//		
//		//		createWordClouds(trumpEntityNames, clintonEntityNames);
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

	private static List<String> getNames(List<String> entities) throws IOException, InterruptedException, ClassNotFoundException {

		List<String> entityNames = new ArrayList<>();
		String name;
		for(String entityId : entities){
			name = AmbiverseConnector.getName(entityId);
			if(name !=  null){
				entityNames.add(name);
			}
		}
		return entityNames;
	}
	
	private static List<String> getEntities(List<TalkEvent> talkEvents) throws IOException, InterruptedException, ClassNotFoundException {
		List<String> entities = new ArrayList<>();
		for(TalkEvent talkEvent : talkEvents){
			entities.addAll(AmbiverseConnector.getEntities(talkEvent.getText()));
		}
		return entities;
	}

	private static List<String> getCategories(List<String> entities) throws IOException, InterruptedException, ClassNotFoundException {
		List<String> categories = null;
		categories = new ArrayList<>();
		for(String entityId :  entities){
			categories.addAll(AmbiverseConnector.getCategories(entityId));
		}

		return categories;
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