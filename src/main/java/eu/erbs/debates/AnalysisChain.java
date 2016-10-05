package eu.erbs.debates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UIMAException;

import eu.erbs.debates.ambiverse.AmbiverseConnector;
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

public class AnalysisChain {

	public enum Politician {Trump,Clinton};

	private static final String[] INTERESTING_WORDS = new String[]{"tremendous", "wrong", "country", "great", "correct", "right", "donald", "trump","hillary","secretary","clinton"};

	private static final File stopwordFile = new File("src/main/resources/stopwords.txt");
	public static void main(String[] args) throws Exception {
		List<TalkEvent> talkEvents = new ArrayList<>();
		
		talkEvents.addAll(DebateLoader.loadDebate("TrumpClinton1.txt"));
		talkEvents.addAll(DebateLoader.loadDebate("KainePence.txt"));


		Filter trumpFilter = new SpeakerFilter("TRUMP: ");
		List<TalkEvent> trumpTalks =  trumpFilter.filter(talkEvents);
		Filter clintonFilter = new SpeakerFilter("CLINTON: ");
		List<TalkEvent> clintonTalks =  clintonFilter.filter(talkEvents);


		//		createWordClounds(trumpTalks, clintonTalks);

		List<DebateAnalysator> analysators = new ArrayList<>();
		//		analysators.add(new LengthAnalysator());
		//		analysators.add(new DiversityAnalysator());
		//		analysators.add(new ReadabilityAnalysator());
		//		analysators.add(new WordCountAnalysator(INTERESTING_WORDS));
		//		analysators.add(new MostFrequentWordsAnalysator(15,stopwordFile));


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

		ambiverseAnalytics(trumpTalks, clintonTalks);


	}

	private static void ambiverseAnalytics(List<TalkEvent> trumpTalks, List<TalkEvent> clintonTalks)
			throws IOException, InterruptedException, ClassNotFoundException, FileNotFoundException {

		List<String> trumpEntities = getEntities(trumpTalks, Politician.Trump);
		System.out.println("Loaded " + trumpEntities.size() + " entities for Trump");
		List<String> clintonEntities = getEntities(clintonTalks, Politician.Clinton);
		System.out.println("Loaded " + clintonEntities.size() + " entities for Clinton");
		AmbiverseConnector.serialize();

		List<String> trumpCategories = getCategories(trumpEntities, Politician.Trump);
		System.out.println("Loaded " + trumpCategories.size() + " categories for Trump");
		List<String> clintonCategories = getCategories(clintonEntities, Politician.Clinton);
		System.out.println("Loaded " + clintonCategories.size() + " categories for Clinton");
		AmbiverseConnector.serialize();

		List<String> trumpEntityNames = getEntityNames(trumpEntities, Politician.Trump);
		System.out.println("Loaded " + trumpEntityNames.size() + " entity names for Trump");
		List<String> clintonEntityNames = getEntityNames(clintonEntities, Politician.Clinton);
		System.out.println("Loaded " + clintonEntityNames.size() + " entity names for Clinton");
		AmbiverseConnector.serialize();

		System.out.println("Trump categories:\t" + StringUtils.join(trumpCategories, ", "));
		System.out.println("Clinton categories:\t" + StringUtils.join(clintonCategories, ", "));
		//		createWordClouds(trumpEntityNames, clintonEntityNames);

		System.out.println("Loaded " + trumpCategories.size() + " categories for Trump");
		System.out.println("Loaded " + clintonCategories.size() + " categories for Clinton");
		List<String> trumpCategoryNames = getCategoryNames(trumpCategories, Politician.Trump);
		List<String> clintonCategoryNames = getCategoryNames(clintonCategories, Politician.Clinton);
		AmbiverseConnector.serialize();
	}

	private static List<String> getEntityNames(List<String> entities, Politician politician) throws IOException, InterruptedException, ClassNotFoundException {

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

	private static List<String> getCategoryNames(List<String> entities, Politician politician) throws IOException, InterruptedException, ClassNotFoundException {
		List<String> entityNames = null;
		entityNames = new ArrayList<>();
		String name;
		for(String entityId : entities){
			name = AmbiverseConnector.getName(entityId);
			if(name !=  null){
				entityNames.add(name);
			}
		}
		return entityNames;
	}

	private static List<String> getEntities(List<TalkEvent> talkEvents, Politician politician) throws IOException, InterruptedException, ClassNotFoundException {
		List<String> entities = new ArrayList<>();
		for(TalkEvent talkEvent : talkEvents){
			entities.addAll(AmbiverseConnector.getEntities(talkEvent.getText()));
		}
		return entities;
	}

	private static List<String> getCategories(List<String> entities, Politician politician) throws IOException, InterruptedException, ClassNotFoundException {
		List<String> categories = null;
		categories = new ArrayList<>();
		for(String entityId :  entities){
			categories.addAll(AmbiverseConnector.getCategories(entityId));
		}

		return categories;
	}

	private static void createWordClouds(List<String> trumpWords, List<String> clintonWords) throws IOException {
		WordCloudGenerator.createWordCloud(trumpWords, Politician.Trump);
		WordCloudGenerator.createWordCloud(clintonWords, Politician.Clinton);
		WordCloudGenerator.createDifferenceWordCloud(trumpWords, clintonWords);
	}

	private static List<String> getWords(List<TalkEvent> talkEvents) throws UIMAException {
		List<String> words = new ArrayList<>();
		for(TalkEvent talkEvent : talkEvents){
			words.addAll(DKProUtils.tokenize(talkEvent.getText().trim()));
		}
		return words;
	}
}