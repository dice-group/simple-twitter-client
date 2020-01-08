package upb.dice.twitter;

import twitter4j.Query;

public interface DataExtractor {
    /**
     * A method which writes data objects to a file
     * @param query
     */
    void storeData(Query query);
}
