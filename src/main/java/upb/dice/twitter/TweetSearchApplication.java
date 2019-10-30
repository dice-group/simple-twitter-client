package upb.dice.twitter;

import org.apache.commons.cli.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An application which extracts tweets based on a specific keyword or location provided by the user
 */
public class TweetSearchApplication {

    public static void main(String args[]) {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                SearchTweets searchTweets = new SearchTweets();

                //initialize the parser
                CommandLineParser parser = new DefaultParser();

                //create available options
                Option option1 = Option.builder("k").hasArg(true).numberOfArgs(1).desc("Keyword Based Search").longOpt("key").argName("Keyword").build();
                Option option2 = Option.builder("l").longOpt("loc").hasArg(true).numberOfArgs(3).desc("Location Based Search").argName("Latitude><Longitude><Radius In Kilometers").valueSeparator(' ').build();
                Option option3 = Option.builder("h").longOpt("Help").hasArg(false).desc("Help Menu").build();

                //prepare the options
                Options options = new Options();
                options.addOption(option1).addOption(option2).addOption(option3);

                CommandLine cmd = null;
                try {
                    cmd = parser.parse(options, args);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //if there is a value for keybased, get it
                assert cmd != null;
                if (cmd.hasOption("k")) {
                    String[] searchArgs = cmd.getOptionValues("k");

                    String key = searchArgs[0];
                    try {
                        searchTweets.keywordQuery(key);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (cmd.hasOption("l")) {
                    String[] searchArgs = cmd.getOptionValues("l");
                    double latitude = Double.parseDouble(searchArgs[0]);
                    double longitude = Double.parseDouble(searchArgs[1]);
                    double radius = Double.parseDouble(searchArgs[2]);
                    try {
                        searchTweets.locationQuery(latitude, longitude, radius);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (cmd.hasOption("h")) {
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp("Command Line Helper", options);

                }
                System.out.println("Running: " + new java.util.Date());
            }
        }, 0, (long) 1.44e+7); //Run after every 4 hours

    }
}