package bme.tmit.telki.distance_matrix;

import com.google.maps.GeoApiContext;

import java.io.*;

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

    private final String KEY_FILE = "secret.config";
    private final String API_KEY = parseKeyConfig();
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

    private String parseKeyConfig() {
        String currentWorkingDir = System.getProperty("user.dir");
        File file = new File(currentWorkingDir, KEY_FILE);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {

                String[] split = st.split(":");
                if (split[0].equals("key")) {
                    return split[1];
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("secret.config has to be made manually in project root, with valid api key (template: sample-secret.config)");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "key not found";
    }

}