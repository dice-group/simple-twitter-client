package upb.dice.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import java.util.Map;

/**
 * Checks the rate limit for the requests to determine the remaining limit for the search
 */
public class RateLimitChecker {
    private Logger LOGGER = LoggerFactory.getLogger(RateLimitChecker.class);
    private Twitter twitter = TwitterFactory.getSingleton();

    /**
     * This method checks if the rate limit has been reached for the current search
     *
     * @return true if the rate limit has reached or false if the rate limit has not reached
     */
    public boolean rateLimitCheck() throws TwitterException {
        Map<String, RateLimitStatus> rateLimit = twitter.getRateLimitStatus();
        for (String status : rateLimit.keySet()) {
            RateLimitStatus timeLeft = rateLimit.get(status);
            if (timeLeft.getRemaining() == 0) {
                LOGGER.info("Rate limit exceeded, will try after 15 minutes");
                return true;
            }
        }
        return false;
    }
}
