package upb.dice.twitter;

import twitter4j.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * If the rate limit is not reached, extract tweets based on the keywords or location (provided as latitude, longitude and the radius of coverage)
 * and stores them in a file. Also maintains a trackrecord in a file of the maxID obtained in every search
 */
public class TweetExtractor {
    private StoreMaxID storeMaxID = new StoreMaxID();
    private Twitter twitter = TwitterFactory.getSingleton();
    private RateLimitChecker rateLimitChecker = new RateLimitChecker();
    private boolean check;

    /**
     * Recursive search for tweets based on this query and store the maxID for the search results
     *
     * @param query search based on this query
     */
    public void getTweet(Query query) throws IOException, TwitterException {
        QueryResult queryResult;
        check = rateLimitChecker.rateLimitCheck();
        System.out.println("Tweets search Start");
        FileWriter writer = new FileWriter("Tweets.txt", true);
        int counter = 0;
        try {
            do {
                check = rateLimitChecker.rateLimitCheck();
                queryResult = twitter.search(query);
                List<Status> queryTweets = queryResult.getTweets();
                long maxId;
                if (counter == 0) {
                    maxId = queryResult.getMaxId();
                    if (query.getQuery() == null) {
                        storeMaxID.locationBasedMaxID(maxId, query);
                    } else {
                        storeMaxID.keywordBasedMaxID(maxId, query);
                    }
                }
                counter++;
                for (Status i : queryTweets) {
                    writer.append(i.toString());
                }
            }
            while (((query = queryResult.nextQuery()) != null) && (check));
        } catch (TwitterException | IOException e) {
            System.out.println("Please try with proper authentication data");
        }
        System.out.println("Tweets search End");
        writer.close();
    }
}
