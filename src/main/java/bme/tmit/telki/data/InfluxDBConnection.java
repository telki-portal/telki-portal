package bme.tmit.telki.data;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Singleton class
 * Handles database connection.
 */
public class InfluxDBConnection {
    private static final Logger LOG = LoggerFactory.getLogger(InfluxDBConnection.class);
    public static final String measurement_name = "traveltime";

    private static InfluxDBConnection connectionProvider = null;
    private static InfluxDB connection;

    private static final String url = "http://127.0.0.1:8086";
    private static final String username = "root";
    private static final String password = "root";
    private static final String database_name = "testdb";

    private InfluxDBConnection() {
        connection = InfluxDBFactory.connect(url, username, password);

        //checking connection
        Pong response = connection.ping();
        if (response.getVersion().equalsIgnoreCase("unknown")) {
            LOG.error("Error pinging server.");
            //todo exception?
            return;
        }
        LOG.info("Successfully connected to database as " + username);

        //create / use database
        if (!connection.databaseExists(database_name)) {
            connection.createDatabase(database_name);
            //influxDB.createRetentionPolicy("defaultPolicy", database_name, "30d", 1, true); //todo ??
        }
        connection.setDatabase(database_name);
    }

    private static InfluxDB getConnection() {
        if (connection == null)
            connectionProvider = new InfluxDBConnection();
        return connection;
    }

    public static void saveEntry(TrafficInfoEntry entry) {
        Point point = Point.measurement(measurement_name)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("origin", entry.getOrigin())
                .addField("destination", entry.getDest())
                .addField("timeintraffic", entry.getInTraffic())
                .build();
        getConnection().write(point);
    }

    public static List<TrafficInfoEntry> getEntries() {
        Query q = new Query("Select * from " + measurement_name, database_name);
        QueryResult queryResult = getConnection()
                .query(q);

        InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
        return resultMapper.toPOJO(queryResult, TrafficInfoEntry.class);
    }
}