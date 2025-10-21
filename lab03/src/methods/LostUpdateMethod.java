package methods;

import db.DBConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class LostUpdateMethod implements Method {

    @Override
    public void run(int userId, int threads, int incrementsPerThread) throws Exception {
        ExecutorService ex = Executors.newFixedThreadPool(threads);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int t = 0; t < threads; t++) {
            tasks.add(() -> {
                try (Connection conn = DBConfig.getConnection()) {
                    conn.setAutoCommit(false);
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
        return "Lost-update";
    }
}
