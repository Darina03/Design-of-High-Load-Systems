package daryna;


import com.datastax.oss.driver.api.core.ConsistencyLevel;

public class Main {
    public static void main(String[] args) throws Exception {
        CassandraManager manager = new CassandraManager("127.0.0.1", 9042);

        manager.createKeyspace();
        manager.createTable();

        CounterRunner runner = new CounterRunner(manager);

        runner.runTest(ConsistencyLevel.ONE);
        runner.runTest(ConsistencyLevel.QUORUM);

        manager.close();
    }
}