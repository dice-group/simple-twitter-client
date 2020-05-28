package upb.dice.twitter;

import twitter4j.Query;

/**
 * An interface for the data extraction
 */
public interface DataExtractor {
    /**
     * Method which writes data objects to a file
     * @param query based on which the data is extracted
     *              from a particular platform
     */
    void storeData(Query query);
}
