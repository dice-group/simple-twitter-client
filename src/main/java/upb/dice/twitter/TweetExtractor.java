package upb.dice.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of the Data Extractor for twitter
 * If the rate limit is not reached, extract tweets based on the keywords or location (provided as latitude, longitude and the radius of coverage)
 * and stores them in a file. Also maintains a trackrecord in a file of the maxID obtained in every search
 */
public class TweetExtractor implements DataExtractor {

    private final static String directoryName = "Tweets_Search_Details";
    private final static String TweetDataFilepath = directoryName + File.separator + "Tweet_object1.txt";
    public long oldestTweetID;
    private Logger LOGGER = LoggerFactory.getLogger(TweetExtractor.class);
    private IDHandler idHandler = new IDHandler();
    private Twitter twitter = TwitterFactory.getSingleton();
    private boolean remainingTweet;
    public boolean rateLimit;
    private long maxId;
    Query queryDup;
    long sinceID;
    int counter = 0;

    /**
     * Recursive search for tweets based on this query and store the maxID for the search results
     *
     * @param query search based on this query
     */
    public void storeData(Query query) {
        QueryResult queryResult;
        queryDup = query;
        oldestTweetID = 0;
        int j = 1;
        try {
            rateLimit = new RateLimitChecker().rateLimitCheck();
            LOGGER.info("Tweets search Start");
            File file = new File(TweetDataFilepath);
            FileWriter writer = new FileWriter(file, true);
            sinceID = 0;
            do {
                queryResult = twitter.search(query);
                List<Status> queryTweets = queryResult.getTweets();
                sinceID = queryResult.getSinceId();
                //the first page and the first time store maxID
                if (counter == 0) {
                    maxId = queryResult.getMaxId();
                }
                for (Status i : queryTweets) {
                    oldestTweetID = i.getId();
                    writer.append(String.valueOf(j)).append(i.toString());
                    j++;
                }
                counter++;
                rateLimit = new RateLimitChecker().rateLimitCheck();
            }
            while (((query = queryResult.nextQuery()) != null) && (!rateLimit) && oldestTweetID > sinceID);

            /*
            if the rate limit has reached and there is a gap between the baseID and the current tweet, redo the search with the oldest tweet as the upper bound
            also store the oldestTweetID and sinceID, else simply store the maxID as sinceID and oldestTweetID which is the scenario wherein the rate limit has not reached
            and the gap does not exist
            */
            if (oldestTweetID - sinceID > 0) {
                if (rateLimit && query != null) {
                    // There is a gap, rate limit was reached and there exists further objects. Store the current state of the query and extract the remaining
                    idHandler.writeCurrentState(queryDup, maxId, oldestTweetID, sinceID);
                    extractRemaining(query);

                } else if (!rateLimit && query == null) {
                    //There is a gap, but further tweets are not accessible (primary reason is that the tweets are older than 7 days). Consider the state of the query as
                    // equivalent to that of the state of a completed query
                    idHandler.writeCurrentState(queryDup, maxId, maxId, maxId);
                }
            } else {
                // There is no gap, store all the IDs as maxIDs (for easy operational reasons)
                idHandler.writeCurrentState(queryDup, maxId, maxId, maxId);
            }
            writer.close();

        } catch (TwitterException | IOException | InterruptedException e) {
            LOGGER.error(e.toString());
            rateLimit = true;
            try {
                TimeUnit.MINUTES.sleep(15);
            } catch (InterruptedException ex) {
                LOGGER.error(e.toString());
            }
            storeData(query);
        }
        LOGGER.info("Tweets search End");
    }

    /**
     * Sets the current query with the upper bound as the oldestTweetID with the same sinceID
     *
     * @param query which has not extracted all the available data
     */
    public void extractRemaining(Query query) throws InterruptedException {
        query.setUntil(String.valueOf(oldestTweetID));
        query.setSinceId(sinceID);
        remainingTweet = true;
        rateLimit = false;
        LOGGER.info("In extracting the remaining tweets. Current time is: " + new Date());
        TimeUnit.MINUTES.sleep(15);
        storeData(query);
    }
}
