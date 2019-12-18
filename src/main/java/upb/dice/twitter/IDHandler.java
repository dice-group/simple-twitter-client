package upb.dice.twitter;

import twitter4j.GeoLocation;
import twitter4j.Query;

import java.io.*;
import java.util.*;

/**
 * Stores and retrieves the maxID, oldestTweetID and the sinceID with the respective keyword/location for a search result which is used for the query generator
 * during the next search
 */
public class IDHandler {
    private static String directoryName = "Tweets_Search_Details";
    private static String keywordMaxIDFilepath = directoryName + File.separator + "Keyword_MaxID.txt";
    private static String locationMaxIDFilepath = directoryName + File.separator + "Location_MaxID.txt";

    /**
     * This method stores the maxID depending on the query
     *
     * @param maxID the lower bound for the further queries
     * @param query determining the location based or keyword based
     */
    public void storeMaxID(double maxID, Query query) throws IOException {
        //keywordBased MaxID
        if (query.getQuery() != null) {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(keywordMaxIDFilepath), true))) {
                out.append(query.getQuery()).append(",").append(String.format("%.0f", maxID));
            }
        } else {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(locationMaxIDFilepath), true))) {
                out.append(String.valueOf(query.getGeocode())).append(",").append(String.format("%.0f", maxID));
            }
        }
    }

    /**
     * This method searches the parameters stored which serves as the basis of query generation
     *
     * @param key for which the parameters have to be checked
     * @return a list of the maxId, oldestTweetID and the sinceID as a list
     */
    public List<Long> keyParse(String key) throws IOException {
        List<Long> list = new ArrayList<>();
        list.add(-1L);
        list.add(-1L);
        list.add(-1L);
        File file = new File(keywordMaxIDFilepath);
        file.getParentFile().mkdirs();
        file.createNewFile();
        Scanner scan = new Scanner(file);
        while (scan.hasNext()) {
            String line = scan.nextLine();
            if (line.contains(key)) {
                String[] splitString = line.split(",");
                list.set(0, Long.parseLong(splitString[1]));
                list.set(1, Long.parseLong(splitString[2]));
                list.set(2, Long.parseLong(splitString[3]));
            }
        }
        return list;
    }

    /**
     * this method searches the parameters stored which serves as the basis of query generation
     *
     * @param geoLocation for which the parameters have to be checked
     * @return the maxID for the respective geoLocation
     */
    public List<Long> locationParse(GeoLocation geoLocation) throws IOException {
        Map<List<String>, List<Long>> pairStringMap = new HashMap<>();
        File file = new File(locationMaxIDFilepath);
        file.getParentFile().mkdir();
        file.createNewFile();
        String line;
        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((line = br.readLine()) != null) {
            String[] splitString = line.split(",");
            pairStringMap.put(Arrays.asList(splitString[0], splitString[1]), Arrays.asList(Long.parseLong(splitString[3]), Long.parseLong(splitString[4]), Long.parseLong(splitString[5])));
        }
        String lat_str = String.valueOf(geoLocation.getLatitude());
        String lon_str = String.valueOf(geoLocation.getLongitude());
        List<Long> IDList = new ArrayList<>();
        IDList.add(-1L);
        IDList.add(-1L);
        IDList.add(-1L);
        if (pairStringMap.containsKey(Arrays.asList(lat_str, lon_str))) {
            IDList = pairStringMap.get(Arrays.asList(lat_str, lon_str));
        }
        return IDList;
    }

    /**
     * Stores the oldestTweetID for a given query
     *
     * @param oldestTweetID
     * @param query         to which the parameters have to be stored
     */
    public void writeOldestTweetID(double oldestTweetID, Query query) throws IOException {
        if (query.getQuery() != null) {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(keywordMaxIDFilepath), true))) {
                out.append(",").append(String.format("%.0f", oldestTweetID));
            }
        } else {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(locationMaxIDFilepath), true))) {
                out.append(",").append(String.format("%.0f", oldestTweetID));
            }
        }
    }

    /**
     * Stores the sinceID for a given query
     *
     * @param sinceID
     * @param query   to which the parameters have to be stored
     * @throws IOException
     */
    public void writeSinceID(double sinceID, Query query) throws IOException {
        if (query.getQuery() != null) {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(keywordMaxIDFilepath), true))) {
                out.append(",").append(String.format("%.0f", sinceID)).append('\n');
            }
        } else {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(locationMaxIDFilepath), true))) {
                out.append(",").append(String.format("%.0f", sinceID)).append('\n');
            }
        }
    }
}
