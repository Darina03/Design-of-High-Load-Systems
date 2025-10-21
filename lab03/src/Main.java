import test.CounterTest;
import methods.*;

public class Main {

    public static void main(String[] args) throws Exception {
        int userId = 1;
        int threads = 10;
        int increments = 10000;

        CounterTest test = new CounterTest();

        test.runTest(new LostUpdateMethod(), userId, threads, increments);
        test.runTest(new SerializableNoRetryMethod(), userId, threads, increments);
        test.runTest(new SerializableWithRetryMethod(), userId, threads, increments);
        test.runTest(new InPlaceUpdateMethod(), userId, threads, increments);
        test.runTest(new RowLevelLockingMethod(), userId, threads, increments);
        test.runTest(new OptimisticConcurrencyMethod(), userId, threads, increments);

    }
}
