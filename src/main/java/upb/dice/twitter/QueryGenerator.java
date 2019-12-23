package upb.dice.twitter;

import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.Query;

import java.io.IOException;
import java.util.List;

/**
 * A query is generated based on the keyword or location provided
 */
public class QueryGenerator {

    private IDHandler idHandler = new IDHandler();

    /**
     * This method builds a query based on the keyword provided
     *
     * @param key for which the query is built and the tweets containing this key are searched and extracted
     * @return query which is built based on the keyword and the sinceID (if available)
     */
    public Query keywordQueryGen(String key) throws IOException {
        List<Long> detailesID = idHandler.keyParse(key);
        long maxID = detailesID.get(0), oldestTweetID = detailesID.get(1), sinceID = detailesID.get(2);
        Query query = new Query(key);
        if (oldestTweetID - sinceID > 0) {
            query.setSinceId(sinceID);
            query.setUntil(String.valueOf(oldestTweetID));
        } else {
            query.setSinceId(maxID);
        }
        return query;
    }

    /**
     * This method builds the query based on the location provided
     *
     * @param geoQuery geoquery required to generate a location based query
     * @param radius    coverage from the location point
     * @return query which is built based on the location attributes and the sinceID (if available)
     */
    public Query locationQueryGen(GeoQuery geoQuery, double radius) throws IOException {
        Query query = new Query();
        query.setGeoCode(geoQuery.getLocation(), radius, Query.KILOMETERS);
        List<Long> detailesID = idHandler.locationParse(new GeoLocation(geoQuery.getLocation().getLatitude(), geoQuery.getLocation().getLongitude()));
        long maxID = detailesID.get(0), oldestTweetID = detailesID.get(1), sinceID = detailesID.get(2);
        if (oldestTweetID - sinceID > 0) {
            query.setSinceId(sinceID);
            query.setUntil(String.valueOf(oldestTweetID));
        } else {
            query.setSinceId(maxID);
        }
        return query;
    }
}
