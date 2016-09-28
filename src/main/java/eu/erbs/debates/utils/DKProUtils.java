package eu.erbs.debates.utils;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.readability.ReadabilityAnnotator;
import de.tudarmstadt.ukp.dkpro.core.type.ReadabilityScore;

public class DKProUtils {

	private static AnalysisEngine tokenizer;
	private static AnalysisEngine tagger;
	private static AnalysisEngine lemmatizer;
	private static AnalysisEngine readability;
	
	public static List<String> tokenize(String text) throws UIMAException{
		if (tokenizer == null)
		{
			tokenizer = createEngine(OpenNlpSegmenter.class);
		}
		JCas jCas = JCasFactory.createJCas();
		jCas.setDocumentLanguage("en");
		jCas.setDocumentText(text);

		SimplePipeline.runPipeline(jCas, tokenizer);

		List<String> words = new ArrayList<>();
		for(Token token : JCasUtil.select(jCas, Token.class)){
			words.add(token.getCoveredText());
		}
		return words;
		
	}

	public static Map<String, Double> getLengthStatistics(String text) throws UIMAException
	{

		if (tokenizer == null)
		{
			tokenizer = createEngine(OpenNlpSegmenter.class);
		}
		if (tagger == null)
		{
			tagger = createEngine(OpenNlpPosTagger.class);
		}
		if (lemmatizer == null)
		{
			lemmatizer = createEngine(LanguageToolLemmatizer.class);
		}

		JCas jCas = JCasFactory.createJCas();
		jCas.setDocumentLanguage("en");
		jCas.setDocumentText(text);

		SimplePipeline.runPipeline(jCas, tokenizer, tagger, lemmatizer);

		Map<String, Double> length = new HashMap<String, Double>();
		int numberOfTokens = JCasUtil.select(jCas, Token.class).size();
		int numberOfSentences = JCasUtil.select(jCas, Sentence.class).size();
		Set<String> lemmas = new HashSet<String>();
		int numberOfCharaters = 0;
		for (Token token : JCasUtil.select(jCas, Token.class))
		{
			numberOfCharaters += token.getCoveredText().length();
			lemmas.add(token.getLemma().getValue());
		}
		int numberOfSetOfWords = lemmas.size();

		length.put("tokens", (double) numberOfTokens);
		length.put("avg. word length", (numberOfCharaters / (double) numberOfTokens));
		length.put("avg. sentence length", (numberOfTokens / (double) numberOfSentences));
		length.put("lexical diversity", (numberOfTokens / (double) numberOfSetOfWords));

		return length;

	}

	public static Map<String,Double> getReadabaility(String text) throws UIMAException
	{

		if(tokenizer == null) {
			tokenizer = createEngine(OpenNlpSegmenter.class);
		}
		if(tagger == null) {
			tagger = createEngine(OpenNlpPosTagger.class);
		}
		if (readability == null)
		{
			readability = createEngine(ReadabilityAnnotator.class);
		}

		JCas jCas = JCasFactory.createJCas();
		jCas.setDocumentLanguage("en");
		jCas.setDocumentText(text);

		SimplePipeline.runPipeline(jCas, tokenizer, tagger, lemmatizer, readability);

		Map<String,Double> readability = new HashMap<String,Double>();
		for (ReadabilityScore score : JCasUtil.select(jCas, ReadabilityScore.class))
		{
			readability.put(score.getMeasureName(), score.getScore());
		}
		return readability;

	}

}