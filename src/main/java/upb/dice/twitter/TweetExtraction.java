package upb.dice.twitter;

import twitter4j.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static twitter4j.TwitterFactory.getSingleton;

/**
 * Extract tweets based on the keywords or location(provided as latitude, longitude and the radius of coverage)
 */
public class TweetExtraction {
    private StoreMaxID storeMaxID = new StoreMaxID();
    private Twitter twitter = getSingleton();

    /**
     * Recursive search for tweets based on this query and store the maxID for the search results
     * @param query search based on this query
     */

    public void getTweet(Query query) throws IOException {
        QueryResult queryResult;
        System.out.println("Tweets search Start");
        FileWriter writer = new FileWriter("Tweets.txt", true);
        int counter = 0;
        try{
            do {
                queryResult = twitter.search(query);
                List<Status> queryTweets = queryResult.getTweets();
                long maxId;
                if (counter == 0) {
                    maxId = queryResult.getMaxId();

                    if (query.getQuery() == null) {
                        storeMaxID.locationBasedMaxID(maxId,query);
                    }
                    else {
                        storeMaxID.keywordBasedMaxID(maxId,query);
                    }
                }
                counter ++;
                for(Status i: queryTweets) {
                            writer.append(String.valueOf(i.getId())).append(":").append(i.getText()).append('\n');
                        }
            }
            while ((query = queryResult.nextQuery()) != null);

        } catch (TwitterException | IOException e) {
            System.out.println("Please try after 15 minutes or with proper authentication data");
            e.printStackTrace();

        }
        System.out.println("Tweets search End");
    }
}
