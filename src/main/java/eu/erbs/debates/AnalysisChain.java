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

	private static final String[] INTERESTING_WORDS = new String[]{
			"tremendous", "wrong", "country", "great", "correct", "right", "false",
			"donald", "trump","hillary","secretary","clinton","mike","pence","tim","kaine"};

	private static final File stopwordFile = new File("src/main/resources/stopwords.txt");
	public static void main(String[] args) throws Exception {
		List<TalkEvent> talkEvents = new ArrayList<>();
		
		talkEvents.addAll(DebateLoader.loadDebate("TrumpClinton1.txt"));
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
			System.out.print("Pence:\t\t");
			System.out.println(analysator.analyse(penceTalks));
			System.out.print("Kaine:\t");
			System.out.println(analysator.analyse(kaineTalks));
		}

		ambiverseAnalytics(trumpTalks, clintonTalks);
		ambiverseAnalytics(penceTalks, kaineTalks);

	}

	private static void ambiverseAnalytics(List<TalkEvent> trumpTalks, List<TalkEvent> clintonTalks)
			throws IOException, InterruptedException, ClassNotFoundException, FileNotFoundException {

		List<String> trumpEntities = getEntities(trumpTalks);
		System.out.println("Loaded " + trumpEntities.size() + " entities for Trump");
		List<String> clintonEntities = getEntities(clintonTalks);
		System.out.println("Loaded " + clintonEntities.size() + " entities for Clinton");
		AmbiverseConnector.serialize();

		List<String> trumpCategories = getCategories(trumpEntities);
		System.out.println("Loaded " + trumpCategories.size() + " categories for Trump");
		List<String> clintonCategories = getCategories(clintonEntities);
		System.out.println("Loaded " + clintonCategories.size() + " categories for Clinton");
		AmbiverseConnector.serialize();

		List<String> trumpEntityNames = getNames(trumpEntities);
		System.out.println("Loaded " + trumpEntityNames.size() + " entity names for Trump");
		List<String> clintonEntityNames = getNames(clintonEntities);
		System.out.println("Loaded " + clintonEntityNames.size() + " entity names for Clinton");
		AmbiverseConnector.serialize();

		System.out.println("Trump categories:\t" + StringUtils.join(trumpCategories, ", "));
		System.out.println("Clinton categories:\t" + StringUtils.join(clintonCategories, ", "));
		//		createWordClouds(trumpEntityNames, clintonEntityNames);

		System.out.println("Loaded " + trumpCategories.size() + " categories for Trump");
		System.out.println("Loaded " + clintonCategories.size() + " categories for Clinton");
		List<String> trumpCategoryNames = getNames(trumpCategories);
		List<String> clintonCategoryNames = getNames(clintonCategories);
		AmbiverseConnector.serialize();
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