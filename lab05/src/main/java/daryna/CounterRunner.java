package daryna;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class CounterRunner {

    private final CassandraManager manager;



    public void runTest(ConsistencyLevel cl) throws Exception {
        System.out.println("\n---------- CL = " + cl + " ----------");

        manager.resetCounter();

        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            tasks.add(new IncrementWorker(
                    manager.getSession(),
                    cl,
                    10_000
            ));
        }

        Instant start = Instant.now();

        executor.invokeAll(tasks);
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        Instant end = Instant.now();
        long millis = Duration.between(start, end).toMillis();

        long finalValue = getCounterValue();

        System.out.println("Execution time: " + millis + " ms");
        System.out.println("Elapsed time: " + finalValue);
    }

    private long getCounterValue() {
        ResultSet rs = manager.getSession().execute("SELECT like_counter FROM task3.likes WHERE id='post1';");
        Row row = rs.one();
        return row == null ? 0 : row.getLong("like_counter");
    }
}
