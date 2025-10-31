package daryna.service;

import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;


public class W3TimeoutWriteStrategy implements WriteStrategy {

    private final WriteConcern writeConcern;

    public W3TimeoutWriteStrategy(int wtimeoutMs) {
        this.writeConcern = new WriteConcern(3, wtimeoutMs);
    }

    @Override
    public boolean incrementOnce(MongoCollection<Document> collection) {
        MongoCollection<Document> coll = collection.withWriteConcern(writeConcern);
        Document updated = coll.findOneAndUpdate(
                new Document("_id", "likes"),
                new Document("$inc", new Document("count", 1)),
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        );
        return updated != null;
    }

    @Override
    public String name() { return "w3_timeout"; }
}