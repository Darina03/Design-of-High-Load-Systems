package methods;

import db.DBConfig;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class OptimisticConcurrencyMethod implements Method {

    @Override
    public void run(int userId, int threads, int incrementsPerThread) throws Exception {
        ExecutorService ex = Executors.newFixedThreadPool(threads);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int t = 0; t < threads; t++) {
            tasks.add(() -> {
                try (Connection conn = DBConfig.getConnection()) {
                    conn.setAutoCommit(false);
                    PreparedStatement sel = conn.prepareStatement(
                            "SELECT counter, version FROM user_counter WHERE user_id = ?");
                    PreparedStatement upd = conn.prepareStatement(
                            "UPDATE user_counter SET counter = ?, version = ? WHERE user_id = ? AND version = ?");
                    for (int i = 0; i < incrementsPerThread; i++) {
                        boolean done = false;
                        while (!done) {
                            sel.setInt(1, userId);
                            try (ResultSet rs = sel.executeQuery()) {
                                rs.next();
                                int counter = rs.getInt(1) + 1;
                                int version = rs.getInt(2);
                                upd.setInt(1, counter);
                                upd.setInt(2, version + 1);
                                upd.setInt(3, userId);
                                upd.setInt(4, version);
                                int updated = upd.executeUpdate();
                                if (updated > 0) {
                                    conn.commit();
                                    done = true;
                                } else {
                                    conn.rollback();
                                    Thread.yield();
                                }
                            }
                        }
                    }
                }
                return null;
            });
        }

        ex.invokeAll(tasks);
        ex.shutdown();
        ex.awaitTermination(1, TimeUnit.HOURS);
    }

    @Override
    public String getName() {
        return "Optimistic concurrency control";
    }
}
