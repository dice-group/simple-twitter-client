package upb.dice.twitter;

import twitter4j.Query;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Stores and retrieves the maxID, oldestTweetID and the sinceID with the respective keyword/location for a search result which is used for the query generator
 * during the next search
 */
public class IDHandler {
    private final static String directoryName = "Tweets_Search_Details";
    private final static String keywordMaxIDFilepath = directoryName + File.separator + "Keyword_MaxID2.txt";
    private final static String locationMaxIDFilepath = directoryName + File.separator + "Location_MaxID2.txt";

    /**
     * This method records the details of the current status of a particular query
     *
     * @param query         of which the current state has to be recorded
     * @param maxID         The ID
     * @param oldestTweetID the ID until which the search was done
     * @param sinceID       the lower bound to the search which is done
     * @throws IOException
     */
    public void writeCurrentState(Query query, double maxID, double oldestTweetID, double sinceID) throws IOException {
        BufferedWriter out;
        if (query.getQuery() != null) {
            out = new BufferedWriter(new FileWriter(new File(keywordMaxIDFilepath), true));
            out.newLine();
            out.append(query.getQuery()).append(",").append(String.format("%.0f", maxID));
        } else {
            out = new BufferedWriter(new FileWriter(new File(locationMaxIDFilepath), true));
            out.newLine();
            out.append(String.valueOf(query.getGeocode())).append(",").append(String.format("%.0f", maxID));
        }
        out.append(",").append(String.format("%.0f", oldestTweetID)).append(",").append(String.format("%.0f", sinceID));
        out.close();
    }

    /**
     * This method returns a list containing the current status for a particular query
     *
     * @param query for which the current state (describing if all the data is extracted or not) is obtained
     * @return a list with the current state with the details of the maxID, oldestTweetID and the sinceID
     * @throws IOException
     */
    public List<Long> retrieveCurrentState(Query query) throws IOException {
        File file;
        List<Long> list = new ArrayList<>();
        list.add((long) Double.POSITIVE_INFINITY);//maxID
        list.add((long) Double.POSITIVE_INFINITY);//oldestTweetID
        list.add(-1L);//sinceID
        if (query.getQuery() != null) {
            file = new File(keywordMaxIDFilepath);
        } else {
            file = new File(locationMaxIDFilepath);
        }
        file.getParentFile().mkdirs();
        file.createNewFile();
        Scanner scan = new Scanner(file);
        if (scan.hasNext()) {
            do {
                String line = scan.nextLine();
                if (query.getQuery() != null){
                    if(line.contains(query.getQuery())){
                        String[] splitString = line.split(",");

                        //if the current oldestTweetID value is not null, replace with the new maxID and oldestTweetID value. Else return the previous value for both
                        if (splitString[2] != null) {
                            list.set(0, Long.parseLong(splitString[1]));
                            list.set(1, Long.parseLong(splitString[2]));
                        }
                        if (splitString[3] != null) //if sinceID is not available, set the previously available sinceID
                            list.set(2, Long.parseLong(splitString[3]));
                    }
                } else if (query.getGeocode() != null) {
                    String[] locationString = query.getGeocode().split(",");
                    if (line.contains((locationString[0] + "," + locationString[1]))) {
                        String[] splitString = line.split(",");
                        //if the current oldestTweetID value is not null, replace with the new maxID and oldestTweetID value. Else return the previous value for both
                        if (splitString[4] != null) {
                            list.set(0, Long.parseLong(splitString[3]));
                            list.set(1, Long.parseLong(splitString[4]));
                        }
                        if (splitString[5] != null) //if sinceID is not available, set the previously available sinceID
                            list.set(2, Long.parseLong(splitString[5]));
                    }
                }
            } while (scan.hasNext());
        } else {
            //if nothing is found, set to -1 as usual
            list.set(0,-1L);
            list.set(1, -1L);
        }
        scan.close();
        return list;
    }
}


