package daryna.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoClientProvider {

    private MongoClientProvider() {}

    public static MongoClient create(AppConfig config) {
        ConnectionString cs = new ConnectionString(config.getConnectionString());
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(cs)
                .build();
        return MongoClients.create(settings);
    }
}
