package eu.erbs.debates.io;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import eu.erbs.debates.model.TalkEvent;

public class DebateLoader { 

	public final static String TRUMP = "TRUMP: ";
	public final static String TRUMP2 = "TRUMP2: ";
	public final static String BIDEN = "BIDEN: ";
	public final static String WALLACE = "WALLACE";
	public final static String CLINTON = "CLINTON: ";
	public final static String HOLT = "HOLT: ";
	public final static String QUIJANO = "QUIJANO: ";
	public final static String KAINE = "KAINE: ";
	public final static String PENCE = "PENCE: ";

	private final static File DIRECTORY =  new File("src/main/resources");
	
	public static void main(String[] args) throws IOException{
		loadDebate("TrumpBiden.txt");

	}

	public static List<TalkEvent> loadDebate(String debateFileName) throws IOException{
		
		return loadDebate(new File(DIRECTORY, debateFileName));
	}
		public static List<TalkEvent> loadDebate(File debateFile) throws IOException{
		String content = FileUtils.readFileToString(debateFile,  Charset.defaultCharset());

		//Remove applause and other annotations
		content = content.replaceAll("\\(.*\\)", "");
		String patternString = "((" + TRUMP + "|" +  CLINTON + "|" +  TRUMP2 + "|" +  BIDEN + "|" +  WALLACE + "|" +  HOLT + "|" +  PENCE + "|" +  QUIJANO + "|" +  KAINE + ")(.*))";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(content);

		List<TalkEvent> talkEvents = new ArrayList<>();
		TalkEvent talkEvent;

		while(matcher.find()) {
			talkEvent = new TalkEvent();
			talkEvent.setSpeaker(matcher.group(2));
			talkEvent.setText(matcher.group(3));
			talkEvents.add(talkEvent);
		}

		System.out.println("Loaded " + talkEvents.size() + " talk events");
		return talkEvents;
	}
}
