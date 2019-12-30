package upb.dice.twitter;

import twitter4j.Query;
import java.io.IOException;

public class KeywordQuery extends QueryGenerator {
    String key;
    KeywordQuery(String key){
        this.key = key;
    }
    @Override
    public Query generateQuery() throws IOException {
        query.setQuery(key);
        return getModifiedQuery(query);
    }
}
