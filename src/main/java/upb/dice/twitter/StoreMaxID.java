package upb.dice.twitter;

import twitter4j.Query;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Stores the maxID with the respective keyword/location for a search result which is used as a base point for the next search
 */
public class StoreMaxID {
    private static String directoryName = "Tweets_Search_Details";
    private static String keywordMaxIDFilepath = directoryName + File.separator + "Keyword_MaxID.txt";
    private static String locationMaxIDFilepath = directoryName + File.separator + "Location_MaxID.txt";

    /**
     * @param maxID the maxID retrieved from the current search
     * @param query the query containing the latitude and longitude
     */
    public void locationBasedMaxID(double maxID, Query query) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(locationMaxIDFilepath), true))) {
            out.append(String.valueOf(query.getGeocode())).append(",").append(String.format("%.0f", maxID));
            out.append('\n');
        }
    }

    /**
     * @param maxID the maxID retrieved from the current search
     * @param query the query containing the keyword
     */
    public void keywordBasedMaxID(double maxID, Query query) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(keywordMaxIDFilepath), true))) {
            out.append(query.getQuery()).append(",").append(String.format("%.0f", maxID));
            out.append('\n');
        }
    }
}
