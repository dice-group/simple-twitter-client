package upb.dice.twitter;

import twitter4j.Query;
import java.io.IOException;

/**
 * Generates a query based on the keyword provided and the
 * by considering the current state of the query
 */
public class KeywordQuery extends QueryGenerator {
    private String key;

    /**
     * Constructor which sets the key
     * @param key for which the query should be generated
     */
    KeywordQuery(String key){
        this.key = key;
    }
    @Override
    public Query generateQuery() throws IOException {
        query.setQuery(key);
        return getModifiedQuery(query);
    }
}
