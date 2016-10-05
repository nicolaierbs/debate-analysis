package eu.erbs.debates.ambiverse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

	private static final File SERIALIZED_DIRECTORY = new File("output/serialized");
	private static final File ENTITY_MAPPER_FILE = new File(SERIALIZED_DIRECTORY, "entitymapper.ser");
	private static final File CATEGORY_MAPPER_FILE = new File(SERIALIZED_DIRECTORY, "categorymapper.ser");
	private static final File NAME_MAPPER_FILE = new File(SERIALIZED_DIRECTORY, "namemapper.ser");

	private static Map<String,List<String>> entityMapper;
	private static Map<String,List<String>> categoryMapper;
	private static Map<String,String> nameMapper;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport = new NetHttpTransport();


	@SuppressWarnings("unchecked")
	private static void initialize() throws IOException, ClassNotFoundException{
		Credential credential = AmbiverseApiClient.authorize(httpTransport, JSON_FACTORY);

		// Instantiate a new API client
		client = new AmbiverseApiClient(httpTransport, JSON_FACTORY, credential);

		if(ENTITY_MAPPER_FILE.exists()){
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(ENTITY_MAPPER_FILE));
			entityMapper = (HashMap<String,List<String>>) in.readObject();
			in.close();
		}
		else{
			entityMapper = new HashMap<>();
		}
		
		if(CATEGORY_MAPPER_FILE.exists()){
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(CATEGORY_MAPPER_FILE));
			categoryMapper = (HashMap<String,List<String>>) in.readObject();
			in.close();
		}
		else{
			categoryMapper = new HashMap<>();
		}
		
		if(NAME_MAPPER_FILE.exists()){
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(NAME_MAPPER_FILE));
			nameMapper = (HashMap<String,String>) in.readObject();
			in.close();
		}
		else{
			nameMapper = new HashMap<>();
		}
	}
	
	public static void serialize() throws FileNotFoundException, IOException{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ENTITY_MAPPER_FILE));
		out.writeObject(entityMapper);
		out.close();

		out = new ObjectOutputStream(new FileOutputStream(CATEGORY_MAPPER_FILE));
		out.writeObject(categoryMapper);
		out.close();

		out = new ObjectOutputStream(new FileOutputStream(NAME_MAPPER_FILE));
		out.writeObject(nameMapper);
		out.close();
}

	public static List<String> getEntities(String text) throws IOException, InterruptedException, ClassNotFoundException{

		if(client == null){
			initialize();
		}

		if(entityMapper.containsKey(text)){
			return entityMapper.get(text);
		}
		else{

			List<String> entities = new ArrayList<>();

			AnalyzeInput input = new AnalyzeInput()
					.withLanguage("en")		// Optional. If not set, language detection happens automatically.
					.withText(text);

			AnalyzeOutput output = client.entityLinking().analyze().process(input).execute();

			//Don't go to hard on Ambiverse API...
			Thread.sleep(10000);


			for (Match match : output.getMatches()) {
				entities.add(match.getEntity().getId());
			}
			System.out.println("Extracted " + StringUtils.join(entities, ", ") + " from:\t" + text);
			entityMapper.put(text, entities);
			return entities;
		}
	}

	public static List<String> getCategories(String entityId) throws IOException, InterruptedException, ClassNotFoundException{

		if(client == null){
			initialize();
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
			System.out.println("Extracted " + StringUtils.join(categories, ", ") + " from:\t" + entityId);
			categoryMapper.put(entityId, categories);
			//Don't go to hard on Ambiverse API...
			Thread.sleep(10000);
			return categories;
		}
	}

	public static String getName(String entityId) throws IOException, InterruptedException, ClassNotFoundException{

		if(client == null){
			initialize();
		}

		if(nameMapper.containsKey(entityId)){
			return nameMapper.get(entityId);
		}
		else{
			Entities entities = client.knowledgeGraph().entities()
					.get(entityId)
					.execute();

			for (Entity entity : entities.getEntities()) {
				nameMapper.put(entityId, entity.getName());
				//Don't go to hard on Ambiverse API...
				System.out.println("Searching ambiverse for name of " + entityId);
				Thread.sleep(100);
				return entity.getName();
			}
		}
		return null;
	}
}