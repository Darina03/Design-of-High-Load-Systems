package daryna;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;

import java.util.concurrent.Callable;

public class IncrementWorker implements Callable<Void> {

    private final CqlSession session;
    private final PreparedStatement incrementStmt;
    private final int iterations;
    private final ConsistencyLevel cl;

    public IncrementWorker(CqlSession session, ConsistencyLevel cl, int iterations) {
        this.session = session;
        this.iterations = iterations;
        this.cl = cl;

        this.incrementStmt = session.prepare(
                "UPDATE task3.likes SET like_counter = like_counter + 1 WHERE id='post1';"
        );
    }

    @Override
    public Void call() {
        for (int i = 0; i < iterations; i++) {
            session.execute(incrementStmt.bind().setConsistencyLevel(cl));
        }
        return null;
    }

}
