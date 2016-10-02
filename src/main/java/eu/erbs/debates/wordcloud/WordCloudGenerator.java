package eu.erbs.debates.wordcloud;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.PolarBlendMode;
import com.kennycason.kumo.PolarWordCloud;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import com.kennycason.kumo.palette.LinearGradientColorPalette;

import eu.erbs.debates.AnalysisChain.Politician;

public class WordCloudGenerator {

	private static File CLINTON_BACKGROUND = new File("src/main/resources/background/clinton.gif");
	private static File TRUMP_BACKGROUND = new File("src/main/resources/background/trump.png");

	public static void createDifferenceWordCloud(List<String> words1, List<String> words2)
			throws IOException
	{
		final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		frequencyAnalyzer.setWordFrequenciesToReturn(750);
		frequencyAnalyzer.setMinWordLength(4);
		// frequencyAnalyzer.setStopWords(loadStopWords());
		frequencyAnalyzer.setStopWords(FileUtils.readLines(new File("src/main/resources/stopwords.txt"), Charset.defaultCharset()));

		final List<WordFrequency> wordFrequencies1 = frequencyAnalyzer.load(words1);
		final List<WordFrequency> wordFrequencies2 = frequencyAnalyzer.load(words2);
		final Dimension dimension = new Dimension(600, 600);
		final PolarWordCloud wordCloud = new PolarWordCloud(dimension, CollisionMode.PIXEL_PERFECT, PolarBlendMode.BLUR);
		wordCloud.setPadding(2);
		wordCloud.setBackground(new CircleBackground(300));
		wordCloud.setBackgroundColor(Color.WHITE);
		wordCloud.setFontScalar(new SqrtFontScalar(10, 40));
		ColorPalette democrats = new ColorPalette(new Color(0,0,255),new Color(20,50,205),new Color(10,50,235),new Color(50,10,225));
		wordCloud.setColorPalette(democrats);
		wordCloud.build(wordFrequencies2, wordFrequencies1);
		wordCloud.writeToFile("output/wordcloud/difference.png");
	}

	public static void createWordCloud(List<String> words, Politician politician) throws IOException
	{
		final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
		// frequencyAnalyzer.setStopWords(FileUtils.readLines(new
		// File("src/main/resources/stopwords.txt")));
		frequencyAnalyzer.setWordFrequenciesToReturn(400);

		final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(words);
		final Dimension dimension = new Dimension(550, 550);
		final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.RECTANGLE);
		wordCloud.setPadding(0);
		
		switch (politician) {
		case Clinton:
			wordCloud.setBackground(new PixelBoundryBackground(CLINTON_BACKGROUND));
			break;
		case Trump:
			wordCloud.setBackground(new PixelBoundryBackground(TRUMP_BACKGROUND));
			break;
		}

		// wordCloud.setBackground(new RectangleBackground(dimension));
		wordCloud.setColorPalette(new LinearGradientColorPalette(Color.red, Color.blue, 8));
		wordCloud.setFontScalar(new LinearFontScalar(8, 40));
		wordCloud.setBackgroundColor(Color.white);

		wordCloud.build(wordFrequencies);
		wordCloud.writeToFile("output/wordcloud/" + politician.name() + ".png");
	}

}
