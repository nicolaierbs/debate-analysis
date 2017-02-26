package eu.erbs.debates.model;

import java.util.Map;

public class Politician {

	private final static String SEP = "\t";

	private String twitterHandle;
	private String name;
	private Party party;
	private Gender gender;
	private Map<String,Double> results;

	public enum Gender {MALE,FEMALE};
	public enum Party {CDU,CSU,SPD,GREEN,LINKE,AFD,SONSTIGE};

	public Politician(){
	}

	public Politician(String input){
		String[] parts = input.split(SEP);
		twitterHandle = parts[0];
		name = parts[1];

		System.out.println(input);

		gender = Gender.valueOf(parts[2]);

		if(parts.length > 3){
			party = Party.valueOf(parts[3]);		
		}
	}

	public String toString(){
		return
				twitterHandle + SEP +
				name;
	}

	public String getTwitterHandle() {
		return twitterHandle;
	}

	public void setTwitterHandle(String twitterHandle) {
		this.twitterHandle = twitterHandle;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Map<String, Double> getResults() {
		return results;
	}

	public void setResults(Map<String, Double> results) {
		this.results = results;
	}

	public static String getSep() {
		return SEP;
	}

}
