package daryna;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import daryna.config.AppConfig;
import daryna.config.MongoClientProvider;
import daryna.service.CounterService;
import daryna.service.WriteStrategy;
import daryna.service.WriteStrategyFactory;
import daryna.worker.IncrementWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws Exception {
        String mode = args.length > 0 ? args[0] : "w1";
        int clients = args.length > 1 ? Integer.parseInt(args[1]) : 10;
        int increments = args.length > 2 ? Integer.parseInt(args[2]) : 10_000;

        AppConfig config = AppConfig.defaultConfig();
        try (MongoClient client = MongoClientProvider.create(config)) {
            MongoDatabase db = client.getDatabase(config.getDatabase());


            CounterService counterService = new CounterService(db, config);
            counterService.initCounter();

            WriteStrategy strategy = WriteStrategyFactory.create(mode);

            ExecutorService executor = Executors.newFixedThreadPool(clients);
            List<Callable<Integer>> tasks = new ArrayList<>();
            for (int i = 0; i < clients; i++) {
                tasks.add(new IncrementWorker(counterService, strategy, increments));
            }

            long start = System.currentTimeMillis();
            List<Future<Integer>> results = executor.invokeAll(tasks);
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);
            long end = System.currentTimeMillis();

            int total = results.stream().mapToInt(f -> {
                try { return f.get(); }
                catch (Exception e) { return 0; }
            }).sum();

            System.out.printf("Mode=%s; Clients=%d; Increments per each=%d%n", mode, clients, increments);
            System.out.printf("Total successful increments = %d%n", total);
            System.out.printf("Elapsed ms = %d%n", (end - start));

            int finalValue = counterService.readCountMajority();
            System.out.printf("Final value (readConcern MAJORITY) = %d. Expected = %d%n",
                    finalValue, clients * increments);
        }
    }}