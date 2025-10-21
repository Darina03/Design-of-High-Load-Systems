package methods;

import db.DBConfig;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class SerializableNoRetryMethod implements Method {

    @Override
    public void run(int userId, int threads, int incrementsPerThread) throws Exception {
        ExecutorService ex = Executors.newFixedThreadPool(threads);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int t = 0; t < threads; t++) {
            tasks.add(() -> {
                try (Connection conn = DBConfig.getConnection()) {
                    conn.setAutoCommit(false);
                    conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                    PreparedStatement sel = conn.prepareStatement("SELECT counter FROM user_counter WHERE user_id = ?");
                    PreparedStatement upd = conn.prepareStatement("UPDATE user_counter SET counter = ? WHERE user_id = ?");
                    for (int i = 0; i < incrementsPerThread; i++) {
                        sel.setInt(1, userId);
                        try (ResultSet rs = sel.executeQuery()) {
                            rs.next();
                            int c = rs.getInt(1) + 1;
                            upd.setInt(1, c);
                            upd.setInt(2, userId);
                            upd.executeUpdate();
                            conn.commit();
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Serialization error: " + e.getMessage());
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
        return "Serializable update (no retry)";
    }
}
