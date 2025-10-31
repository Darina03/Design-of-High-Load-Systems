package daryna.config;

public class AppConfig {

    private final String connectionString;
    private final String database;
    private final String collection;

    public AppConfig(String connectionString, String database, String collection) {
        this.connectionString = connectionString;
        this.database = database;
        this.collection = collection;
    }

    public String getConnectionString() { return connectionString; }
    public String getDatabase() { return database; }
    public String getCollection() { return collection; }

    public static AppConfig defaultConfig() {
        return new AppConfig(
                "mongodb://mongo1:27017,mongo2:27018,mongo3:27019/?replicaSet=rs_lab04",
                "lab04_db",
                "likes_counter"
        );
    }
}
