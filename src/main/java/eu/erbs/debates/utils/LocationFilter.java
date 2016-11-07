package eu.erbs.debates.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class LocationFilter {

	public static void main(String[] args) throws IOException {
		File locationsFile = new File("output/locations/1478536278793.csv");
		List<String> locations = new ArrayList<>();
		locations.add("Country,Frequency");

		for(String line : FileUtils.readLines(locationsFile, Charset.defaultCharset())){
			if(line.equals("Country,Frequency")){
				continue;
			}
			try{
				if(Integer.valueOf(line.split(",")[1]) <= 266){
					locations.add(line);
				}
			}
			catch(Exception e){
				System.out.println(line);
			}
		}
		FileUtils.writeLines(new File(locationsFile.getName() + ".filtered"), locations, false);

	}

}
