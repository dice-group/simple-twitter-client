package upb.dice.twitter;

import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.Query;

import java.io.IOException;

    class SearchTweets {

    private MaxIDCheck maxIDCheck = new MaxIDCheck();
    private TweetExtraction tweetExtraction = new TweetExtraction();

    void keywordQuery(String key) throws IOException {
        long key_id_1 = maxIDCheck.keyParse(key);
        Query query1 = new Query(key);
        if (key_id_1 != 1) {
            query1.sinceId(key_id_1);
        }
        tweetExtraction.getTweet(query1);
    }

    void locationQuery(double latitude, double longitude, double radius) throws IOException {
        GeoQuery geoQuery = new GeoQuery(new GeoLocation(latitude, longitude));
        Query query = new Query();
        query.setGeoCode(geoQuery.getLocation(), radius, Query.KILOMETERS);
        long key_id_1 = maxIDCheck.locationParse(new GeoLocation(latitude, longitude));
        if (key_id_1 != 1) {
            query.sinceId(key_id_1);
        }
        tweetExtraction.getTweet(query);
    }

}
