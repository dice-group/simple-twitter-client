package upb.dice.twitter;

import twitter4j.Query;
import java.io.IOException;
import java.util.List;

/**
 * Generates keyword or location based query based on the current state of the query
 */
public abstract class QueryGenerator {
    IDHandler idHandler = new IDHandler();
    Query query = new Query();

    /**
     * Generates query based on the state of the query
     * @return modified query based on the current state
     */
    public abstract Query generateQuery() throws IOException;

    /**
     * Modifies the query based on the current state
     * @param query which is generated without considering the current state
     * @return query that is modified according to the current state
     */
    public Query getModifiedQuery(Query query) throws IOException {
        List<Long> detailesID = idHandler.retrieveCurrentState(query);
        long maxID = detailesID.get(0), oldestTweetID = detailesID.get(1), sinceID = detailesID.get(2);
        if (Math.abs(oldestTweetID) - Math.abs(sinceID) > 0) {
            query.setMaxId(oldestTweetID);
        } else {
            query.setSinceId(maxID);
        }
        return query;
    }
}
