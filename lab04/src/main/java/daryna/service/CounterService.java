package daryna.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.ReadConcern;
import daryna.config.AppConfig;
import lombok.Getter;
import org.bson.Document;

@Getter
public class CounterService {
    private final MongoCollection<Document> collection;
    private final MongoDatabase database;

    public CounterService(MongoDatabase database, AppConfig config) {
        this.database = database;
        this.collection = database.getCollection(config.getCollection());
    }

    public void initCounter() {
        collection.drop();
        database.createCollection(collection.getNamespace().getCollectionName());
        collection.insertOne(new Document("_id", "likes").append("count", 0));
    }

    public int readCountMajority() {
        Document doc = collection.withReadConcern(ReadConcern.MAJORITY)
                .find(Filters.eq("_id", "likes"))
                .first();
        if (doc == null) return -1;
        return doc.getInteger("count", -1);
    }
}
