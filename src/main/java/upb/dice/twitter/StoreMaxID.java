package upb.dice.twitter;

import twitter4j.Query;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Stores the maxID with the respective keyword/location for a search result which is used as a base point for the next search
 */
public class StoreMaxID {

    /**
     * @param maxID the maxID retrieved from the current search
     * @param query the query containing the latitude and longitude
     */
    public void locationBasedMaxID(double maxID, Query query) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter("Latitude_Longitude_MaxID1.txt", true))) {
            out.append(String.valueOf(query.getGeocode())).append(",").append(String.valueOf(maxID));
            out.append('\n');
        }
    }

    /**
     * @param maxID the maxID retrieved from the current search
     * @param query the query containing the keyword
     */
    public void keywordBasedMaxID(double maxID, Query query) throws IOException{
         try (BufferedWriter out = new BufferedWriter(new FileWriter("Keyword_MaxID.txt", true))) {
                out.append(query.getQuery()).append(",").append(String.valueOf(maxID));
                out.append('\n');
            }
        }
}
