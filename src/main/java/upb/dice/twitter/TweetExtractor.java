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
 * If the rate limit is not reached, extract tweets based on the keywords or location (provided as latitude, longitude and the radius of coverage)
 * and stores them in a file. Also maintains a trackrecord in a file of the maxID obtained in every search
 */
public class TweetExtractor {

    private static String directoryName = "Tweets_Search_Details";
    private static String TweetDataFilepath = directoryName + File.separator + "Tweet_object.txt";
    public long oldestTweetID;
    private Logger LOGGER = LoggerFactory.getLogger(TweetExtractor.class);
    private IDHandler idHandler = new IDHandler();
    private Twitter twitter = TwitterFactory.getSingleton();
    private boolean remainingTweet;
    public boolean rateLimit;
    private long maxId;

    /**
     * Recursive search for tweets based on this query and store the maxID for the search results
     *
     * @param query search based on this query
     */
    public void getTweet(Query query) throws InterruptedException {
        QueryResult queryResult;
        System.out.println(query);
        Query query1 = query;
        oldestTweetID = 0;
        long sinceID;
        try {
            rateLimit = new RateLimitChecker().rateLimitCheck();
            LOGGER.info("Tweets search Start");
            File file = new File(TweetDataFilepath);
            FileWriter writer = new FileWriter(file, true);
            int counter = 0;
            sinceID = 0;

            do {
                queryResult = twitter.search(query);
                List<Status> queryTweets = queryResult.getTweets();

                //the first page and the first time store maxID
                if (counter == 0) {
                    maxId = queryResult.getMaxId();
                    sinceID = queryResult.getSinceId();
                    idHandler.storeMaxID(maxId, query);
                }

                for (Status i : queryTweets) {
                    oldestTweetID = i.getId();
                    writer.append(i.toString());
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
            if (oldestTweetID - sinceID > 0 && rateLimit && query != null) {
                idHandler.writeOldestTweetID(oldestTweetID, query1);
                idHandler.writeSinceID(sinceID, query1);
                query.setUntil(String.valueOf(oldestTweetID));
                query.setSinceId(sinceID);
                remainingTweet = true;
                rateLimit = false;
                LOGGER.info("In extracting the remaining tweets. Current time is: " + new Date());
                TimeUnit.MINUTES.sleep(15);
                getTweet(query);
            } else {
                idHandler.writeOldestTweetID(maxId, query1);
                idHandler.writeSinceID(maxId, query1);
            }

            writer.close();

        } catch (TwitterException | IOException e) {
            LOGGER.error(e.toString());
            rateLimit = true;
            TimeUnit.MINUTES.sleep(15);
            getTweet(query);
        }
        LOGGER.info("Tweets search End");
    }
}
