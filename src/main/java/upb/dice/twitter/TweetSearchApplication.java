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
    public static void main(String[] args) {
        final long[] checkPeriod = {60*60*1000};
        Timer timer = new Timer();

        //initialize the parser
        CommandLineParser parser = new DefaultParser();

        //create available options
        Option option1 = Option.builder("k").hasArg(true).numberOfArgs(2).desc("Keyword Based Search").longOpt("key").argName("Keyword><Check period (In hours)").build();
        Option option2 = Option.builder("l").longOpt("loc").hasArg(true).numberOfArgs(4).desc("Location Based Search").argName("Latitude><Longitude><Radius In Kilometers><Check period (In hours)").valueSeparator(' ').build();
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
        CommandLine finalCmd = cmd;
        assert finalCmd != null;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    TweetExplorer searchTweets = new TweetExplorer();
                    if (finalCmd.hasOption("h")) {
                        HelpFormatter formatter = new HelpFormatter();
                        formatter.printHelp("Command Line Helper", options);
                        System.exit(0);
                    }
                    //if there is a value for keybased, get it
                    if (finalCmd.hasOption("k")) {
                        String[] searchArgs = finalCmd.getOptionValues("k");
                        String key = searchArgs[0];
                        long periodSeconds = Long.parseLong(searchArgs[1]);
                        checkPeriod[0] = periodSeconds * 60 * 60 * 1000;
                        try {
                            searchTweets.keywordQuery(key);
                        } catch (IOException | TwitterException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (finalCmd.hasOption("l")) {
                        String[] searchArgs = finalCmd.getOptionValues("l");
                        double latitude = Double.parseDouble(searchArgs[0]);
                        double longitude = Double.parseDouble(searchArgs[1]);
                        double radius = Double.parseDouble(searchArgs[2]);
                        long periodSeconds = Long.parseLong(searchArgs[3]);
                        checkPeriod[0] = periodSeconds * 60 * 60 * 1000;
                        try {
                            searchTweets.locationQuery(latitude, longitude, radius);
                        } catch (IOException | TwitterException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Running: " + new java.util.Date());
                }
            }, 0, checkPeriod[0]);
    }
}