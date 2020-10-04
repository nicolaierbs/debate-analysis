package eu.erbs.debates.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Preprocessing {

	public static void main(String[] args) throws IOException {
		String content = FileUtils.readFileToString(new File("src/main/resources/raw/TrumpBiden.txt"));
		content = content.replaceAll("Chris Wallace: ", "WALLACE: ");
		content = content.replaceAll("President Donald J. Trump: ", "TRUMP2: ");
		content = content.replaceAll("Vice President Joe Biden: ", "BIDEN: ");
		content = content.replaceAll("\\((\\d{2}:)?\\d{2}:\\d{2}\\)", "");
		content = content.replaceAll("\\[crosstalk (\\d{2}:)?\\d{2}:\\d{2}\\]", "");
		content = content.replaceAll("[\\\r\\\n]+"," ");
		content = content.replaceAll("\\s\\s+", " ");
		content = content.replaceAll("WALLACE: ", "\nWALLACE: ");
		content = content.replaceAll("TRUMP2: ", "\nTRUMP2: ");
		content = content.replaceAll("BIDEN: ", "\nBIDEN: ");
		FileUtils.writeStringToFile(new File("src/main/resources/TrumpBiden.txt"), content);
		

	}

}
