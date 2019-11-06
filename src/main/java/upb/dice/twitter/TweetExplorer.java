package upb.dice.twitter;

import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.Query;
import twitter4j.TwitterException;

import java.io.IOException;

/**
 * A query is built based on the keyword or location provided. Tweets related to this query are extracted
 */
public class TweetExplorer {

    private MaxIDChecker maxIDChecker = new MaxIDChecker();
    private TweetExtractor tweetExtractor = new TweetExtractor();

    /**
     * This method builds a query based on the keyword and extracts the tweets containing this keyword
     * @param key for which the query is built and the tweets containing this key are searched and extracted
     */
    public void keywordQuery(String key) throws IOException, TwitterException, InterruptedException {
        long key_id_1 = maxIDChecker.keyParse(key);
        Query query1 = new Query(key);
        if (key_id_1 != 1) {
            query1.sinceId(key_id_1);
        }
        tweetExtractor.getTweet(query1);
    }

    /**
     * This method builds the query based on the location provided and extracts the tweets from the respective location
     * @param latitude of the location
     * @param longitude of the location
     * @param radius coverage from the location point
     */
    public void locationQuery(double latitude, double longitude, double radius) throws IOException, TwitterException, InterruptedException {
        GeoQuery geoQuery = new GeoQuery(new GeoLocation(latitude, longitude));
        Query query = new Query();
        query.setGeoCode(geoQuery.getLocation(), radius, Query.KILOMETERS);
        long key_id_1 = maxIDChecker.locationParse(new GeoLocation(latitude, longitude));
        if (key_id_1 != 1) {
            query.sinceId(key_id_1);
        }
        tweetExtractor.getTweet(query);
    }

}
