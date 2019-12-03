package upb.dice.twitter;

import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.Map;

/**
 * Checks the rate limit for the requests to determine the remaining limit for the search
 */
public class RateLimitChecker {
    private Twitter twitter = TwitterFactory.getSingleton();
    /**
     * This method checks if the rate limit has been reached for the current search
     *
     * @return 1 if the rate limit has reached
     */
    public boolean rateLimitCheck() throws TwitterException, InterruptedException {
        Map<String, RateLimitStatus> rateLimit = twitter.getRateLimitStatus();
        for (String status : rateLimit.keySet()) {
            RateLimitStatus timeLeft = rateLimit.get(status);
            if (timeLeft.getRemaining() == 0) {
                System.out.println("Rate limit exceeded, will try after 15 minutes");
                return true;
            }
        }
        return false;
    }
}
