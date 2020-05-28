package upb.dice.twitter;

import twitter4j.Query;
import java.io.IOException;

/**
 * Generates a query based on the location details provided and the
 * by considering the current state of the query
 */
public class LocationQuery extends QueryGenerator {
    private GeoPosition geoPosition;

    /**
     * Constructor taking the position for which the query has to be generated
     * @param geoPosition for which the query has to be generated
     */
    public LocationQuery(GeoPosition geoPosition) {
        this.geoPosition = geoPosition;
    }

    @Override
    public Query generateQuery() throws IOException {
        query.setGeoCode(geoPosition.getLocation(), geoPosition.getRadius(), Query.KILOMETERS);
        return getModifiedQuery(query);
    }
}
