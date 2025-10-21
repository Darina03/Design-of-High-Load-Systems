package test;

import db.UserCounterRepository;
import methods.Method;

public class CounterTest {

    private final UserCounterRepository repository = new UserCounterRepository();

    public void runTest(Method method, int userId, int threads, int incrementsPerThread) throws Exception {
        System.out.println("\n---------- " + method.getName() + " ----------");
        repository.resetCounter(userId);

        long start = System.nanoTime();
        method.run(userId, threads, incrementsPerThread);
        long elapsed = (System.nanoTime() - start) / 1000000;

        int finalValue = repository.getCounter(userId);
        System.out.printf("Time: %d ms, final counter = %d%n",
                elapsed, finalValue);
    }
}
