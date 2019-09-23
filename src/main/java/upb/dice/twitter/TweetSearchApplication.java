package upb.dice.twitter;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.FileWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TweetSearchApplication {

	ConfigurationBuilder configBuilder = new ConfigurationBuilder();
	Twitter twitter;
	
	//Authentication
	TweetSearchApplication(){
		configBuilder.setDebugEnabled(true)
		.setOAuthConsumerKey("iCv6sCoYcQtM3EgxbPrz3zj8p")
		.setOAuthConsumerSecret("RXkay6HURXFPRJPA60Z3ZBHk775qIf6aSHjPCErAVSUkTXBGg8")
		.setOAuthAccessToken("1174976100880896000-2XLLvld7N051jWYUpHcJdvgNWTEng1")
		.setOAuthAccessTokenSecret("ZloZQ4u0r3ddyts5lGzq28QlO5GRoUq3hUoSFYwsB43j8");
		twitter = new TwitterFactory(configBuilder.build()).getInstance();
	}


	public void getTweets(String key) throws TwitterException, IOException {
		System.out.println("Starting Tweets with keyword "+key);
		//Query Build
		Query query = new Query(key);
		query.query(key);
		query.getUntil();
		
		//Latitude and Longitude of Berlin and 100 KM Radius around
		query.geoCode(new GeoLocation(52.513212, 13.379758), 100,Query.KILOMETERS);
		
		QueryResult queryRes;
		FileWriter writer = new FileWriter(key+".txt");
		do {			 
			queryRes = twitter.search(query);
			List<Status> queryTweets = queryRes.getTweets();				
			for(Status t : queryTweets) {
				writer.append(t.getText()+System.lineSeparator());
			}
		}while((query = queryRes.nextQuery()) != null);
		writer.close();	
		System.out.println("End Tweets with keyword "+key);

	}

	public static void main(String[] args) throws TwitterException, IOException {
		//List of Keywords
		List<String> keyWords = new ArrayList<>(Arrays.asList("Cassini", "Huygens"));
		TweetSearchApplication search = new TweetSearchApplication();
		for(String i: keyWords)
			search.getTweets(i);		
	}
}



