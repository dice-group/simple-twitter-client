package upb.dice.twitter;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.util.*;

public class TweetSearchApplication {
    private double lat;
    private double lon;
    private Twitter twitter;

    //Authentication
    private TweetSearchApplication() {

        ConfigurationBuilder configBuilder = new ConfigurationBuilder();
        configBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey("iCv6sCoYcQtM3EgxbPrz3zj8p")
                .setOAuthConsumerSecret("RXkay6HURXFPRJPA60Z3ZBHk775qIf6aSHjPCErAVSUkTXBGg8")
                .setOAuthAccessToken("1174976100880896000-2XLLvld7N051jWYUpHcJdvgNWTEng1")
                .setOAuthAccessTokenSecret("ZloZQ4u0r3ddyts5lGzq28QlO5GRoUq3hUoSFYwsB43j8")
        ;
        twitter = new TwitterFactory(configBuilder.build()).getInstance();

    }

    //Check if the given keyword tweets are already extracted, if not keep track of the new maxID
    private long keyParse(String key) throws IOException {
        Map<String, String> map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader("Keyword_MaxID.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitString = line.split(":");
                map.put(splitString[0], splitString[1]);

            }
            if (map.containsKey(key)) {
                String id_string = map.get(key);
                return Long.parseLong(id_string);
            }
        }
        return 1L;
    }

    //Check if the given location tweets are already extracted, if not, keep track of the new maxID
    private long locationParse(GeoLocation geoLocation) throws IOException {
        Map<List<String>, String> pairStringMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("Latitude_Longitude_MaxID.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitString = line.split(":");
                String latitude = splitString[0];
                String longitude = splitString[1];
                String locMaxId = splitString[2];
                pairStringMap.put(Arrays.asList(latitude, longitude), locMaxId);
            }
            String lon_str = String.valueOf(geoLocation.getLatitude());
            String lat_str = String.valueOf(geoLocation.getLatitude());
            if (pairStringMap.containsKey(Arrays.asList(lon_str, lat_str))) {
                String id_string = pairStringMap.get(Arrays.asList(lon_str, lat_str));
                return Long.parseLong(id_string);
            }
            return 1L;
        }
    }


    private void queryBuild(String key) throws TwitterException, IOException {
        long key_id_1 = keyParse(key);
        Query query1 = new Query(key);
        if (key_id_1 != 1) {
            query1.sinceId(key_id_1);
        }
        getTweet(query1);
    }

    //Location based Tweet Search
    private void queryBuild(double latitude, double longitude, double radius) throws TwitterException, IOException {
        GeoQuery geoQuery = new GeoQuery(new GeoLocation(latitude, longitude));
        Query query = new Query();
        query.setGeoCode(geoQuery.getLocation(), radius, Query.KILOMETERS);
        lat = latitude;
        lon = longitude;

//       query.until("2019-02-31").since("2019-02-01");   //Specific time period
        long key_id_1 = locationParse(new GeoLocation(latitude, longitude));
        if (key_id_1 != 1) {
            query.setSinceId(key_id_1);
        }
        getTweet(query);
    }


    //Extract the tweets based on the query built
    private void getTweet(Query query) throws TwitterException, IOException {
        QueryResult queryResult;
        System.out.println("Tweets search Start");
        FileWriter writer = new FileWriter("Tweets.txt", true);
        int counter = 0;
        do {
            queryResult = twitter.search(query);
            List<Status> queryTweets = queryResult.getTweets();
            long maxId;
            if (counter == 0) {
                maxId = queryResult.getMaxId();
                //Location based
                if (query.getQuery() == null) {
                    try (BufferedWriter out = new BufferedWriter(new FileWriter("Latitude_Longitude_MaxID.txt", true))) {
                        out.append(String.valueOf(lat)).append(":").append(String.valueOf(lon)).append(":").append(String.valueOf(maxId));
                        out.append('\n');
                    }
                }
                //Keyword Based
                else {
                    String key = query.getQuery();
                    try (BufferedWriter out = new BufferedWriter(new FileWriter("Keyword_MaxID.txt", true))) {
                        out.append(key).append(":").append(String.valueOf(maxId));
                        out.append('\n');
                    }
                }
            }
            counter += 1;
            for (Status i : queryTweets) {
                if (!i.isRetweet()) {
                    writer.append(String.valueOf(i.getId())).append(":").append(i.getText()).append('\n');
                }
            }

        }
        while ((query = queryResult.nextQuery()) != null);
        System.out.println("Tweets search End");
    }


    public static void main(String[] args) throws TwitterException, IOException {
        //List of Keywords
        ArrayList<String> keyWords = new ArrayList<>(Arrays.asList("Titan"));
        TweetSearchApplication tweetSearch = new TweetSearchApplication();
        double radius = 10;
        double longitude = 13.379758;
        double latitude = -8.7575;
        System.out.println("1: Keyword Based Search. 2: Location Based Search");
        Scanner in = new Scanner(System.in);
        int s = in.nextInt();
        switch (s) {
            case 1:
                for (String s1 : keyWords)
                    tweetSearch.queryBuild(s1);

            case 2:
                tweetSearch.queryBuild(latitude, longitude, radius);
        }
    }
}