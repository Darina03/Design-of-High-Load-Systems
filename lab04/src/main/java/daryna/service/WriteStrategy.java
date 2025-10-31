package daryna.service;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

public interface WriteStrategy {

    boolean incrementOnce(MongoCollection<Document> collection);
    String name();
}