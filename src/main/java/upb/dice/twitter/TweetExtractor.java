package upb.dice.twitter;

import twitter4j.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * If the rate limit is not reached, extract tweets based on the keywords or location (provided as latitude, longitude and the radius of coverage)
 * and stores them in a file. Also maintains a trackrecord in a file of the maxID obtained in every search
 */
public class TweetExtractor {

    private StoreMaxID storeMaxID = new StoreMaxID();
    private Twitter twitter = TwitterFactory.getSingleton();
    private boolean remainingTweet;
    public boolean rateLimit;

    /**
     * Recursive search for tweets based on this query and store the maxID for the search results
     *
     * @param query search based on this query
     */
    public void getTweet(Query query) throws InterruptedException {
        QueryResult queryResult;
        try {
            rateLimit = new RateLimitChecker().rateLimitCheck();
            System.out.println("Tweets search Start");
            String directoryName = "Tweets Search Details";
            FileWriter writer = new FileWriter(new File(directoryName + File.separator + "Tweet object.txt"), true);
            int counter = 0;
            long sinceID = 0;
            long oldestTweetID;
            do {
                queryResult = twitter.search(query);
                List<Status> queryTweets = queryResult.getTweets();
                long maxId = queryResult.getMaxId();

                //the first page and the first time
                if (counter == 0 && !remainingTweet) {
                    sinceID = queryResult.getSinceId();
                    if (query.getQuery() != null) {
                        storeMaxID.keywordBasedMaxID(maxId, query);
                    }
                    else {
                        storeMaxID.locationBasedMaxID(maxId, query);
                    }
                }
                oldestTweetID = 0;
                for (Status i : queryTweets) {
                    oldestTweetID = i.getId();
                    writer.append(i.toString());
                }
                counter++;
                rateLimit = new RateLimitChecker().rateLimitCheck();
            }
            while (((query = queryResult.nextQuery()) != null) && (!rateLimit) && oldestTweetID > sinceID);

            //if the rate limit has reached and there is a gap between the base and the current tweet, redo the search with the oldest tweet as the upper bound
            if (oldestTweetID - sinceID > 0 && rateLimit && query != null) {
                query.setSinceId(sinceID);
                query.setUntil(String.valueOf(oldestTweetID));
                remainingTweet = true;
                rateLimit = false;
                System.out.println("In extracting the remaining tweets. Current time is: " + new Date());
                TimeUnit.MINUTES.sleep(15);
                getTweet(query);
            }
            writer.close();
        } catch (TwitterException | IOException | InterruptedException e) {
            System.out.println(e);
            rateLimit = true;
            TimeUnit.MINUTES.sleep(15);
        }
        System.out.println("Tweets search End");
    }
}
