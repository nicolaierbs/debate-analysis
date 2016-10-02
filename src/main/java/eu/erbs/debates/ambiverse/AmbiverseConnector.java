package eu.erbs.debates.ambiverse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ambiverse.api.AmbiverseApiClient;
import com.ambiverse.api.model.AnalyzeInput;
import com.ambiverse.api.model.AnalyzeOutput;
import com.ambiverse.api.model.Entities;
import com.ambiverse.api.model.Entity;
import com.ambiverse.api.model.Match;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

public class AmbiverseConnector {

	private static AmbiverseApiClient client = null;

	private static Map<String,List<String>> categoryMapper;

	private static Map<String,String> nameMapper;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport = new NetHttpTransport();


	private static void initialize() throws IOException{
		Credential credential = AmbiverseApiClient.authorize(httpTransport, JSON_FACTORY);

		// Instantiate a new API client
		client = new AmbiverseApiClient(httpTransport, JSON_FACTORY, credential);
	}

	public static List<String> getEntities(String text) throws IOException{

		if(client == null){
			initialize();
		}

		List<String> entities = new ArrayList<>();

		AnalyzeInput input = new AnalyzeInput()
				.withLanguage("en")		// Optional. If not set, language detection happens automatically.
				.withText(text);

		AnalyzeOutput output = client.entityLinking().analyze().process(input).execute();


		for (Match match : output.getMatches()) {
			entities.add(match.getEntity().getId());
		}
		System.out.println("Extracted " + StringUtils.join(entities, ", ") + " from:\t" + text);
		return entities;
	}

	public static List<String> getCategories(String entityId) throws IOException, InterruptedException{

		if(client == null){
			initialize();
		}

		if(categoryMapper == null){
			categoryMapper = new HashMap<>();
		}

		if(categoryMapper.containsKey(entityId)){
			return categoryMapper.get(entityId);
		}
		else{
			List<String> categories = new ArrayList<>();
			Entities entities = client.knowledgeGraph().entities()
					.get(entityId)
					.execute();

			for (Entity entity : entities.getEntities()) {
				categories.addAll(entity.getCategories());
			}
			categoryMapper.put(entityId, categories);
			//Don't go to hard on Ambiverse API...
			Thread.sleep(10000);
			return categories;
		}
	}
	
	public static String getName(String entityId) throws IOException, InterruptedException{

		if(client == null){
			initialize();
		}

		if(nameMapper == null){
			nameMapper = new HashMap<>();
		}

		if(nameMapper.containsKey(entityId)){
			return nameMapper.get(entityId);
		}
		else{
			Entities entities = client.knowledgeGraph().entities()
					.get(entityId)
					.execute();
			
			if(entities.getEntities().size() != 1){
				System.out.println("Critical: Found " + entities.getEntities().size() + " entities for id " + entityId);
			}

			for (Entity entity : entities.getEntities()) {
				nameMapper.put(entityId, entity.getName());
				//Don't go to hard on Ambiverse API...
				Thread.sleep(10000);
				return entity.getName();
			}
		}
		return null;
	}
}