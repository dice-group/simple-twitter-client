package upb.dice.twitter;

import twitter4j.Query;

import java.io.IOException;
import java.util.List;

public abstract class QueryGenerator {
    IDHandler idHandler = new IDHandler();
    Query query = new Query();

    public abstract Query generateQuery() throws IOException;

    public Query getModifiedQuery(Query query) throws IOException {
        List<Long> detailesID = idHandler.retrieveCurrentState(query);
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
