package eu.erbs.debates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import eu.erbs.debates.analysis.DebateAnalysator;
import eu.erbs.debates.analysis.DiversityAnalysator;
import eu.erbs.debates.analysis.LengthAnalysator;
import eu.erbs.debates.analysis.ReadabilityAnalysator;
import eu.erbs.debates.analysis.WordCountAnalysator;
import eu.erbs.debates.io.TwitterLoader;
import eu.erbs.debates.io.utils.TwitterHandleLoader;
import eu.erbs.debates.model.Politician;
import eu.erbs.debates.model.Politician.Gender;
import eu.erbs.debates.model.TalkEvent;

public class TwitterAnalysisChain {

	public static Map<String, Double> totalCountryFrequencies = new HashMap<>(); 

	private static final String[] INTERESTING_WORDS = new String[]{
			"#","@"};

	private final static File RESULTS_FILE = new File("target/results.ser");
	private final static File POLITICIANS_FILE = new File("target/politicians.tsv");

	public static void main(String[] args) throws Exception {
		List<TalkEvent> talkEvents;

		TwitterLoader twitterLoader = new TwitterLoader();

		List<Politician> politicians = new ArrayList<>();
		if(POLITICIANS_FILE.exists()){
			for(String line : FileUtils.readLines(POLITICIANS_FILE, Charset.defaultCharset())){
				politicians.add(new Politician(line));
			}
		}
		else{
			Politician politician;
			List<String> output = new ArrayList<>();
			for(Entry<String, String> entry : TwitterHandleLoader.getBundestwitterPoliticians().entrySet()){
				politician = new Politician();
				politician.setTwitterHandle(entry.getKey());
				politician.setName(entry.getValue());
				politicians.add(politician);
				output.add(politician.toString());
			}
			FileUtils.writeLines(POLITICIANS_FILE, output);
		}

		Map<String, Map<String,Double>> results;

		List<DebateAnalysator> analysators = new ArrayList<>();
		analysators.add(new LengthAnalysator());
		analysators.add(new DiversityAnalysator());
		analysators.add(new ReadabilityAnalysator());
		analysators.add(new WordCountAnalysator(INTERESTING_WORDS));

		if(RESULTS_FILE.exists()){
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RESULTS_FILE));
			results = (HashMap) ois.readObject();
			ois.close();
		}
		else{
			results = new HashMap<>();

			for(Politician politician : politicians){
				results.put(politician.getTwitterHandle(), new HashMap<>());
				talkEvents = new ArrayList<>();
				talkEvents.addAll(twitterLoader.getTweets(politician.getTwitterHandle()));
				System.out.print(politician.getTwitterHandle() + "\t" + politician.getName() + "\t");

				for(DebateAnalysator analysator : analysators){
					System.out.print(analysator.analyse(talkEvents));
					results.get(politician.getTwitterHandle()).putAll(analysator.analyse(talkEvents));;
				}
				System.out.println();
			}

			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("results.ser"));
			oos.writeObject(results);
			oos.close();
		}

		Map<String, Map<String,Double>> genderResults = new HashMap<>();

		List<TalkEvent> maleTalkEvents = new ArrayList<>();
		List<TalkEvent> femaleTalkEvents = new ArrayList<>();

		for(Politician politician : politicians){

			if(politician.getGender().equals(Gender.MALE)){
				maleTalkEvents.addAll(twitterLoader.getTweets(politician.getTwitterHandle()));
			}
			else{
				femaleTalkEvents.addAll(twitterLoader.getTweets(politician.getTwitterHandle()));
			}
		}

		for(DebateAnalysator analysator : analysators){
			genderResults.get("male").putAll(analysator.analyse(maleTalkEvents));;
			genderResults.get("female").putAll(analysator.analyse(femaleTalkEvents));;
		}

		System.out.println(genderResults.get("male"));
		System.out.println(genderResults.get("female"));
	}
}