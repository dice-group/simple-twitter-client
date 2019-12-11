package upb.dice.twitter;

import twitter4j.GeoLocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Retrieves the maxID for a specific query from a file storing the maxID from the previous
 * search results. The file contains the keyword/location with the respective maxID obtained
 * at that instant of search
 */
public class MaxIDChecker {
    private static String directoryName = "Tweets_Search_Details";
    private static String keywordMaxIDFilepath = directoryName + File.separator + "Keyword_MaxID.txt";
    private static String locationMaxIDFilepath = directoryName + File.separator + "Location_MaxID.txt";
    /**
     * This method checks for the maxID for the given keyword, Returns 1 if keyword is new
     *
     * @param key to check the respective maxID
     * @return the maxID for the given key
     */
    public long keyParse(String key) throws IOException {
        long result;
        Map<String, String> map = new HashMap<>();
        File file = new File(keywordMaxIDFilepath);
        file.getParentFile().mkdirs();
        file.createNewFile();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] splitString = line.split(",");
            map.put(splitString[0], splitString[1]);
        }
        if (map.containsKey(key)) {
            String id_string = map.get(key);
            result = Long.parseLong(id_string);
        } else {
            result = 1L;
        }
        return result;
    }

    /**
     * This method checks for the maxID for the current geoLocation. Returns 1 if geoLocation is new
     *
     * @param geoLocation for which the maxID has to be checked
     * @return the maxID for the respective geoLocation
     */
    public long locationParse(GeoLocation geoLocation) throws IOException {
        long result = 1L;
        Map<List<String>, String> pairStringMap = new HashMap<>();
        File file = new File(locationMaxIDFilepath);
        file.getParentFile().mkdir();
        file.createNewFile();
        String line;
        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((line = br.readLine()) != null) {
            String[] splitString = line.split(",");
            String latitude = splitString[0];
            String longitude = splitString[1];
            String locMaxId = splitString[3];
            pairStringMap.put(Arrays.asList(latitude, longitude), locMaxId);
        }
        String lat_str = String.valueOf(geoLocation.getLatitude());
        String lon_str = String.valueOf(geoLocation.getLongitude());
        if (pairStringMap.containsKey(Arrays.asList(lat_str, lon_str))) {
            String id_string = pairStringMap.get(Arrays.asList(lat_str, lon_str));
            result = Long.parseLong(id_string);
        }
        return result;
    }
}


