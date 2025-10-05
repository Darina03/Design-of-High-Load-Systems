package daryna.lab02;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class HazelcastCounter {

    private static final String KEY = "counter-lab02";
    private static final int THREADS = 10;
    private static final int INCREMENTS_PER_THREAD = 10000;
    private static final int EXPECTED = THREADS * INCREMENTS_PER_THREAD;

    public static void main(String[] args) throws Exception {
        String addrs = System.getenv().getOrDefault("HZ_ADDRESSES", "hz1:5701,hz2:5701,hz3:5701");
        ClientConfig cfg = new ClientConfig();
        cfg.setClusterName("lab02-cluster");
        for (String addr : addrs.split(",")) {
            cfg.getNetworkConfig().addAddress(addr.trim());
        }
        HazelcastInstance client = HazelcastClient.newHazelcastClient(cfg);

        System.out.println("Client has been connected to the cluster successfully.");

        runNoLock(client);

        runPessimisticLock(client);

        runOptimisticLock(client);

        runAtomicLong(client);

        client.shutdown();
    }

    private static void runNoLock(HazelcastInstance client) throws InterruptedException {
        System.out.println("\n---------------- No lock ----------------");
        IMap<String, Integer> map = client.getMap("map-counter-lab02");
        map.put(KEY, 0);

        long start = System.nanoTime();
        run(() -> {
            for (int i = 0; i < INCREMENTS_PER_THREAD; i++) {
                Integer current = map.get(KEY);
                if (current == null) current = 0;
                map.put(KEY, current + 1);
            }
        });
        long elapsed = (System.nanoTime() - start) / 1000000;
        Integer finalValue = map.get(KEY);
        System.out.printf("Time: %d ms, final value: %d, expected: %d", elapsed, finalValue, EXPECTED);
    }

    private static void runPessimisticLock(HazelcastInstance client) throws InterruptedException {
        System.out.println("\n---------------- Pessimistic lock ----------------");
        IMap<String, Integer> map = client.getMap("map-counter-lock-lab02");
        map.put(KEY, 0);

        long start = System.nanoTime();
        run(() -> {
            for (int i = 0; i < INCREMENTS_PER_THREAD; i++) {
                map.lock(KEY);
                try {
                    Integer current = map.get(KEY);
                    if (current == null) current = 0;
                    map.put(KEY, current + 1);
                } finally {
                    map.unlock(KEY);
                }
            }
        });
        long elapsed = (System.nanoTime() - start) / 1000000;
        Integer finalValue = map.get(KEY);
        System.out.printf("Time: %d ms, final value: %d, expected: %d", elapsed, finalValue, EXPECTED);
    }

    private static void runOptimisticLock(HazelcastInstance client) throws InterruptedException {
        System.out.println("\n---------------- Optimistic lock ----------------");
        IMap<String, Integer> map = client.getMap("map-counter-optimistic-lab02");
        map.put(KEY, 0);

        long start = System.nanoTime();
        run(() -> {
            for (int i = 0; i < INCREMENTS_PER_THREAD; i++) {
                while (true) {
                    Integer old = map.get(KEY);
                    if (old == null) {
                        Integer prev = map.putIfAbsent(KEY, 1);
                        if (prev == null) break;
                        else continue;
                    } else {
                        boolean success = map.replace(KEY, old, old + 1);
                        if (success) break;
                    }
                }
            }
        });
        long elapsed = (System.nanoTime() - start) / 1000000;
        Integer finalValue = map.get(KEY);
        System.out.printf("Time: %d ms, final value: %d, expected: %d", elapsed, finalValue, EXPECTED);
    }

    private static void runAtomicLong(HazelcastInstance client) throws InterruptedException {
        System.out.println("\n---------------- IAtomicLong (CP Subsystem) ----------------");
        IAtomicLong atomic = client.getCPSubsystem().getAtomicLong("atomic-counter-lab02");
        atomic.set(0);

        long start = System.nanoTime();
        run(() -> {
            for (int i = 0; i < INCREMENTS_PER_THREAD; i++) {
                atomic.incrementAndGet();
            }
        });
        long elapsed = (System.nanoTime() - start) / 1000000;
        long finalValue = atomic.get();
        System.out.printf("Time: %d ms, final value: %d, expected: %d", elapsed, finalValue, (long) EXPECTED);
    }

    private static void run(Runnable task) throws InterruptedException {
        ExecutorService ex = Executors.newFixedThreadPool(THREADS);
        List<Future<?>> futures = new ArrayList<>();
        for (int t = 0; t < THREADS; t++) {
            futures.add(ex.submit(task));
        }
        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        ex.shutdown();
        ex.awaitTermination(1, TimeUnit.MINUTES);
    }
}

