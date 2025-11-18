package daryna;

import com.datastax.oss.driver.api.core.CqlSession;
import lombok.Getter;

import java.net.InetSocketAddress;

@Getter
class CassandraManager {

    private final CqlSession session;

    public CassandraManager(String host, int port) {
        this.session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(host, port))
                .withLocalDatacenter("datacenter1")
                .build();
    }


    public void close() {
        session.close();
    }


    public void createKeyspace() {
        session.execute("""
                CREATE KEYSPACE IF NOT EXISTS task3
                WITH replication = {
                  'class': 'SimpleStrategy',
                  'replication_factor': 3
                };
                """);
    }


    public void createTable() {
        session.execute("""
                CREATE TABLE IF NOT EXISTS task3.likes (
                    id TEXT PRIMARY KEY,
                    like_counter COUNTER
                );
                """);
    }

    public void resetCounter() {
        session.execute("TRUNCATE task3.likes;");    }
}