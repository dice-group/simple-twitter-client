package upb.dice.twitter;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

class TweetExtraction {

    private Twitter twitter;
    private StoreMaxID storeMaxID = new StoreMaxID();

    TweetExtraction(){

        ConfigurationBuilder configBuilder = new ConfigurationBuilder();
        configBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey("CONSUMER KEY")
                .setOAuthConsumerSecret("CONSUMER SECRET")
                .setOAuthAccessToken("ACCESS TOKEN")
                .setOAuthAccessTokenSecret("ACCESS TOKEN SECRET")
        ;
        twitter = new TwitterFactory(configBuilder.build()).getInstance();
    }

    void getTweet(Query query) throws IOException {
        QueryResult queryResult;
        System.out.println("Tweets search Start");
        FileWriter writer = new FileWriter("Tweets.txt", true);
        int counter = 0;
        try{
            do {
                queryResult = twitter.search(query);
                List<Status> queryTweets = queryResult.getTweets();
                long maxId;
                //as maxID is available at the first parse, counter is 0
                if (counter == 0) {
                    maxId = queryResult.getMaxId();

                    //location based store
                    if (query.getQuery() == null) {
                        storeMaxID.locationBasedMaxID(maxId,query);
                    }
                    //keyword based store
                    else {
                        storeMaxID.keywordBasedMaxID(maxId,query);
                    }
                }
                counter ++;
                for (Status i : queryTweets) {
                    if (!i.isRetweet()) {
                        writer.append(String.valueOf(i.getId())).append(":").append(i.getText()).append('\n');
                    }
                }
            }
            while ((query = queryResult.nextQuery()) != null);

        } catch (TwitterException | IOException e) {
            System.out.println("Please try after 15 minutes");
            e.printStackTrace();

        }
        System.out.println("Tweets search End");
    }
}
