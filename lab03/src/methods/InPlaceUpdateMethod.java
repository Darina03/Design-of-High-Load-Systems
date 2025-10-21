package methods;

import db.DBConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class InPlaceUpdateMethod implements Method {

    @Override
    public void run(int userId, int threads, int incrementsPerThread) throws Exception {
        ExecutorService ex = Executors.newFixedThreadPool(threads);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int t = 0; t < threads; t++) {
            tasks.add(() -> {
                try (Connection conn = DBConfig.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "UPDATE user_counter SET counter = counter + 1 WHERE user_id = ?")) {
                    conn.setAutoCommit(true);
                    for (int i = 0; i < incrementsPerThread; i++) {
                        ps.setInt(1, userId);
                        ps.executeUpdate();
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
        return "In-place update";
    }
}
