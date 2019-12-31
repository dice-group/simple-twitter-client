package upb.dice.twitter;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.GeoLocation;
import twitter4j.GeoQuery;
import twitter4j.Query;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An application which extracts tweets based on a specific keyword or location provided by the user.
 * The input consists of the keywords and location list passed as command line arguments together along with the period of execution
 * Initially all the available tweets from the past 7 days are extracted. In the consecutive batches only the newest tweets are extracted
 */
public class TweetSearchApplication {

    public static void main(String[] args) {

        final Logger LOGGER = LoggerFactory.getLogger(TweetSearchApplication.class);
        // original queue of keywords
        List<String> keywordQueue = new ArrayList<>();

        //original queue of location details
        final Map<GeoQuery, Long> locationsQueue = new HashMap<>();

        //the period of execution aka the time of the every batch
        final long[] checkPeriod = {60 * 60 * 1000};

        //initialize the parser
        CommandLineParser defaultParser = new DefaultParser();

        //create available options
        Option option1 = Option.builder("k").hasArg(true).desc("Keyword Based Search").numberOfArgs(Option.UNLIMITED_VALUES).longOpt("key").valueSeparator(' ').argName("Keyword").build();
        Option option2 = Option.builder("l").longOpt("loc").hasArg(true).desc("Location Based Search").numberOfArgs(Option.UNLIMITED_VALUES).argName("Latitude><Longitude><Radius In Kilometers><Check period (In hours)").valueSeparator(' ').build();
        Option option3 = Option.builder("h").longOpt("Help").hasArg(false).desc("Help Menu").build();

        //prepare the options
        Options options = new Options();
        options.addOption(option1).addOption(option2).addOption(option3);
        CommandLine cmd;
        try {

            cmd = defaultParser.parse(options, args); //read in the options

            //helper option
            if (cmd != null && cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Command Line Helper", options);

            }
            //either keyword or location based
            else {
                if (cmd != null && (cmd.hasOption("k") || cmd.hasOption("l"))) {
                    if (cmd.hasOption("k")) {
                        String[] searchArgs = cmd.getOptionValues("k");
                        keywordQueue.addAll(Arrays.asList(searchArgs));
                    }
                    if (cmd.hasOption("l")) {
                        String[] searchArgs = cmd.getOptionValues("l");
                        int j = 0;
                        while (j < (searchArgs.length - 1)) {
                            locationsQueue.put(new GeoQuery(new GeoLocation((Double.parseDouble(searchArgs[j])), Double.parseDouble(searchArgs[j + 1]))), Long.parseLong(searchArgs[j + 2]));
                            long periodSeconds = Long.parseLong(searchArgs[searchArgs.length - 1]);
                            checkPeriod[0] = periodSeconds * 60 * 60 * 1000;
//                            checkPeriod[0] = 10;
                            j = j + 3;

                        }
                    }
                }
                IDHandler idHandler = new IDHandler();
                DataExtractor tweetExtractor = new TweetExtractor();
                final Iterator[] iterator_current = {keywordQueue.iterator()};
                final Iterator[] iterator1_current = {locationsQueue.entrySet().iterator()};

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //start keyword based search
                        while (iterator_current[0].hasNext()) {
                            try {
                                String keyword = (String) iterator_current[0].next();
                                LOGGER.info("Current Keyword is: " + keyword);
                                tweetExtractor.getData( new KeywordQuery(keyword).generateQuery());//Start the search
                            } catch (IOException e) {
                                LOGGER.error(e.getMessage());
                            }
                        }

                        //sort the keywords in the decreasing order of their gap which exists and give the keyword with higher gap as the priority to
                        // query in the next timer
                        Map<String, Long> sortedKeyID = new HashMap<>();
                        Map<String, Long> keyID = new HashMap<>();
                        for (String key : keywordQueue) {
                            try {
                                List<Long> keyIDList = idHandler.retrieveCurrentState(new KeywordQuery(key).generateQuery());
                                keyID.put(key, keyIDList.get(1) - keyIDList.get(2));
                            } catch (IOException e) {
                                LOGGER.error(e.getMessage());
                            }
                        }
                        keyID.entrySet()
                                .stream()
                                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                .forEachOrdered(x -> sortedKeyID.put(x.getKey(), x.getValue()));
                        iterator_current[0] = sortedKeyID.keySet().iterator();

//                      start location based search
                        while (iterator1_current[0].hasNext()) {
                            Entry pair = (Entry) iterator1_current[0].next();
                            GeoQuery curentLoc = (GeoQuery) pair.getKey();
                            LOGGER.info("Current Latitude and Longitude " + curentLoc.getLocation().getLatitude() + "," + curentLoc.getLocation().getLongitude()+','+pair.getValue());
                            try {
                                Query locationQuery = new LocationQuery(curentLoc,(long)pair.getValue()).generateQuery();
                                tweetExtractor.getData(locationQuery); //Search start
                            } catch (IOException e) {
                                LOGGER.error(e.getMessage());
                            }
                        }

                        //sort the location details in the decreasing order of their gap which exists and give the location with higher gap as the priority to
                        // query with in the next timer
//                        LinkedHashMap<GeoQuery, Long> sortedLocID = new LinkedHashMap<>();
//                        for (Entry<GeoQuery, Long> lc : locationsQueue.entrySet()) {
//                            try {
//                                List<Long> locIDList = idHandler.retrieveCurrentState(new LocationQuery(lc.getKey(), lc.getValue()).generateQuery());
//                                Map<GeoQuery, Long> locID = new HashMap<>();
//                                locID.put(lc.getKey(), locIDList.get(1) - locIDList.get(2));
//                                locID.entrySet()
//                                        .stream()
//                                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//                                        .forEachOrdered(x -> sortedLocID.put(x.getKey(), x.getValue()));
//                            } catch (IOException e) {
//                                LOGGER.error(e.getMessage());
//                            }
//                        }
                        LinkedHashMap<Map<GeoQuery, Long>, Long> sortedLocID = new LinkedHashMap<>();
                        Map<Map<GeoQuery, Long>, Long> locID =  new HashMap<>();
                        for (Entry<GeoQuery, Long> lc : locationsQueue.entrySet()) {
                            try {
                                List<Long> locIDList = idHandler.retrieveCurrentState(new LocationQuery(lc.getKey(), lc.getValue()).generateQuery());
                                locID.put(Map.of(lc.getKey(), lc.getValue()), locIDList.get(1) - locIDList.get(2));
                            } catch (IOException e) {
                                LOGGER.error(e.getMessage());
                            }
                        }
                        locID.entrySet()
                                .stream()
                                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                .forEachOrdered(x -> sortedLocID.put(x.getKey(), x.getValue()));

                        Map<GeoQuery, Long> result = new HashMap<>();
                        sortedLocID.forEach((map,id)->{
                            map.forEach(result::put);});
                        iterator1_current[0] = result.entrySet().iterator();

                    }
                }, 0, checkPeriod[0]);
                LOGGER.info("Running at: " + new Date());
            }
        } catch (ParseException |
                NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
