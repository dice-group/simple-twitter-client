package upb.dice.twitter;

import twitter4j.Query;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class StoreMaxID {

    //Location based store
    void locationBasedMaxID(double maxID, Query query) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter("Latitude_Longitude_MaxID1.txt", true))) {
            out.append(String.valueOf(query.getGeocode())).append(",").append(String.valueOf(maxID));
            out.append('\n');
        }
    }

    //Keyword Based store
    void keywordBasedMaxID(double maxID, Query query) throws IOException{
         try (BufferedWriter out = new BufferedWriter(new FileWriter("Keyword_MaxID1.txt", true))) {
                out.append(query.getQuery()).append(",").append(String.valueOf(maxID));
                out.append('\n');
            }
        }
}
