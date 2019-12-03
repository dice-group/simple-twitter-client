package upb.dice.twitter;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.*;

/**
 * An application which extracts tweets based on a specific keyword or location provided by the user.
 * The input consists of the keywords and location list passed as command line arguments together along with the period of execution
 * Initially all the available tweets from the past 7 days are extracted. In the consecutive batches only the newest tweets are extracted
 */
public class TweetSearchApplication {

    public static void main(String[] args) {

        // original queue of keywords
        Queue<String> keywordQueue = new LinkedList<>();

        //original queue of location details (radius, latitude and longitude)
        Queue<Double> radiusQeue = new LinkedList<>();
        Queue<Double> latitudeQeue = new LinkedList<>();
        Queue<Double> longitudeQeue = new LinkedList<>();

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
                            latitudeQeue.add(Double.parseDouble(searchArgs[j]));
                            longitudeQeue.add(Double.parseDouble(searchArgs[j + 1]));
                            radiusQeue.add(Double.parseDouble(searchArgs[j + 2]));
                            long periodSeconds = Long.parseLong(searchArgs[searchArgs.length - 1]);
                            checkPeriod[0] = periodSeconds * 60 * 60 * 1000;
                            j = j + 3;

                        }
                    }

                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {

                        TweetExplorer tweetExplorer  = new TweetExplorer();

                        @Override
                        public void run() {
                            Iterator iterator, iterator1, iterator2, iterator3;

                            //initialize a new queue by copying from the original queue
                            LinkedList<String> currentKey = new LinkedList<>(keywordQueue);
                            LinkedList<Double> currentLat = new LinkedList<>(latitudeQeue);
                            LinkedList<Double> currentLon = new LinkedList<>(longitudeQeue);
                            LinkedList<Double> currentRad = new LinkedList<>(radiusQeue);


                            //get iterators for the queue
                            iterator = currentKey.iterator();
                            iterator1 = currentLat.iterator();
                            iterator2 = currentLon.iterator();
                            iterator3 = currentRad.iterator();

                            //runs forever with the queues being assigned inside the while loop
                            while ((iterator.hasNext()) || (iterator1.hasNext() && iterator2.hasNext() && iterator3.hasNext())) {

                                //start keyword based search
                                while (iterator.hasNext()) {
                                    String keyCurrent = currentKey.peek();
                                    System.out.println("Current keyword is: " + keyCurrent);

                                    try {
                                        tweetExplorer.keywordQuery(keyCurrent); //Start the search
                                        currentKey.remove(); //Remove from the current queue
                                    } catch (IOException | InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                //start location based search
                                while (iterator1.hasNext() && iterator2.hasNext() && iterator3.hasNext()) {
                                    Double lat = currentLat.peek();
                                    Double lon = currentLon.peek();
                                    Double rad = currentRad.peek();
                                    System.out.println("Current Latitude, Longitude and Radius: " + lat.toString() + "," + lon.toString() + ',' + rad.toString());
                                    try {
                                        tweetExplorer.locationQuery(lat, lon, rad); //Search start
                                        //remove from the current queue
                                        currentLat.remove();
                                        currentLon.remove();
                                        currentRad.remove();

                                    } catch (IOException | InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                //initialize new queue by copying values from the original queue
                                currentKey = new LinkedList<>(keywordQueue);
                                currentLat = new LinkedList<>(latitudeQeue);
                                currentLon = new LinkedList<>(longitudeQeue);
                                currentRad = new LinkedList<>(radiusQeue);

                                //get the iterator for the newly assigned queue
                                iterator = currentKey.iterator();
                                iterator1 = currentLat.iterator();
                                iterator2 = currentLon.iterator();
                                iterator3 = currentRad.iterator();

                            }

                        }
                    }, 0, checkPeriod[0]);
                    System.out.println("Running at: " + new Date());
                }
            }
        } catch (ParseException | NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
