package bme.tmit.telki.distance_matrix;

import com.google.maps.GeoApiContext;

/**
 * Singleton class
 * Provides the Geo api context with the API key.
 * <p>
 * nem tudom mi az a proper QPS enforcement, de singletont kértek, itt van
 * <p>
 * "The GeoApiContext is designed to be a Singleton in your application. Please instantiate one on application startup,
 * and continue to use it for the life of your application. This will enable proper QPS enforcement across all of your requests."
 * https://github.com/googlemaps/google-maps-services-java
 */
public class GeoApiContextProvider {

    private final String API_KEY = "AIzaSyBcJixlXHjzXIc0McRXeGtaEDp_jQh9Ef0";
    private static GeoApiContextProvider contextProvider = null;
    public GeoApiContext context;

    public GeoApiContextProvider() {
        context = new GeoApiContext.Builder()
                .apiKey(API_KEY)
                .build();
    }

    /**
     * @return Returns GeoApiContext used in google API requests
     */
    public static GeoApiContext getContext() {
        if (contextProvider == null)
            contextProvider = new GeoApiContextProvider();
        return contextProvider.context;
    }
}