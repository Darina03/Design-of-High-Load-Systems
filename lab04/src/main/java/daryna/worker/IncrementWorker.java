package daryna.worker;


import com.mongodb.client.MongoCollection;
import daryna.service.CounterService;
import daryna.service.WriteStrategy;
import org.bson.Document;

import java.util.concurrent.Callable;

public class IncrementWorker implements Callable<Integer> {

    private final CounterService counterService;
    private final WriteStrategy strategy;
    private final int increments;

    public IncrementWorker(CounterService counterService, WriteStrategy strategy, int increments) {
        this.counterService = counterService;
        this.strategy = strategy;
        this.increments = increments;
    }

    @Override
    public Integer call() {
        MongoCollection<Document> coll = counterService.getCollection();
        int localSuccess = 0;
        for (int i = 0; i < increments; i++) {
            try {
                boolean ok = strategy.incrementOnce(coll);
                if (ok) localSuccess++;
            } catch (Exception e) {
                System.err.printf("Worker [%s] write failed: %s%n", strategy.name(), e.getMessage());
            }
        }
        return localSuccess;
    }
}