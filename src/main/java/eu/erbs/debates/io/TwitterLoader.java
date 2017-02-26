package eu.erbs.debates.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import eu.erbs.debates.model.TalkEvent;
import eu.erbs.utils.PropertyUtils;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterLoader {
	
	private Twitter twitter;

	private static final String CONSUMER_KEY = "oauth.consumerKey";
	private static final String CONSUMER_SECRET = "oauth.consumerSecret";
	private static final String ACCESS_TOKEN = "oauth.accessToken";
	private static final String ACCESS_SECRET = "oauth.accessTokenSecret";
	private static final String TWITTER_DEBUG = "debug";

	private static final Logger log = Logger.getLogger(TwitterLoader.class.getName());

	public TwitterLoader() throws IOException, TwitterException
	{

		Properties properties = PropertyUtils.loadProperties("twitter.properties");

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb
				.setDebugEnabled(Boolean.getBoolean(properties.getProperty(TWITTER_DEBUG)))
				.setOAuthConsumerKey(properties.getProperty(CONSUMER_KEY))
				.setOAuthConsumerSecret(properties.getProperty(CONSUMER_SECRET))
				.setOAuthAccessToken(properties.getProperty(ACCESS_TOKEN))
				.setOAuthAccessTokenSecret(properties.getProperty(ACCESS_SECRET));
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();

		log.info("Established Twitter connection.");
	}
	
	public List<TalkEvent> getTweets(String twitterHandle) throws TwitterException{
		
//		Query query = new Query(twitterHandle);
//		query.setSince("2017-01-01");
//		query.setUntil("2013-07-02");
				 
		List<TalkEvent> talkEvents = new ArrayList<>();
		TalkEvent talkEvent;
		for(Status status : twitter.getUserTimeline(twitterHandle)){
			talkEvent = new TalkEvent();
			talkEvent.setSpeaker(twitterHandle);
			talkEvent.setText(status.getText());
			talkEvents.add(talkEvent);
		}
		return talkEvents;
	}


}
