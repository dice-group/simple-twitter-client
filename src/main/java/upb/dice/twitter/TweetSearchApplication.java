package upb.dice.twitter;

import org.apache.commons.cli.*;
import twitter4j.TwitterException;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An application which extracts tweets based on a specific keyword or location provided by the user.
 * 1) If keyword based: The keyword has to be mentioned along with the time of periodic execution (in hours) is provided
 * 2) If location based: The latitude, longitude and the radius along with the time of periodic execution (in hours) is provided
 */
public class TweetSearchApplication {

    private static String key = null;
    private static double latitude, longitude, radius = -1;

    public static void main(String[] args) {
        final long[] checkPeriod = {60 * 60 * 1000};

        //initialize the parser
        CommandLineParser parser = new DefaultParser();

        //create available options
        Option option1 = Option.builder("k").hasArg(true).numberOfArgs(2).desc("Keyword Based Search").longOpt("key").argName("Keyword><Check period (In hours)").build();
        Option option2 = Option.builder("l").longOpt("loc").hasArg(true).numberOfArgs(4).desc("Location Based Search").argName("Latitude><Longitude><Radius In Kilometers><Check period (In hours)").valueSeparator(' ').build();
        Option option3 = Option.builder("h").longOpt("Help").hasArg(false).desc("Help Menu").build();

        //prepare the options
        Options options = new Options();
        options.addOption(option1).addOption(option2).addOption(option3);

        CommandLine cmd;
        try {
            cmd = parser.parse(options, args); //read in the options
            //helper option
            if (cmd != null && cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Command Line Helper", options);
            }
            //either keyword or location based
            else if ((cmd != null && cmd.hasOption("k")) || (cmd != null && cmd.hasOption("l"))) {
                if (cmd.hasOption("k")) {
                    String[] searchArgs = cmd.getOptionValues("k");
                    key = searchArgs[0];
                    long periodSeconds = Long.parseLong(searchArgs[1]);
                    checkPeriod[0] = periodSeconds * 60 * 60 * 1000;
                } else if (cmd.hasOption("l")) {
                    String[] searchArgs = cmd.getOptionValues("l");
                    latitude = Double.parseDouble(searchArgs[0]);
                    longitude = Double.parseDouble(searchArgs[1]);
                    radius = Double.parseDouble(searchArgs[2]);
                    long periodSeconds = Long.parseLong(searchArgs[3]);
                    checkPeriod[0] = periodSeconds * 60 * 60 * 1000;
                }
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    TweetExplorer searchTweets = new TweetExplorer();

                    @Override
                    public void run() {
                        if (key != null) {
                            try {
                                searchTweets.keywordQuery(key);
                            } catch (IOException | InterruptedException | TwitterException e) {
                                e.printStackTrace();
                            }
                        }
                        if (latitude != -1 && longitude != -1 && radius != -1) {
                            try {
                                searchTweets.locationQuery(latitude, longitude, radius);
                            } catch (IOException | TwitterException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }, 0, checkPeriod[0]);
                System.out.println("Running at: " + new java.util.Date());
            }
        } catch (ParseException e) {
            System.out.println("Error in arguments. " + e.getMessage());
        }

    }
}