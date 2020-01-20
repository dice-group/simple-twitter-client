package upb.dice.twitter;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Query;
import java.io.IOException;
import java.util.*;

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
        List<GeoPosition> locationsQueue = new ArrayList<>();

        //the period of execution (the time of the every batch)
        final long[] checkPeriod = {60 * 60 * 1000};

        //initialize the parser
        CommandLineParser defaultParser = new DefaultParser();

        //create available options
        Option option1 = Option.builder("k").hasArg(true).desc("Keyword Based Search").numberOfArgs(Option.UNLIMITED_VALUES).longOpt("key").valueSeparator(' ').argName("Keyword").build();
        Option option2 = Option.builder("l").longOpt("loc").hasArg(true).desc("Location Based Search").numberOfArgs(Option.UNLIMITED_VALUES).argName("Latitude><Longitude><Radius In Kilometers><Check period (In hours)").valueSeparator(' ').build();
        Option option3 = Option.builder("h").longOpt("Help").hasArg(false).desc("Help Menu").build();
        Option option4 = Option.builder("t").longOpt("Period").hasArg(true).desc("Period of search").build();

        //prepare the options
        Options options = new Options();
        options.addOption(option1).addOption(option2).addOption(option3).addOption(option4);
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
                            locationsQueue.add(new GeoPosition(Double.valueOf(searchArgs[j]), Double.valueOf(searchArgs[j + 1]),
                                    Double.valueOf(searchArgs[j + 2])));
                            j = j + 3;
                        }
                    }

                }
                if (cmd != null && cmd.hasOption("t")) {
                    String[] searchArgs = cmd.getOptionValues("t");
                    checkPeriod[0] = Long.parseLong(searchArgs[0]) * 60 * 60 * 1000;
                }
                DataExtractor tweetExtractor = new TweetExtractor();
                final Iterator[] keywordIterator = {keywordQueue.iterator()};
                final Iterator[] positionIterator = {locationsQueue.iterator()};

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //start keyword based search
                        while (keywordIterator[0].hasNext()) {
                            try {
                                String keyword = (String) keywordIterator[0].next();
                                LOGGER.info("Current Keyword is: " + keyword);
                                tweetExtractor.storeData(new KeywordQuery(keyword).generateQuery());//Start the search
                            } catch (IOException e) {
                                LOGGER.error(e.getMessage());
                            }
                        }
                        keywordIterator[0] = keywordQueue.iterator();//set the iterator for the next run

                        //start location based search
                        while (positionIterator[0].hasNext()) {
                            try {
                                GeoPosition currentPosition = (GeoPosition) positionIterator[0].next();
                                LOGGER.info("Current position is: "+ currentPosition);
                                Query locationQuery = new LocationQuery(currentPosition).generateQuery();
                                tweetExtractor.storeData(locationQuery);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        positionIterator[0] = locationsQueue.iterator();//set the iterator for the next run
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
