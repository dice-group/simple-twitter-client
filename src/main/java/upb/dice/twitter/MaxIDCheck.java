package upb.dice.twitter;

import twitter4j.GeoLocation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MaxIDCheck {
    long keyParse(String key) throws IOException {
        Map<String, String> map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader("Keyword_MaxID1.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitString = line.split(",");
                map.put(splitString[0], splitString[1]);

            }

            if (map.containsKey(key)) {
                String id_string = map.get(key);
                System.out.println("key and max are"+key+id_string);
                return (new BigDecimal(id_string).intValue());
            }
        }
        return 1L;
    }

    long locationParse(GeoLocation geoLocation) throws IOException {
        Map<List<String>, String> pairStringMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("Latitude_Longitude_MaxID1.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitString = line.split(",");
                String latitude = splitString[0];
                String longitude = splitString[1];
                String locMaxId = splitString[3];
                pairStringMap.put(Arrays.asList(latitude, longitude), locMaxId);
            }
            String lon_str = String.valueOf(geoLocation.getLatitude());
            String lat_str = String.valueOf(geoLocation.getLatitude());
            if (pairStringMap.containsKey(Arrays.asList(lon_str, lat_str))) {
                String id_string = pairStringMap.get(Arrays.asList(lon_str, lat_str));
                return (new BigDecimal(id_string).intValue());
            }
            return 1L;
        }
    }

}
